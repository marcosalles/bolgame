package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameDAO extends JpaRepository<Game, String> {
}
