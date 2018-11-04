package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.QueuedPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueDAO extends JpaRepository<QueuedPlayer, Long> {
	QueuedPlayer findFirstByOrderByCreatedAtAsc();
}