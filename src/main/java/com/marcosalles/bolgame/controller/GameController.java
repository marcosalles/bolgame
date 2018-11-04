package com.marcosalles.bolgame.controller;

import com.marcosalles.bolgame.model.Message;
import com.marcosalles.bolgame.model.dto.TurnInfo;
import com.marcosalles.bolgame.service.GameService;
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
	private GameService gameService;

	@RequestMapping({"/", "/dashboard"})
	public String dashboard() {
		return "dashboard";
	}

	@MessageMapping("/user/{hash}/register")
	public void register(@DestinationVariable final String hash, final Message<String> message) {
		this.playerService.registerPlayer(hash, message.getPlayerId(), message.getContents());
	}

	@MessageMapping("/user/{hash}/game/search")
	public void search(@DestinationVariable final String hash) {
		this.playerService.getPlayerFor(hash)
			.ifPresent(playerService::placeOnQueue);
	}

	@MessageMapping("/user/{hash}/game/makemove")
	public void makeMove(@DestinationVariable final String hash, final Message<TurnInfo> message) {
		this.playerService.getPlayerFor(hash)
			.ifPresent(player -> this.gameService.makeMoveFor(player, message.getContents()));
	}
}
