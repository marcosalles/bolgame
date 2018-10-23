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
import static org.hamcrest.Matchers.not;
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

		service.registerPlayer(hash, id, username);
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

		service.registerPlayer(hash, id, username);
		verify(playerDAO, never()).save(builtPlayer);
		verify(eventPublisher).firePlayerRegistered(hash, builtPlayer);
	}

	@Test
	public void changeUsernameIfNotAnon__should_overwrite_username_if_previous_is_anonymous() {
		var expectedUsername = "username";
		var player = Player.builder().username("anonymous").build();
		service.changeUsernameIfNotAnon(player, expectedUsername);
		assertThat(player.getUsername(), is(expectedUsername));
	}

	@Test
	public void changeUsernameIfNotAnon__should_overwrite_username_if_previous_is_different_and_new_is_not_anonymous() {
		var expectedUsername = "other-username";
		var player = Player.builder().username("username").build();
		service.changeUsernameIfNotAnon(player, expectedUsername);
		assertThat(player.getUsername(), is(expectedUsername));
	}

	@Test
	public void changeUsernameIfNotAnon__should_not_overwrite_username_if_new_is_anonymous() {
		var unexpectedUsername = "anonymous";
		var expectedUsername = "username";
		var player = Player.builder().username(expectedUsername).build();
		service.changeUsernameIfNotAnon(player, unexpectedUsername);
		assertThat(player.getUsername(), not(unexpectedUsername));
		assertThat(player.getUsername(), is(expectedUsername));
	}
}