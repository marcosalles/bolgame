package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.GameDAO;
import com.marcosalles.bolgame.dao.QueueDAO;
import com.marcosalles.bolgame.model.entity.Game;
import com.marcosalles.bolgame.model.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
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
		var players = new ArrayList<Player>();
		var firstQueuedPlayer = queueDAO.findFirstByOrderByCreatedAtAsc();
		players.add(firstQueuedPlayer.getPlayer());
		queueDAO.delete(firstQueuedPlayer);

		var secondQueuedPlayer = queueDAO.findFirstByOrderByCreatedAtAsc();
		players.add(secondQueuedPlayer.getPlayer());
		queueDAO.delete(secondQueuedPlayer);

		Collections.shuffle(players);
		var playerOne = players.get(0);
		var playerTwo = players.get(1);

		var game = Game.builder()
			.id(UUID.randomUUID().toString())
			.playerOne(playerOne)
			.playerTwo(playerTwo)
			.build();

		return gameDAO.save(game);
	}

}
