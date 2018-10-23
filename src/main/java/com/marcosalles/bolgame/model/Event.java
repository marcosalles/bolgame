package com.marcosalles.bolgame.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

@Getter
@ToString
public class Event extends ApplicationEvent {

	private EventType type;
	private String id;
	private Serializable payload;

	@Builder
	public Event(Object source, EventType type, String id, Serializable payload) {
		super(source);
		this.type = type;
		this.id = id;
		this.payload = payload;
	}
}
