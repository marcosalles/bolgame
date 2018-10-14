package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.PlayerDAO;
import com.marcosalles.bolgame.model.entity.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
import org.thymeleaf.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class PlayerService {

	@Autowired
	private PlayerDAO playerDAO;

	public Player findOrCreatePlayerFrom(final String playerId, final String username) {
		var optionalPlayer = playerDAO.findById(playerId);
		var player = optionalPlayer.orElseGet(() ->
			Player.builder()
				.id(Optional.ofNullable(playerId).orElseGet(() -> UUID.randomUUID().toString()))
				.build()
		);
		this.changeUsernameIfNotAnon(player, username);

		return playerDAO.save(player);
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
}
