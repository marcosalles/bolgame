package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerDAO extends JpaRepository<Player, String> {
}
