package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.PlayerDAO;
import com.marcosalles.bolgame.dao.QueueDAO;
import com.marcosalles.bolgame.event.EventPublisher;
import com.marcosalles.bolgame.model.entity.Player;
import com.marcosalles.bolgame.model.entity.QueuedPlayer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class PlayerService {

	@Autowired
	private PlayerDAO playerDAO;
	@Autowired
	private QueueDAO queueDAO;
	@Autowired
	private EventPublisher eventPublisher;

	public void registerPlayer(final String hash, final Optional<String> optionalPlayerId, final String username) {
		final var optionalPlayer = playerDAO.findById(optionalPlayerId.orElseGet(() -> ""));
		var player = optionalPlayer.orElseGet(() ->
			Player.builder()
				.id(UUID.randomUUID().toString())
				.build()
		);
		this.changeUsernameIfNotAnon(player, username);

		player = playerDAO.save(player);
		this.eventPublisher.firePlayerRegistered(hash, player);
	}

	protected void changeUsernameIfNotAnon(final Player player, final String username) {
		final var anonymous = "anonymous";
		final var newUsername = StringUtils.concatReplaceNulls(anonymous, username);

		final boolean userWasAnon = anonymous.equals(player.getUsername());
		final boolean newUsernameIsCustom = !anonymous.equals(newUsername);
		if (userWasAnon || newUsernameIsCustom) {
			player.setUsername(HtmlUtils.htmlEscape(username));
		}
	}

	public void placeOnQueue(final Player player) {
		var queuedPlayer = QueuedPlayer.builder().player(player).build();
		if (!queueDAO.exists(Example.of(queuedPlayer))) {
			queueDAO.save(queuedPlayer);
		}

		this.eventPublisher.firePlayerQueued(player);
	}

	public Optional<Player> getPlayerFor(final String playerId) {
		return playerDAO.findById(playerId);
	}
}
