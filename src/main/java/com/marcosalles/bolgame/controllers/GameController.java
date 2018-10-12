package com.marcosalles.bolgame.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcosalles.bolgame.model.entity.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.security.PermitAll;
import java.util.UUID;

@PermitAll
@Controller
public class GameController {

	@RequestMapping({"/dashboard"})
	public String dashboard() {
		return "dashboard";
	}

	@MessageMapping("/user/register")
	@SendTo("/user/registered")
	public User register(String payload) throws Exception {
		User user = new ObjectMapper().readValue(payload, User.class);
		System.out.printf("GameController::register(%s)\n", user.getUsername());
		Thread.sleep(1000); // simulated delay
		return User.builder()
			.id(UUID.randomUUID().toString())
			.username(HtmlUtils.htmlEscape(user.getUsername()))
			.build();
	}

}
