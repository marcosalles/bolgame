package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.GameDAO;
import com.marcosalles.bolgame.dao.QueueDAO;
import com.marcosalles.bolgame.model.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class GameService {

	public static final long NUMBER_OF_PLAYERS_FOR_GAME = 2L;
	@Autowired
	private GameDAO gameDAO;
	@Autowired
	private QueueDAO queueDAO;

	public Optional<Game> createGameIfPossible() {
		if (this.isReadyToStartGame()) {
			var game = this.startGame();
			return Optional.ofNullable(game);
		}
		return Optional.empty();
	}

	protected boolean isReadyToStartGame() {
		return queueDAO.count() >= NUMBER_OF_PLAYERS_FOR_GAME;
	}

	protected Game startGame() {
		var firstQueuedPlayer = queueDAO.findFirstByOrderByCreatedAtAsc();
		var playerOne = firstQueuedPlayer.getPlayer();
		queueDAO.delete(firstQueuedPlayer);

		var secondQueuedPlayer = queueDAO.findFirstByOrderByCreatedAtAsc();
		var playerTwo = secondQueuedPlayer.getPlayer();
		queueDAO.delete(secondQueuedPlayer);

		var game = Game.builder()
			.id(UUID.randomUUID().toString())
			.playerOne(playerOne)
			.playerTwo(playerTwo)
			.build();

		return gameDAO.save(game);
	}

}
