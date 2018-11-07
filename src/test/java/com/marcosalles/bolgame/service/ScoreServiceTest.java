package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.ScoreDAO;
import com.marcosalles.bolgame.model.entity.Player;
import com.marcosalles.bolgame.model.entity.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class ScoreServiceTest {

	private ScoreService service;

	private ScoreDAO scoreDAO;
	private Player player;

	@BeforeEach
	void setUp() {
		player = mock(Player.class);
		scoreDAO = mock(ScoreDAO.class);
		service = new ScoreService(scoreDAO);
	}

	@Test
	void allScoresFor__should_return_list_from_dao() {
		doReturn(List.of()).when(scoreDAO).findAllWithParticipant(player);
		assertThat(service.allScoresFor(player), is(empty()));

		final var score = mock(Score.class);
		doReturn(List.of(score)).when(scoreDAO).findAllWithParticipant(player);
		assertThat(service.allScoresFor(player), contains(score));
	}
}