package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.Player;
import com.marcosalles.bolgame.model.entity.Score;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScoreDAO extends CrudRepository<Score, Long> {
	@Query("FROM Score WHERE :player in (game.playerOne, game.playerTwo) ORDER BY createdAt DESC")
	List<Score> findAllWithParticipant(@Param("player") Player player);
}
