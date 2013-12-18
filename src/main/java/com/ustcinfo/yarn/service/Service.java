/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.ustcinfo.yarn.service;

/**
 * Service LifeCycle.
 */
public interface Service {

	/**
	 * Service states
	 */
	public enum STATE {
		/** Constructed but not initialized */
		NOTINITED,

		/** Initialized but not started or stopped */
		INITED,

		/** started and not stopped */
		STARTED,

		/** stopped. No further state transitions are permitted */
		STOPPED
	}

	/**
	 * Initialize the service.
	 * 
	 * The transition must be from {@link STATE#NOTINITED} to
	 * {@link STATE#INITED} unless the operation failed and an exception was
	 * raised.
	 * 
	 * @param config
	 *            the configuration of the service
	 */
	void init(/*Configuration config*/);

	/**
	 * Start the service.
	 * 
	 * The transition should be from {@link STATE#INITED} to
	 * {@link STATE#STARTED} unless the operation failed and an exception was
	 * raised.
	 */

	void start();

	/**
	 * Stop the service.
	 * 
	 * This operation must be designed to complete regardless of the initial
	 * state of the service, including the state of all its internal fields.
	 */
	void stop();

	/**
	 * Register an instance of the service state change events.
	 * 
	 * @param listener
	 *            a new listener
	 */
	void register(ServiceStateChangeListener listener);

	/**
	 * Unregister a previously instance of the service state change events.
	 * 
	 * @param listener
	 *            the listener to unregister.
	 */
	void unregister(ServiceStateChangeListener listener);

	/**
	 * Get the name of this service.
	 * 
	 * @return the service name
	 */
	String getName();

	/**
	 * Get the current service state
	 * 
	 * @return the state of the service
	 */
	STATE getServiceState();

	/**
	 * Get the service start time
	 * 
	 * @return the start time of the service. This will be zero if the service
	 *         has not yet been started.
	 */
	long getStartTime();
}