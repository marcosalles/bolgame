package com.marcosalles.bolgame.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Random;

import static com.marcosalles.bolgame.model.entity.GameState.STARTED;

@Entity
@Getter
@Table(name = "games")
@NoArgsConstructor
public class Game extends BaseEntity {

	@Id
	private String id;

	@OneToOne
	@JoinColumn(name = "player_one_id")
	private Player playerOne;

	@OneToOne
	@JoinColumn(name = "player_two_id")
	private Player playerTwo;

	@Setter
	@Enumerated(EnumType.STRING)
	private GameState state = STARTED;

	@Builder
	public Game(String id, Player playerOne, Player playerTwo) {
		this.id = id;
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
	}

	public GameState goToNextState() {
		boolean finished = new Random().nextInt(100) > 90; //FIXME victory condition
		this.state = this.state.next(finished);
		return this.state;
	}
}
