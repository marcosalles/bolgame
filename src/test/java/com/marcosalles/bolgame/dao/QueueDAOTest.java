package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.Player;
import com.marcosalles.bolgame.model.entity.QueuedPlayer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class QueueDAOTest {

	@Autowired
	private QueueDAO queueDAO;
	@Autowired
	private PlayerDAO playerDAO;

	@Test
	public void findFirstByOrderByCreatedAtAsc__should_return_null_if_no_one_queued() {
		assertThat(queueDAO.findFirstByOrderByCreatedAtAsc(), is(nullValue()));
	}

	@Test
	public void findFirstByOrderByCreatedAtAsc__should_return_first_queued_player() {
		final var player1 = playerDAO.save(Player.builder().id("player-1").username("player-1").build());
		queueDAO.save(QueuedPlayer.builder().player(player1).build());
		final var player2 = playerDAO.save(Player.builder().id("player-1").username("player-1").build());
		queueDAO.save(QueuedPlayer.builder().player(player2).build());

		final var queuedPlayer = queueDAO.findFirstByOrderByCreatedAtAsc();
		assertThat(queuedPlayer, not(nullValue()));
		assertThat(queuedPlayer.getPlayer(), is(player1));
	}
}