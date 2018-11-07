package com.marcosalles.bolgame.model.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Getter
@Setter
@Table(name = "queued_players")
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
public class QueuedPlayer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "player_id")
	private Player player;

	@Builder
	public QueuedPlayer(Player player) {
		this.player = player;
	}
}
