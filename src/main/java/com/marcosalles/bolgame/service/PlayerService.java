package com.marcosalles.bolgame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcosalles.bolgame.dao.PlayerDAO;
import com.marcosalles.bolgame.model.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerService {

	@Autowired
	private PlayerDAO playerDAO;

	public Player findOrCreatePlayerFrom(String payload) throws IOException {
		Player probe = new ObjectMapper().readValue(payload, Player.class);
		Optional<Player> optionalUser = playerDAO.findOne(Example.of(probe));
		Player player = optionalUser.orElseGet(() ->
			playerDAO.save(Player.builder()
				.id(UUID.randomUUID().toString())
				.username(HtmlUtils.htmlEscape(probe.getUsername()))
				.build())
		);
		return player;
	}
}
