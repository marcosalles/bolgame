package com.marcosalles.bolgame.dao;

import com.marcosalles.bolgame.model.entity.QueuedPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueDAO extends JpaRepository<QueuedPlayer, Long> {
	public QueuedPlayer findFirstByOrderByCreatedAtAsc();
}