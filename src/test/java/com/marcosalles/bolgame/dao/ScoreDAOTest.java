package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.Game;
import com.marcosalles.bolgame.model.entity.Player;
import com.marcosalles.bolgame.model.entity.Score;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ScoreDAOTest {

	@Autowired
	private ScoreDAO scoreDAO;
	@Autowired
	private GameDAO gameDAO;
	@Autowired
	private PlayerDAO playerDAO;

	@Test
	public void findAllWithParticipant__should_return_empty_if_no_scores() {
		final var player = this.player("player");
		assertThat(scoreDAO.findAllWithParticipant(player), hasSize(0));
	}

	@Test
	public void findAllWithParticipant__should_return_empty_if_no_scores_with_game_participant() {
		final var player = this.player("player");
		final Game game = game("game", this.player("player-1"), this.player("player-2"));
		scoreDAO.save(Score.builder().game(game).build());

		assertThat(scoreDAO.findAllWithParticipant(player), hasSize(0));
	}

	@Test
	public void findAllWithParticipant__should_return_scores_ordered_by_creation_date() {
		final var player = this.player("player");

		final var score1 = scoreDAO.save(Score.builder()
			.game(this.game("game-1", player, this.player("player-2"))).build());
		final var score2 = scoreDAO.save(Score.builder()
			.game(this.game("game-2", this.player("player-3"), player)).build());
		final var score3 = scoreDAO.save(Score.builder()
			.game(this.game("game-3", player, this.player("player-4"))).build());

		assertThat(scoreDAO.findAllWithParticipant(player), contains(score3, score2, score1));
	}

	private Game game(String id, Player playerOne, Player playerTwo) {
		return gameDAO.save(Game
			.builder()
			.id(id)
			.playerOne(playerOne)
			.playerTwo(playerTwo)
			.build()
		);
	}

	private Player player(String idAndUsername) {
		return playerDAO.save(Player.builder().id(idAndUsername).username(idAndUsername).build());
	}
}