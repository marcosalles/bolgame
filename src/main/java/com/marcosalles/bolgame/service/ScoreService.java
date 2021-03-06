package com.marcosalles.bolgame.service;

import com.marcosalles.bolgame.dao.ScoreDAO;
import com.marcosalles.bolgame.model.entity.Player;
import com.marcosalles.bolgame.model.entity.Score;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ScoreService {
	@Autowired
	private ScoreDAO scoreDAO;

	public List<Score> allScoresFor(Player player) {
		return this.scoreDAO.findAllWithParticipant(player);
	}
}
