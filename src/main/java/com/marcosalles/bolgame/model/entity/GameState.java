package com.marcosalles.bolgame.model.entity;

import com.marcosalles.bolgame.model.EventType;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.marcosalles.bolgame.model.EventType.GAME_FINISHED;
import static com.marcosalles.bolgame.model.EventType.TURN_OVER;

public enum GameState {
	PLAYER_ONES_TURN(TURN_OVER) {
		@Override
		public GameState next(boolean finished) {
			return finished ? FINISHED : PLAYER_TWOS_TURN;
		}
	},
	PLAYER_TWOS_TURN(TURN_OVER) {
		@Override
		public GameState next(boolean finished) {
			return finished ? FINISHED : PLAYER_ONES_TURN;
		}
	},
	FINISHED(GAME_FINISHED) {
		@Override
		public boolean isAllowed(String playersPlace) {
			return false;
		}
	};

	@Getter
	private final EventType eventToFire;

	GameState(EventType eventToFire) {
		this.eventToFire = eventToFire;
	}

	public GameState next(boolean finished) {
		return this;
	}

	public boolean isAllowed(String playersPlace) {
		return Arrays.asList("one", "two").contains(playersPlace) && name().toLowerCase().contains(playersPlace);
	}
}
