package com.pcagrade.retriever.scheduler;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
@EnableAsync
public class SchedulerConfig implements SchedulingConfigurer, SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	SchedulerService schedulerService;
	
	@Autowired
	ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;
	
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	schedulerService.loadTasks(taskRegistrar.getScheduledTasks());
    }
    
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		loadProcessorTasks();
	}

	@Override
	public void afterSingletonsInstantiated() {
		loadProcessorTasks();
	}
    
    private void loadProcessorTasks() {
    	schedulerService.loadTasks(scheduledAnnotationBeanPostProcessor.getScheduledTasks());
    }
}