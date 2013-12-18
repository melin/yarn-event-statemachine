/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.ustcinfo.yarn.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ustcinfo.yarn.service.CompositeService;
import com.ustcinfo.yarn.service.Service;

/**
 * Create on @2013-12-11 @下午2:45:02 
 * @author bsli@ustcinfo.com
 */
@SuppressWarnings("unchecked")
public class SimpleMRAppMaster extends CompositeService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SimpleMRAppMaster.class);
	
	private Dispatcher dispatcher;
	private String jobId;
	private int taskNumber;
	private String[] taskIds;
	
	public SimpleMRAppMaster(String name, String jobId, int taskNumber) {
		super(name);
		this.jobId = jobId;
		this.taskNumber = taskNumber;
		taskIds = new String[taskNumber];
		for(int i=0; i<taskNumber; i++) {
			taskIds[i] = new String(jobId + "_task_" + i);
		}
	}

	public void init() {
		dispatcher = new AsyncDispatcher();
		dispatcher.register(JobEventType.class, new JobEventDispatcher());
		dispatcher.register(TaskEventType.class, new TaskEventDispatcher());
		addService((Service) dispatcher);
		super.init();
	}
	
	public Dispatcher getDispatcher() {
		return dispatcher;
	}

	private class JobEventDispatcher implements EventHandler<JobEvent> {
		
		@Override
		public void handle(JobEvent event) {
			if(event.getType() == JobEventType.JOB_KILL) {
				LOGGER.info("Receive JOB_KILL event, Killing all the tasks");
				for(int i=0; i<taskNumber; i++) {
					dispatcher.getEventHandler().handle(new TaskEvent(taskIds[i], TaskEventType.T_KILL));
				}
			} else if(event.getType() == JobEventType.JOB_INIT) {
				LOGGER.info("Receive JOB_INIT event, scheduling tasks");
				for(int i=0; i<taskNumber; i++) {
					dispatcher.getEventHandler().handle(new TaskEvent(taskIds[i], TaskEventType.T_SCHEDULE));
				}
			}
		}
		
	}
	
	private class TaskEventDispatcher implements EventHandler<TaskEvent> {

		@Override
		public void handle(TaskEvent event) {
			if(event.getType() == TaskEventType.T_KILL) {
				LOGGER.info("Receive T_KILL event of task " + event.getTaskId());
			} else if(event.getType() == TaskEventType.T_SCHEDULE) {
				LOGGER.info("Receive T_SCHEDULE event of taks " + event.getTaskId());
			}
		}
		
	}
}
