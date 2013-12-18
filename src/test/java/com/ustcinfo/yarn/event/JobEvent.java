/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.ustcinfo.yarn.event;

/**
 * Create on @2013-12-11 @下午2:43:19 
 * @author bsli@ustcinfo.com
 */
public class JobEvent extends AbstractEvent<JobEventType> {
	
	private String jobId;

	public JobEvent(String jobId, JobEventType type) {
		super(type);
		this.jobId = jobId;
	}

	public String getJobId() {
		return jobId;
	}
	
}
