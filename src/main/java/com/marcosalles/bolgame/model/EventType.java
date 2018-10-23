package com.marcosalles.bolgame.model;

import java.util.Optional;

public enum EventType {
	PLAYER_REGISTERED("/user/%s/registered"),
	QUEUE_UPDATED,
	GAME_STARTED("/user/%s/game/started");

	private Optional<String> endpoint;

	EventType() {
		this(null);
	}

	EventType(String endpoint) {
		this.endpoint = Optional.ofNullable(endpoint);
	}

	public boolean triggersAutoMessage() {
		return endpoint.isPresent();
	}

	public String getEndpoint(String hash) {
		return endpoint.map(
			path -> String.format(path, hash)
		).get();
	}

	public void resolve(Event event) {
	}
}
