package com.marcosalles.bolgame.model;

import java.util.Optional;

public enum EventType {
	REFRESH("/user/%s/game/refresh"),
	PLAYER_REGISTERED("/user/%s/registered"),
	QUEUE_UPDATED {
		@Override
		public String getEndpoint(String hash) {
			throw new UnsupportedOperationException("No endpoint here");
		}
	},
	GAME_STARTED("/user/%s/game/started"),
	TURN_OVER("/user/%s/game/turnover"),
	GAME_FINISHED("/user/%s/game/finished"),
	SCORE_SAVED("/user/%s/game/score/saved");

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
}
