package com.marcosalles.bolgame.controller;

import com.marcosalles.bolgame.event.EventPublisher;
import com.marcosalles.bolgame.model.Message;
import com.marcosalles.bolgame.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GameController {

	@Autowired
	private PlayerService playerService;
	@Autowired
	private EventPublisher eventPublisher;

	@RequestMapping({"/", "/dashboard"})
	public String dashboard() {
		return "dashboard";
	}

	@MessageMapping("/user/{hash}/register")
	public void register(@DestinationVariable final String hash, final Message message) throws Exception {
		this.playerService.registerPlayer(hash, message.getPlayerId(), message.getContents());
	}

	@MessageMapping("/user/{hash}/game/search")
	public void search(@DestinationVariable final String hash) throws Exception {
		this.playerService.getPlayerFor(hash)
			.ifPresent(playerService::placeOnQueue);
	}

}
