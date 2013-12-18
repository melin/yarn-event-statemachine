/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.ustcinfo.yarn.service;

/**
 * Create on @2013-12-11 @下午2:30:54
 * 
 * @author bsli@ustcinfo.com
 */
@SuppressWarnings("serial")
public class ServiceException extends RuntimeException {
	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}