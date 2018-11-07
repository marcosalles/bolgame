package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.GameDAO;
import com.marcosalles.bolgame.dao.QueueDAO;
import com.marcosalles.bolgame.dao.ScoreDAO;
import com.marcosalles.bolgame.event.EventPublisher;
import com.marcosalles.bolgame.model.dto.TurnInfo;
import com.marcosalles.bolgame.model.entity.Game;
import com.marcosalles.bolgame.model.entity.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static com.marcosalles.bolgame.model.entity.GameState.FINISHED;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class GameService {

	public static final long NUMBER_OF_PLAYERS_FOR_GAME = 2L;
	@Autowired
	private GameDAO gameDAO;
	@Autowired
	private QueueDAO queueDAO;
	@Autowired
	private ScoreDAO scoreDAO;
	@Autowired
	private EventPublisher eventPublisher;

	public Optional<Game> createGameIfPossible() {
		if (this.isReadyToStartGame()) {
			var game = this.startGame();
			return Optional.ofNullable(game);
		}
		return Optional.empty();
	}

	protected boolean isReadyToStartGame() {
		return this.queueDAO.count() >= NUMBER_OF_PLAYERS_FOR_GAME;
	}

	protected Game startGame() {
		var players = new ArrayList<Player>();
		var firstQueuedPlayer = this.queueDAO.findFirstByOrderByCreatedAtAsc();
		players.add(firstQueuedPlayer.getPlayer());
		this.queueDAO.delete(firstQueuedPlayer);

		var secondQueuedPlayer = this.queueDAO.findFirstByOrderByCreatedAtAsc();
		players.add(secondQueuedPlayer.getPlayer());
		this.queueDAO.delete(secondQueuedPlayer);

		Collections.shuffle(players);
		var playerOne = players.get(0);
		var playerTwo = players.get(1);

		var game = Game.builder()
			.id(UUID.randomUUID().toString())
			.playerOne(playerOne)
			.playerTwo(playerTwo)
			.build();

		this.gameDAO.save(game);
		return game;
	}

	public void makeMoveFor(Player player, TurnInfo turnInfo) {
		this.gameDAO.findById(turnInfo.getGameId())
			.ifPresentOrElse(
				game -> {
					boolean moveMade = game.makeMove(player, turnInfo.getPitId());
					if (moveMade) {
						this.gameDAO.save(game);
						this.eventPublisher.fireMoveMade(game);
						if (game.is(FINISHED)) {
							this.createLogFor(game);
						}
					}
				},
				() -> this.notifyInvalidData(player)
			);
	}

	public void notifyInvalidData(Player player) {
		var game = this.gameDAO.findByParticipantId(player.getId());
		this.eventPublisher.fireInvalidDataFor(player, game);
	}

	public void createLogFor(Game game) {
		var score = this.scoreDAO.save(game.buildScore());
		this.eventPublisher.fireScoreSaved(score);
	}
}