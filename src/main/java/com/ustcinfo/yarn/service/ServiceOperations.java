/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.ustcinfo.yarn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceOperations {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

	private ServiceOperations() {
	}

	/**
	 * Verify that that a service is in a given state.
	 * 
	 * @param state
	 *            the actual state a service is in
	 * @param expectedState
	 *            the desired state
	 * @throws IllegalStateException
	 *             if the service state is different from the desired state
	 */
	public static void ensureCurrentState(Service.STATE state,
			Service.STATE expectedState) {
		if (state != expectedState) {
			throw new IllegalStateException("For this operation, the "
					+ "current service state must be " + expectedState
					+ " instead of " + state);
		}
	}

	/**
	 * Initialize a service.
	 * <p/>
	 * The service state is checked <i>before</i> the operation begins. This
	 * process is <i>not</i> thread safe.
	 * 
	 * @param service
	 *            a service that must be in the state
	 *            {@link Service.STATE#NOTINITED}
	 * @param configuration
	 *            the configuration to initialize the service with
	 * @throws RuntimeException
	 *             on a state change failure
	 * @throws IllegalStateException
	 *             if the service is in the wrong state
	 */

	public static void init(Service service/* , Configuration configuration */) {
		Service.STATE state = service.getServiceState();
		ensureCurrentState(state, Service.STATE.NOTINITED);
		service.init();
	}

	/**
	 * Start a service.
	 * <p/>
	 * The service state is checked <i>before</i> the operation begins. This
	 * process is <i>not</i> thread safe.
	 * 
	 * @param service
	 *            a service that must be in the state
	 *            {@link Service.STATE#INITED}
	 * @throws RuntimeException
	 *             on a state change failure
	 * @throws IllegalStateException
	 *             if the service is in the wrong state
	 */

	public static void start(Service service) {
		Service.STATE state = service.getServiceState();
		ensureCurrentState(state, Service.STATE.INITED);
		service.start();
	}

	/**
	 * Initialize then start a service.
	 * <p/>
	 * The service state is checked <i>before</i> the operation begins. This
	 * process is <i>not</i> thread safe.
	 * 
	 * @param service
	 *            a service that must be in the state
	 *            {@link Service.STATE#NOTINITED}
	 * @param configuration
	 *            the configuration to initialize the service with
	 * @throws RuntimeException
	 *             on a state change failure
	 * @throws IllegalStateException
	 *             if the service is in the wrong state
	 */
	public static void deploy(Service service/* , Configuration configuration */) {
		init(service);
		start(service);
	}

	/**
	 * Stop a service.
	 * <p/>
	 * Do nothing if the service is null or not in a state in which it can
	 * be/needs to be stopped.
	 * <p/>
	 * The service state is checked <i>before</i> the operation begins. This
	 * process is <i>not</i> thread safe.
	 * 
	 * @param service
	 *            a service or null
	 */
	public static void stop(Service service) {
		if (service != null) {
			Service.STATE state = service.getServiceState();
			if (state == Service.STATE.STARTED) {
				service.stop();
			}
		}
	}

	/**
	 * Stop a service; if it is null do nothing. Exceptions are caught and
	 * logged at warn level. (but not Throwables). This operation is intended to
	 * be used in cleanup operations
	 * 
	 * @param service
	 *            a service; may be null
	 * @return any exception that was caught; null if none was.
	 */
	public static Exception stopQuietly(Service service) {
		try {
			stop(service);
		} catch (Exception e) {
			LOGGER.warn("When stopping the service " + service.getName()
					+ " : " + e, e);
			return e;
		}
		return null;
	}
}