package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class PlayerDAOTest {

	@Autowired
	private PlayerDAO playerDAO;

	@Test(expected = JpaSystemException.class)
	public void save__should_not_save_without_id() {
		final var player = Player.builder().username("player").build();
		playerDAO.save(player);
	}
}