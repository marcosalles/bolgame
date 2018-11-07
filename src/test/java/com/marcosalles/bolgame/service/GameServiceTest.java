package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.GameDAO;
import com.marcosalles.bolgame.dao.QueueDAO;
import com.marcosalles.bolgame.dao.ScoreDAO;
import com.marcosalles.bolgame.event.EventPublisher;
import com.marcosalles.bolgame.model.dto.TurnInfo;
import com.marcosalles.bolgame.model.entity.Game;
import com.marcosalles.bolgame.model.entity.Player;
import com.marcosalles.bolgame.model.entity.QueuedPlayer;
import com.marcosalles.bolgame.model.entity.Score;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.marcosalles.bolgame.model.entity.GameState.FINISHED;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceTest {

	private GameService service;
	private GameDAO gameDAO;
	private QueueDAO queueDAO;
	private ScoreDAO scoreDAO;
	private EventPublisher eventPublisher;

	@Before
	public void setUp() throws Exception {
		gameDAO = mock(GameDAO.class);
		queueDAO = mock(QueueDAO.class);
		scoreDAO = mock(ScoreDAO.class);
		eventPublisher = mock(EventPublisher.class);

		service = new GameService(gameDAO, queueDAO, scoreDAO, eventPublisher);
	}

	@Test
	public void createGameIfPossible__should_return_empty_if_not_enough_players_in_queue() {
		doReturn(1L).when(queueDAO).count();
		assertThat(service.createGameIfPossible(), is(Optional.empty()));
	}

	@Test
	public void createGameIfPossible__should_return_game_if_enough_players_in_queue() {
		doReturn(2L).when(queueDAO).count();
		doReturn(mock(QueuedPlayer.class)).when(queueDAO).findFirstByOrderByCreatedAtAsc();

		assertThat(service.createGameIfPossible().isPresent(), is(true));
	}

	@Test
	public void isReadyToStartGame__should_return_if_two_or_more_players_in_queue() {
		doReturn(0L, 1L, 2L, 3L).when(queueDAO).count();
		assertThat(service.isReadyToStartGame(), is(false));
		assertThat(service.isReadyToStartGame(), is(false));
		assertThat(service.isReadyToStartGame(), is(true));
		assertThat(service.isReadyToStartGame(), is(true));
	}

	@Test
	public void startGame__should_create_game_with_shuffled_players() {
		final var queuedPlayer = mock(QueuedPlayer.class);
		final var aPlayer = mock(Player.class);
		final var anotherPlayer = mock(Player.class);
		doReturn(aPlayer, anotherPlayer).when(queuedPlayer).getPlayer();
		doReturn(queuedPlayer).when(queueDAO).findFirstByOrderByCreatedAtAsc();

		final var game = service.startGame();
		assertThat(game, not(nullValue()));
		assertThat(List.of(game.getPlayerOne(), game.getPlayerTwo()), containsInAnyOrder(aPlayer, anotherPlayer));
	}

	@Test
	public void makeMoveFor__should_notify_invalid_data_if_no_valid_game() {
		final var player = mock(Player.class);
		final var turnInfo = mock(TurnInfo.class);

		when(gameDAO.findById(any())).thenReturn(Optional.empty());

		final var service = spy(this.service);
		service.makeMoveFor(player, turnInfo);
		verify(service).notifyInvalidData(player);
	}

	@Test
	public void makeMoveFor__should_save_game_and_fire_move_made_if_game_move_made() {
		final var player = mock(Player.class);
		final var turnInfo = mock(TurnInfo.class);
		final var game = mock(Game.class);

		when(gameDAO.findById(any())).thenReturn(Optional.of(game));
		when(game.makeMove(player, turnInfo.getPitId())).thenReturn(true);

		service.makeMoveFor(player, turnInfo);
		verify(gameDAO).save(game);
		verify(eventPublisher).fireMoveMade(game);
	}

	@Test
	public void makeMoveFor__should_do_nothing_if_no_move_made() {
		final var player = mock(Player.class);
		final var turnInfo = mock(TurnInfo.class);
		final var game = mock(Game.class);

		when(gameDAO.findById(turnInfo.getGameId())).thenReturn(Optional.of(game));
		when(game.makeMove(player, turnInfo.getPitId())).thenReturn(false);

		final var service = spy(this.service);
		service.makeMoveFor(player, turnInfo);
		verify(service, never()).notifyInvalidData(any());
		verify(service, never()).createLogFor(any());
		verify(gameDAO, never()).save(any());
		verify(eventPublisher, never()).fireMoveMade(any());
	}

	@Test
	public void makeMoveFor__should_create_log_if_game_finished() {
		final var player = mock(Player.class);
		final var turnInfo = mock(TurnInfo.class);
		final var game = mock(Game.class);

		when(gameDAO.findById(any())).thenReturn(Optional.of(game));
		when(game.makeMove(player, turnInfo.getPitId())).thenReturn(true);
		when(game.is(FINISHED)).thenReturn(true);

		final var service = spy(this.service);
		service.makeMoveFor(player, turnInfo);
		verify(service).createLogFor(game);
	}

	@Test
	public void notifyInvalidData__should_fire_invalid_data_event_for_player() {
		final var player = mock(Player.class);
		final var game = mock(Game.class);
		when(gameDAO.findByParticipantId(player.getId())).thenReturn(game);

		service.notifyInvalidData(player);
		verify(eventPublisher).fireInvalidDataFor(player, game);
	}

	@Test
	public void createLogFor__should_save_score_and_fire_event() {
		final var game = mock(Game.class);
		final var score = mock(Score.class);
		when(game.buildScore()).thenReturn(score);
		when(scoreDAO.save(score)).thenReturn(score);

		service.createLogFor(game);
		verify(scoreDAO).save(score);
		verify(eventPublisher).fireScoreSaved(score);
	}
}