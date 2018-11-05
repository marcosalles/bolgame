package com.marcosalles.bolgame.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "scores")
@Getter
@NoArgsConstructor
public class Score extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "game_id")
	private Game game;

	@OneToOne
	@JoinColumn(name = "winner_id")
	private Player winner;

	private Integer playerOneScore;
	private Integer playerTwoScore;

	@Builder
	public Score(Game game, Player winner, Integer playerOneScore, Integer playerTwoScore) {
		this.game = game;
		this.winner = winner;
		this.playerOneScore = playerOneScore;
		this.playerTwoScore = playerTwoScore;
	}

	public String getOpponentsUsername(Player player) {
		return this.game.getOpponent(player).getUsername();
	}

	public String getScoreVersus() {
		return String.format("%d x %d", playerOneScore, playerTwoScore);
	}

	public boolean isWinner(Player player) {
		return winner.equals(player);
	}
}
