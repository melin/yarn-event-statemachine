/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.ustcinfo.yarn.event;

/**
 * Create on @2013-12-11 @下午2:11:04
 * 
 * @author bsli@ustcinfo.com
 */
public interface Event<TYPE extends Enum<TYPE>> {

	TYPE getType();

	long getTimestamp();

	String toString();
}
