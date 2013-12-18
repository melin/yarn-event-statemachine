/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.ustcinfo.yarn.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create on @2013-12-11 @下午2:29:10
 * 
 * @author bsli@ustcinfo.com
 */
public class CompositeService extends AbstractService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompositeService.class);

	private List<Service> serviceList = new ArrayList<Service>();

	public CompositeService(String name) {
		super(name);
	}

	public Collection<Service> getServices() {
		return Collections.unmodifiableList(serviceList);
	}

	protected synchronized void addService(Service service) {
		serviceList.add(service);
	}

	protected synchronized boolean removeService(Service service) {
		return serviceList.remove(service);
	}

	public synchronized void init(/*Configuration conf*/) {
		for (Service service : serviceList) {
			service.init();
		}
		super.init();
	}

	public synchronized void start() {
		int i = 0;
		try {
			for (int n = serviceList.size(); i < n; i++) {
				Service service = serviceList.get(i);
				service.start();
			}
			super.start();
		} catch (Throwable e) {
			LOGGER.error("Error starting services " + getName(), e);
			stop(i);
			throw new ServiceException("Failed to Start " + getName(), e);
		}

	}

	public synchronized void stop() {
		if (this.getServiceState() == STATE.STOPPED) {
			// The base composite-service is already stopped, don't do anything
			// again.
			return;
		}
		if (serviceList.size() > 0) {
			stop(serviceList.size() - 1);
		}
		super.stop();
	}

	private synchronized void stop(int numOfServicesStarted) {
		// stop in reserve order of start
		for (int i = numOfServicesStarted; i >= 0; i--) {
			Service service = serviceList.get(i);
			try {
				service.stop();
			} catch (Throwable t) {
				LOGGER.info("Error stopping " + service.getName(), t);
			}
		}
	}

	/**
	 * JVM Shutdown hook for CompositeService which will stop the give
	 * CompositeService gracefully in case of JVM shutdown.
	 */
	public static class CompositeServiceShutdownHook implements Runnable {

		private CompositeService compositeService;

		public CompositeServiceShutdownHook(CompositeService compositeService) {
			this.compositeService = compositeService;
		}

		@Override
		public void run() {
			try {
				// Stop the Composite Service
				compositeService.stop();
			} catch (Throwable t) {
				LOGGER.info("Error stopping " + compositeService.getName(), t);
			}
		}
	}

}