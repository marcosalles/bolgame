package com.marcosalles.bolgame.event;

import com.marcosalles.bolgame.model.Event;
import com.marcosalles.bolgame.model.entity.Game;
import com.marcosalles.bolgame.model.entity.Player;
import com.marcosalles.bolgame.model.entity.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static com.marcosalles.bolgame.model.EventType.*;

@Component
public class EventPublisher {
	@Autowired
	private ApplicationEventPublisher publisher;

	public void fire(Event event) {
		publisher.publishEvent(event);
	}

	public void firePlayerRegistered(String hash, Player player) {
		this.fire(Event.builder()
			.source(this)
			.type(PLAYER_REGISTERED)
			.id(hash)
			.payload(player)
			.build());
	}

	public void firePlayerQueued(Player player) {
		this.fire(Event.builder()
			.source(this)
			.type(QUEUE_UPDATED)
			.id(player.getId())
			.payload(player)
			.build());
	}

	public void fireGameStarted(Game game) {
		this.fire(Event.builder()
			.source(this)
			.type(GAME_STARTED)
			.id(game.getPlayerOne().getId())
			.payload(game)
			.build());

		this.fire(Event.builder()
			.source(this)
			.type(GAME_STARTED)
			.id(game.getPlayerTwo().getId())
			.payload(game)
			.build());
	}

	public void fireMoveMade(Game game) {
		this.fire(Event.builder()
			.source(this)
			.type(game.getState().getEventToFire())
			.id(game.getPlayerOne().getId())
			.payload(game)
			.build());

		this.fire(Event.builder()
			.source(this)
			.type(game.getState().getEventToFire())
			.id(game.getPlayerTwo().getId())
			.payload(game)
			.build());
	}

	public void fireInvalidDataFor(Player player, Game game) {
		this.fire(Event.builder()
			.source(this)
			.type(REFRESH)
			.id(player.getId())
			.payload(game)
			.build());
	}

	public void fireScoreSaved(Score score) {
		var game = score.getGame();
		this.fire(Event.builder()
			.source(this)
			.type(SCORE_SAVED)
			.id(game.getPlayerOne().getId())
			.payload(score)
			.build());

		this.fire(Event.builder()
			.source(this)
			.type(SCORE_SAVED)
			.id(game.getPlayerTwo().getId())
			.payload(score)
			.build());
	}
}
