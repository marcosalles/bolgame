package com.marcosalles.bolgame.model.entity;

public enum GameState {
	STARTED {
		@Override
		public GameState next(boolean finished) {
			return PLAYER_ONES_TURN;
		}
	},
	PLAYER_ONES_TURN {
		@Override
		public GameState next(boolean finished) {
			return finished ? FINISHED : PLAYER_TWOS_TURN;
		}
	},
	PLAYER_TWOS_TURN {
		@Override
		public GameState next(boolean finished) {
			return finished ? FINISHED : PLAYER_ONES_TURN;
		}
	},
	FINISHED;

	public GameState next(boolean finished) {
		return this;
	}
}
