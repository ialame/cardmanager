package com.pcagrade.retriever.scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {
	
	private Set<ScheduledTask> tasks = new HashSet<>();
	
	public List<String> getTasks() {
		return tasks.stream()
				.map(Object::toString)
				.toList();
	}
	
	public void loadTasks(Collection<ScheduledTask> tasks) {
		this.tasks.addAll(tasks);
	}
	
	public void run(String id) {
		tasks.stream()
				.filter(t -> t.toString().equals(id))
				.findAny()
				.ifPresent(t -> t.getTask().getRunnable().run());
	}

}
