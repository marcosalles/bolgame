package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.PlayerDAO;
import com.marcosalles.bolgame.model.entity.Player;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PlayerServiceTest {

	private PlayerService service;
	private PlayerDAO playerDAO;

	@BeforeEach
	public void setUp() throws Exception {
		playerDAO = mock(PlayerDAO.class);
		service = new PlayerService(playerDAO);
	}

	@Test
	public void findOrCreatePlayerFrom__should_return_entity_when_id_has_been_registered() {
		var id = "some-id";
		var username = "username";
		var savedPlayer = Player.builder().id(id).username(username).build();
		doReturn(Optional.ofNullable(savedPlayer))
			.when(playerDAO).findById(id);
		doReturn(savedPlayer).when(playerDAO).save(savedPlayer);

		var player = service.findOrCreatePlayerFrom(id, username);
		verify(playerDAO).save(savedPlayer);
		assertThat(player, is(savedPlayer));
	}

	@Test
	public void findOrCreatePlayerFrom__should_build_and_return_new_player_when_id_has_not_been_registered() {
		var id = "some-id";
		var username = "username";

		doReturn(Optional.empty())
			.when(playerDAO).findById(id);
		var builtPlayer = Player.builder().id(id).username(username).build();
		doReturn(builtPlayer).when(playerDAO).save(any());

		var player = service.findOrCreatePlayerFrom(id, username);
		verify(playerDAO, never()).save(builtPlayer);
		assertThat(player, is(builtPlayer));
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