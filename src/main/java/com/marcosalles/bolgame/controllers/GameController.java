package com.marcosalles.bolgame.controllers;

import com.marcosalles.bolgame.model.entity.Message;
import com.marcosalles.bolgame.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;

import static java.lang.String.format;

@Controller
public class GameController {

	@Autowired
	private SimpMessagingTemplate template;
	@Autowired
	private PlayerService playerService;

	@RequestMapping({"/dashboard"})
	public String dashboard() {
		return "dashboard";
	}

	@MessageMapping("/game/register-player")
	public void register(final Message message) throws Exception {
		final var player = playerService.findOrCreatePlayerFrom(message.getPlayerId(), message.getContents());
		this.send("/sub/registered-player", player);
	}

	@MessageMapping("/user/{playerId}/game/search")
	public void search(@DestinationVariable final String playerId, final Message message) throws Exception {
		this.send(format("/user/%s/game/searching", playerId), message);
	}

	private void send(String destination, Serializable payload) {
		this.template.convertAndSend(destination, payload);
	}

}
