/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.ustcinfo.yarn.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ustcinfo.yarn.service.CompositeService;

/**
 * Create on @2013-12-11 @下午2:14:00
 * 
 * @author bsli@ustcinfo.com
 */
@SuppressWarnings("rawtypes")
public class AsyncDispatcher extends CompositeService implements Dispatcher {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AsyncDispatcher.class);

	private final BlockingQueue<Event> eventQueue;
	private volatile boolean stopped = false;

	private Thread eventHandlingThread;
	protected final Map<Class<? extends Enum>, EventHandler> eventDispatchers;

	public AsyncDispatcher() {
		this(new LinkedBlockingQueue<Event>());
	}

	public AsyncDispatcher(BlockingQueue<Event> eventQueue) {
		super("Dispatcher");
		this.eventQueue = eventQueue;
		this.eventDispatchers = new HashMap<Class<? extends Enum>, EventHandler>();
	}

	Runnable createThread() {
		return new Runnable() {
			@Override
			public void run() {
				while (!stopped && !Thread.currentThread().isInterrupted()) {
					Event event;
					try {
						event = eventQueue.take();
					} catch (InterruptedException ie) {
						if (!stopped) {
							LOGGER.warn("AsyncDispatcher thread interrupted",
									ie);
						}
						return;
					}
					if (event != null) {
						dispatch(event);
					}
				}
			}
		};
	}

	@Override
	public synchronized void init(/* Configuration conf */) {
		super.init();
	}

	@Override
	public void start() {
		// start all the components
		super.start();
		eventHandlingThread = new Thread(createThread());
		eventHandlingThread.setName("AsyncDispatcher event handler");
		eventHandlingThread.start();
	}

	@Override
	public void stop() {
		stopped = true;
		if (eventHandlingThread != null) {
			eventHandlingThread.interrupt();
			try {
				eventHandlingThread.join();
			} catch (InterruptedException ie) {
				LOGGER.warn("Interrupted Exception while stopping", ie);
			}
		}

		// stop all the components
		super.stop();
	}

	@SuppressWarnings("unchecked")
	protected void dispatch(Event event) {
		// all events go thru this loop
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Dispatching the event " + event.getClass().getName()
					+ "." + event.toString());
		}

		Class<? extends Enum> type = event.getType().getDeclaringClass();

		try {
			eventDispatchers.get(type).handle(event);
		} catch (Throwable t) {
			LOGGER.error("Error in dispatcher thread", t);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void register(Class<? extends Enum> eventType, EventHandler handler) {
		/* check to see if we have a listener registered */
		EventHandler<Event> registeredHandler = (EventHandler<Event>) eventDispatchers
				.get(eventType);
		LOGGER.info("Registering " + eventType + " for " + handler.getClass());
		if (registeredHandler == null) {
			eventDispatchers.put(eventType, handler);
		} else if (!(registeredHandler instanceof MultiListenerHandler)) {
			/*
			 * for multiple listeners of an event add the multiple listener
			 * handler
			 */
			MultiListenerHandler multiHandler = new MultiListenerHandler();
			multiHandler.addHandler(registeredHandler);
			multiHandler.addHandler(handler);
			eventDispatchers.put(eventType, multiHandler);
		} else {
			/* already a multilistener, just add to it */
			MultiListenerHandler multiHandler = (MultiListenerHandler) registeredHandler;
			multiHandler.addHandler(handler);
		}
	}

	@Override
	public EventHandler getEventHandler() {
		return new GenericEventHandler();
	}

	class GenericEventHandler implements EventHandler<Event> {
		public void handle(Event event) {
			/* all this method does is enqueue all the events onto the queue */
			int qSize = eventQueue.size();
			if (qSize != 0 && qSize % 1000 == 0) {
				LOGGER.info("Size of event-queue is " + qSize);
			}
			int remCapacity = eventQueue.remainingCapacity();
			if (remCapacity < 1000) {
				LOGGER.warn("Very low remaining capacity in the event-queue: "
						+ remCapacity);
			}
			try {
				eventQueue.put(event);
			} catch (InterruptedException e) {
				if (!stopped) {
					LOGGER.warn("AsyncDispatcher thread interrupted", e);
				}
				throw new EventException(e);
			}
		};
	}

	/**
	 * Multiplexing an event. Sending it to different handlers that are
	 * interested in the event.
	 * 
	 * @param <T>
	 *            the type of event these multiple handlers are interested in.
	 */
	static class MultiListenerHandler implements EventHandler<Event> {
		List<EventHandler<Event>> listofHandlers;

		public MultiListenerHandler() {
			listofHandlers = new ArrayList<EventHandler<Event>>();
		}

		@Override
		public void handle(Event event) {
			for (EventHandler<Event> handler : listofHandlers) {
				handler.handle(event);
			}
		}

		void addHandler(EventHandler<Event> handler) {
			listofHandlers.add(handler);
		}

	}
}
