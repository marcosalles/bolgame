package com.marcosalles.bolgame.controllers;

import com.marcosalles.bolgame.model.entity.Player;
import com.marcosalles.bolgame.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class GameController {

	@Autowired
	private PlayerService playerService;

	@RequestMapping({"/dashboard"})
	public String dashboard() {
		return "dashboard";
	}

	@MessageMapping("/player/register")
	@SendTo("/player/registered")
	public Player register(HttpServletResponse response, String payload) throws Exception {
		Player player = playerService.findOrCreatePlayerFrom(payload);
		response.addCookie(new Cookie("bolgame.player.id", player.getId()));
		return player;
	}

}
