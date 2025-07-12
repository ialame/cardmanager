package com.pcagrade.retriever.alert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcagrade.retriever.PCAException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;

@Service
public class AlertService {

	private static final Logger LOGGER = LogManager.getLogger(AlertService.class);
	
	@Autowired
	private AlertRepository alertRepository;
	
    @Autowired
    private PlatformTransactionManager transactionManager;

	@Autowired
	private ObjectMapper mapper;
	
	private final List<Runnable> runnables = new ArrayList<>();
	
	public void prepare(String type, Map<String, ?> params, BooleanSupplier prediacte) {
		synchronized (this) {
			runnables.add(executeInTransaction(() -> {
				try {
					Alert alert = findOrCreate(type, params);
		
					if (prediacte.getAsBoolean()) {
						raise(alert);
					} else {
						close(alert);
					}
				} catch (Exception e) {
					LOGGER.error(() -> "Error while processing alert for type: " + type, e);
				}
			}));
		}
	}
	
	@Scheduled(cron = "${retriever.schedule.evaluate-alerts}")
	public void evaluate() {
		List<Runnable> list;
		
		synchronized (this) {
			list = List.copyOf(runnables);
			runnables.clear();
		}
		if (!list.isEmpty()) {
			LOGGER.info("Alerts evaluation started with {} alerts.", list::size);
			list.parallelStream().forEach(Runnable::run);
			LOGGER.info("Alerts evaluation finished.");
		}
	}
		
	public Alert findOrCreate(String type, Map<String, ?> params) {
		String sParams = serializeParams(params);
		
		return findAlert(type, sParams).orElseGet(() -> doCreate(type, sParams));
	}
	
	public final boolean exists(String type, Map<String, ?> params) {
		return exists(type, serializeParams(params));
	}
	
	public final void raise(Alert alert) {
		Integer id = alert.getId();
		
		if (id == null  || id == 0 || !alert.getStatus().isOpen()) {
			alert.open();
			alert = alertRepository.save(alert);
			LOGGER.debug("Alert raised of type: {}", alert::getType);
		}
	}
	
	public final void close(Alert alert) {
		Integer id = alert.getId();
		
		if (id != null && id != 0 && alert.getStatus() != AlertStatus.CLOSED) {
			alert.setStatus(AlertStatus.CLOSED);
			alert = alertRepository.save(alert);
			LOGGER.debug("Alert closed of type: {}", alert::getType);
		}
	}
	
	private Alert doCreate(String type, String params) {
		Alert alert = new Alert();
		
		alert.setType(type);
		alert.setParams(params);
		alert.setStatus(AlertStatus.OPEN);
		return alert;
	}
	
	private boolean exists(String type, String params) {
		return findAlert(type, params).isPresent();
	}
	
	private Optional<Alert> findAlert(String type, String params) {
		return alertRepository.findFirstByTypeIgnoreCaseAndParams(type, params);
	}
	
	private String serializeParams(Map<String, ?> params) {
		try {
			return mapper.writeValueAsString(params);
		} catch (JsonProcessingException e) {
			throw new PCAException("Error while serializing alert params", e);
		}
	}
	
	private Runnable executeInTransaction(Runnable action) throws TransactionException {
		return () -> new TransactionTemplate(transactionManager).execute(s -> {
			action.run();
			return null;
		});
	}
	
}
