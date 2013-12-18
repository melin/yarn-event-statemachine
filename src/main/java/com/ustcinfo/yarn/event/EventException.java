/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.ustcinfo.yarn.event;

/**
 * Create on @2013-12-11 @下午2:30:54
 * 
 * @author bsli@ustcinfo.com
 */
@SuppressWarnings("serial")
public class EventException extends RuntimeException {
	public EventException(Throwable cause) {
		super(cause);
	}

	public EventException(String message) {
		super(message);
	}

	public EventException(String message, Throwable cause) {
		super(message, cause);
	}
}