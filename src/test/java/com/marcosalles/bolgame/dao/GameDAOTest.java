package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.Game;
import com.marcosalles.bolgame.model.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class GameDAOTest {

	@Autowired
	private GameDAO gameDAO;
	@Autowired
	private PlayerDAO playerDao;

	@Test(expected = JpaSystemException.class)
	public void save__should_not_save_without_id() {
		final var game = Game.builder()
			.playerOne(playerDao.save(this.player("player-1")))
			.playerTwo(playerDao.save(this.player("player-2")))
			.build();
		gameDAO.save(game);
	}

	@Test
	public void findByParticipantId__should_return_null_if_no_games() {
		final var player = playerDao.save(this.player("player"));
		assertThat(gameDAO.findAll().iterator().hasNext(), is(false));
		assertThat(gameDAO.findByParticipantId(player.getId()), is(nullValue()));
	}

	@Test
	public void findByParticipantId__should_return_null_if_no_games_with_participant() {
		final var playerOne = playerDao.save(this.player("player-1"));
		final var playerTwo = playerDao.save(this.player("player-2"));
		final var playerThree = playerDao.save(this.player("player-3"));
		gameDAO.save(Game
			.builder()
			.id("game-1")
			.playerOne(playerOne)
			.playerTwo(playerTwo)
			.build()
		);
		assertThat(gameDAO.findByParticipantId(playerThree.getId()), is(nullValue()));
	}

	@Test
	public void findByParticipantId__should_return_game_with_player_one() {
		final var playerOne = playerDao.save(this.player("player-1"));
		final var playerTwo = playerDao.save(this.player("player-2"));
		final var game = gameDAO.save(Game
			.builder()
			.id("game-1")
			.playerOne(playerOne)
			.playerTwo(playerTwo)
			.build()
		);
		assertThat(gameDAO.findByParticipantId(playerOne.getId()), is(game));
	}

	@Test
	public void findByParticipantId__should_return_game_with_player_two() {
		final var playerOne = playerDao.save(this.player("player-1"));
		final var playerTwo = playerDao.save(this.player("player-2"));
		final var game = gameDAO.save(Game
			.builder()
			.id("game-1")
			.playerOne(playerOne)
			.playerTwo(playerTwo)
			.build()
		);
		assertThat(gameDAO.findByParticipantId(playerTwo.getId()), is(game));
	}

	private Player player(String idAndUsername) {
		return Player.builder().id(idAndUsername).username(idAndUsername).build();
	}
}