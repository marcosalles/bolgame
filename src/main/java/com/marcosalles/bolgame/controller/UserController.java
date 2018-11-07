package com.marcosalles.bolgame.controller;

import com.marcosalles.bolgame.service.PlayerService;
import com.marcosalles.bolgame.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {
	@Autowired
	private PlayerService playerService;
	@Autowired
	private ScoreService scoreService;

	@GetMapping("/home")
	public String home() {
		return "player/home";
	}

	@GetMapping("/player/{hash}/scores")
	public String scores(@PathVariable("hash") final String playerId, Model model) {
		var optionalPlayer = this.playerService.getPlayerFor(playerId);
		if (optionalPlayer.isPresent()) {
			var player = optionalPlayer.get();
			model.addAttribute("player", player);
			var scores = this.scoreService.allScoresFor(player);
			model.addAttribute("scores", scores);
			return "player/scores";
		}
		return "";
	}
}
