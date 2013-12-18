/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */    
package com.ustcinfo.yarn.event;

/**
 * Create on @2013-12-11 @下午3:01:03 
 * @author bsli@ustcinfo.com
 */
public class SimpleMRAppMasterTest {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		String jobId = "job_20131211_12";
		SimpleMRAppMaster appMaster = new SimpleMRAppMaster("Simple MRAppMaster", jobId, 5);
		appMaster.init();
		appMaster.start();
		appMaster.getDispatcher().getEventHandler().handle(new JobEvent(jobId, JobEventType.JOB_KILL));
		appMaster.getDispatcher().getEventHandler().handle(new JobEvent(jobId, JobEventType.JOB_INIT));
	}
}
