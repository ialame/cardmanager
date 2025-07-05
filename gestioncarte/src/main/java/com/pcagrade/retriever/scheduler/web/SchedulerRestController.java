package com.pcagrade.retriever.scheduler.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pcagrade.retriever.scheduler.SchedulerService;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerRestController {

	@Autowired
	private SchedulerService schedulerService;
	
	@GetMapping("/tasks")
	public List<String> getTasks() {
		return schedulerService.getTasks();
	}
	
	@PostMapping("/tasks/{id}/run")
	public void runTask(@PathVariable String id) {
		schedulerService.run(id);
	}
	
}
