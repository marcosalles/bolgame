package com.marcosalles.bolgame.event;

import com.marcosalles.bolgame.model.Event;
import com.marcosalles.bolgame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class EventListener implements ApplicationListener<Event> {

	@Autowired
	private SimpMessagingTemplate template;
	@Autowired
	private GameService gameService;
	@Autowired
	private EventPublisher eventPublisher;

	@Override
	public void onApplicationEvent(Event event) {
		var eventType = event.getType();
		switch (eventType) {
			case QUEUE_UPDATED: {
				this.gameService
					.createGameIfPossible()
					.ifPresent(this.eventPublisher::fireGameStarted);
			}
		}
		if (eventType.triggersAutoMessage()) {
			String destination = eventType.getEndpoint(event.getId());
			this.send(destination, event.getPayload());
		}
	}

	private void send(String destination, Serializable payload) {
		this.template.convertAndSend(destination, payload);
	}
}
