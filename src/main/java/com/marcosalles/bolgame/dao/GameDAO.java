package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GameDAO extends CrudRepository<Game, String> {

	@Query("FROM Game WHERE :id IN (playerOne.id, playerTwo.id)")
	Game findByParticipantId(@Param("id") String id);
}
