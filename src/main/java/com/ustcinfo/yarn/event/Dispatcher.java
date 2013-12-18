/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.ustcinfo.yarn.event;


/**
 * Create on @2013-12-11 @下午2:12:13
 * 
 * @author bsli@ustcinfo.com
 */
@SuppressWarnings("rawtypes")
public interface Dispatcher {

	EventHandler getEventHandler();

	void register(Class<? extends Enum> eventType, EventHandler handler);
}
