/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.ustcinfo.yarn.event;

/**
 * Create on @2013-12-11 @下午2:36:48 
 * @author bsli@ustcinfo.com
 */
public class TaskEvent extends AbstractEvent<TaskEventType> {
	
	private String taskId;

	public TaskEvent(String taskId, TaskEventType type) {
		super(type);
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}

}
