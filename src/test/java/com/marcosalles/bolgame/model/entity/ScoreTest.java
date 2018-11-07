package com.marcosalles.bolgame.model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ScoreTest {

	private Player playerOne;
	private Player playerTwo;
	private Game game;

	@BeforeEach
	public void setUp() {
		playerOne = Player.builder().id("1").username("1").build();
		playerTwo = Player.builder().id("2").username("2").build();
		game = Game.builder().id("game").playerOne(playerOne).playerTwo(playerTwo).build();
	}

	@Test
	public void getOpponentsUsername__should_return_other_players_username() {
		final var score = Score.builder().game(game).build();
		assertThat(score.getOpponentsUsername(playerOne), is(playerTwo.getUsername()));
		assertThat(score.getOpponentsUsername(playerTwo), is(playerOne.getUsername()));
	}

	@Test
	public void getOpponentsUsername__should_return_player_ones_username_if_null_player() {
		final var score = Score.builder().game(game).build();
		assertThat(score.getOpponentsUsername(null), is(playerOne.getUsername()));
	}

	@Test
	public void getScoreVersus__should_return_scores_joined_by__x_() {
		final var score = Score.builder().playerOneScore(1).playerTwoScore(2).build();
		assertThat(score.getScoreVersus(), is("1 x 2"));
	}

	@Test
	public void isWinner__should_return_false_if_draw() {
		final var drawScore = Score.builder().build();
		assertThat(drawScore.isWinner(playerOne), is(false));
		assertThat(drawScore.isWinner(playerTwo), is(false));
	}

	@Test
	public void isWinner__should_return_false_if_other_player() {
		assertThat(Score.builder().winner(playerTwo).build().isWinner(playerOne), is(false));
		assertThat(Score.builder().winner(playerOne).build().isWinner(playerTwo), is(false));
	}

	@Test
	public void isWinner__should_return_false_if_null_param() {
		assertThat(Score.builder().winner(playerTwo).build().isWinner(null), is(false));
		assertThat(Score.builder().winner(playerOne).build().isWinner(null), is(false));
	}

	@Test
	public void isWinner__should_return_true_if_same_player() {
		assertThat(Score.builder().winner(playerOne).build().isWinner(playerOne), is(true));
		assertThat(Score.builder().winner(playerTwo).build().isWinner(playerTwo), is(true));
	}
}