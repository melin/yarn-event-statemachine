/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.ustcinfo.yarn.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create on @2013-12-11 @下午2:20:16
 * 
 * @author bsli@ustcinfo.com
 */
public abstract class AbstractService implements Service {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

	/**
	 * Service state: initially {@link STATE#NOTINITED}.
	 */
	private STATE state = STATE.NOTINITED;

	/**
	 * Service name.
	 */
	private final String name;
	/**
	 * Service start time. Will be zero until the service is started.
	 */
	private long startTime;

	/**
	 * List of state change listeners; it is final to ensure that it will never
	 * be null.
	 */
	private List<ServiceStateChangeListener> listeners = new ArrayList<ServiceStateChangeListener>();

	/**
	 * Construct the service.
	 * 
	 * @param name
	 *            service name
	 */
	public AbstractService(String name) {
		this.name = name;
	}

	@Override
	public synchronized STATE getServiceState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException
	 *             if the current service state does not permit this action
	 */
	@Override
	public synchronized void init(/*Configuration conf*/) {
		ensureCurrentState(STATE.NOTINITED);
		changeState(STATE.INITED);
		LOGGER.info("Service:" + getName() + " is inited.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException
	 *             if the current service state does not permit this action
	 */
	@Override
	public synchronized void start() {
		startTime = System.currentTimeMillis();
		ensureCurrentState(STATE.INITED);
		changeState(STATE.STARTED);
		LOGGER.info("Service:" + getName() + " is started.");
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalStateException
	 *             if the current service state does not permit this action
	 */
	@Override
	public synchronized void stop() {
		if (state == STATE.STOPPED || state == STATE.INITED
				|| state == STATE.NOTINITED) {
			// already stopped, or else it was never
			// started (eg another service failing canceled startup)
			return;
		}
		ensureCurrentState(STATE.STARTED);
		changeState(STATE.STOPPED);
		LOGGER.info("Service:" + getName() + " is stopped.");
	}

	@Override
	public synchronized void register(ServiceStateChangeListener l) {
		listeners.add(l);
	}

	@Override
	public synchronized void unregister(ServiceStateChangeListener l) {
		listeners.remove(l);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Verify that that a service is in a given state.
	 * 
	 * @param currentState
	 *            the desired state
	 * @throws IllegalStateException
	 *             if the service state is different from the desired state
	 */
	private void ensureCurrentState(STATE currentState) {
		ServiceOperations.ensureCurrentState(state, currentState);
	}

	/**
	 * Change to a new state and notify all listeners. This is a private method
	 * that is only invoked from synchronized methods, which avoid having to
	 * clone the listener list. It does imply that the state change listener
	 * methods should be short lived, as they will delay the state transition.
	 * 
	 * @param newState
	 *            new service state
	 */
	private void changeState(STATE newState) {
		state = newState;
		// notify listeners
		for (ServiceStateChangeListener l : listeners) {
			l.stateChanged(this);
		}
	}
}