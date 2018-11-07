package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.PlayerDAO;
import com.marcosalles.bolgame.dao.QueueDAO;
import com.marcosalles.bolgame.event.EventPublisher;
import com.marcosalles.bolgame.model.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PlayerServiceTest {

	private PlayerService service;
	private PlayerDAO playerDAO;
	private QueueDAO queueDAO;
	private EventPublisher eventPublisher;

	@BeforeEach
	public void setUp() throws Exception {
		playerDAO = mock(PlayerDAO.class);
		queueDAO = mock(QueueDAO.class);
		eventPublisher = mock(EventPublisher.class);
		service = new PlayerService(playerDAO, queueDAO, eventPublisher);
	}

	@Test
	public void registerPlayer__should_save_player_and_fire_event_when_id_has_been_registered() {
		var hash = "some-hash";
		var id = "some-id";
		var username = "username";
		var savedPlayer = Player.builder().id(id).username(username).build();
		doReturn(Optional.ofNullable(savedPlayer))
			.when(playerDAO).findById(id);
		doReturn(savedPlayer).when(playerDAO).save(savedPlayer);

		service.registerPlayer(hash, Optional.ofNullable(id), username);
		verify(playerDAO).save(savedPlayer);
		verify(eventPublisher).firePlayerRegistered(hash, savedPlayer);
	}

	@Test
	public void registerPlayer__should_build_and_save_new_player_and_fire_event_when_id_has_not_been_registered() {
		var hash = "some-hash";
		var id = "some-id";
		var username = "username";

		doReturn(Optional.empty())
			.when(playerDAO).findById(id);
		var builtPlayer = Player.builder().id(id).username(username).build();
		doReturn(builtPlayer).when(playerDAO).save(any());

		service.registerPlayer(hash, Optional.ofNullable(id), username);
		verify(playerDAO, never()).save(builtPlayer);
		verify(eventPublisher).firePlayerRegistered(hash, builtPlayer);
	}

	@Test
	void placeOnQueue__should_save_queued_player_if_not_yet_there() {
		final var player = mock(Player.class);
		doReturn(false).when(queueDAO).exists(any());
		service.placeOnQueue(player);
		verify(queueDAO).save(any());
	}

	@Test
	void placeOnQueue__should_fire_event_for_player() {
		final var player = mock(Player.class);
		service.placeOnQueue(player);
		verify(eventPublisher).firePlayerQueued(player);
	}

	@Test
	void getPlayerFor__should_return_empty_if_no_player_exists() {
		final var id = "id";
		doReturn(Optional.empty()).when(playerDAO).findById(id);
		assertThat(service.getPlayerFor(id), is(Optional.empty()));
	}

	@Test
	void getPlayerFor__should_return_player_when_exists() {
		final var id = "id";
		doReturn(Optional.of(mock(Player.class))).when(playerDAO).findById(id);
		assertThat(service.getPlayerFor(id).isPresent(), is(true));
	}
}