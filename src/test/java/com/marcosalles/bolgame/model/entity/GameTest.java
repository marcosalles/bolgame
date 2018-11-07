package com.marcosalles.bolgame.model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.marcosalles.bolgame.model.entity.GameState.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class GameTest {

	private Player playerOne;
	private Player playerTwo;
	private Game game;

	@BeforeEach
	void setUp() {
		playerOne = Player.builder().id("1").build();
		playerTwo = Player.builder().id("2").build();
		game = Game.builder()
			.playerOne(playerOne)
			.playerTwo(playerTwo)
			.build();
	}

	@Test
	void getOpponent__should_return_player_not_provided() {
		assertThat(game.getOpponent(playerOne), is(playerTwo));
		assertThat(game.getOpponent(playerTwo), is(playerOne));
	}

	@Test
	void getSortedPits__should_return_pits_sorted_alphabetically() {
		var pits = game.getSortedPits();
		assertThat(pits, hasSize(14)); // 2 big pits, 12 small pits

		var sortedKeys = pits.stream().map(pit -> pit.getKey()).collect(toList());
		assertThat(sortedKeys, contains(
			"pit-one-1",
			"pit-one-2",
			"pit-one-3",
			"pit-one-4",
			"pit-one-5",
			"pit-one-6",
			"pit-one-big",
			"pit-two-1",
			"pit-two-2",
			"pit-two-3",
			"pit-two-4",
			"pit-two-5",
			"pit-two-6",
			"pit-two-big"
		));
	}

	@Test
	void builder__should_build_pits_with_6_stones_each_and_zero_on_bigs() {
		var pits = Game.builder().build().getPits().entrySet();

		boolean allSmallPitHaveSixStones = pits.stream().filter(pit -> !pit.getKey().contains("big")).allMatch(pit -> pit.getValue() == 6);
		assertThat(allSmallPitHaveSixStones, is(true));

		boolean allBigPitAreZeroed = pits.stream().filter(pit -> pit.getKey().contains("big")).allMatch(pit -> pit.getValue() == 0);
		assertThat(allBigPitAreZeroed, is(true));
	}

	@Test
	void moveStonesThroughPits__should_move_stones_until_emtpy_skipping_opponents_big_pit() {
		var pits = game.getPits();
		game.moveStonesThroughPits("one", "two", "pit-one-4");
		game.moveStonesThroughPits("one", "two", "pit-one-5");
		game.moveStonesThroughPits("one", "two", "pit-one-6");

		assertThat("pit-one-1", pits.get("pit-one-1"), is(7));
		assertThat("pit-one-2", pits.get("pit-one-2"), is(6));
		assertThat("pit-one-3", pits.get("pit-one-3"), is(6));
		assertThat("pit-one-4", pits.get("pit-one-4"), is(0));
		assertThat("pit-one-5", pits.get("pit-one-5"), is(0));
		assertThat("pit-one-6", pits.get("pit-one-6"), is(0));
		assertThat("pit-one-big", pits.get("pit-one-big"), is(3));

		assertThat("pit-two-1", pits.get("pit-two-1"), is(9));
		assertThat("pit-two-2", pits.get("pit-two-2"), is(9));
		assertThat("pit-two-3", pits.get("pit-two-3"), is(9));
		assertThat("pit-two-4", pits.get("pit-two-4"), is(8));
		assertThat("pit-two-5", pits.get("pit-two-5"), is(8));
		assertThat("pit-two-6", pits.get("pit-two-6"), is(7));
		assertThat("pit-two-big", pits.get("pit-two-big"), is(0));
	}

	@Test
	void moveStonesThroughPits__should_steal_opponents_stones_if_ends_on_empty_own_small_pit() {
		var pits = game.getPits();
		game.moveStonesThroughPits("one", "two", "pit-one-1");
		game.moveStonesThroughPits("one", "two", "pit-one-2");
		game.moveStonesThroughPits("one", "two", "pit-one-6");
		// ends in pit-one-1, which has 0 stones, adding 1
		// pit-two-6 has 7 stones. goes to 0.
		// adds 7 to pit-one-1. total is 8

		assertThat("pit-one-1", pits.get("pit-one-1"), is(8));
		assertThat("pit-one-2", pits.get("pit-one-2"), is(0));
		assertThat("pit-one-3", pits.get("pit-one-3"), is(8));
		assertThat("pit-one-4", pits.get("pit-one-4"), is(8));
		assertThat("pit-one-5", pits.get("pit-one-5"), is(8));
		assertThat("pit-one-6", pits.get("pit-one-6"), is(0));
		assertThat("pit-one-big", pits.get("pit-one-big"), is(3));

		assertThat("pit-two-1", pits.get("pit-two-1"), is(8));
		assertThat("pit-two-2", pits.get("pit-two-2"), is(8));
		assertThat("pit-two-3", pits.get("pit-two-3"), is(7));
		assertThat("pit-two-4", pits.get("pit-two-4"), is(7));
		assertThat("pit-two-5", pits.get("pit-two-5"), is(7));
		assertThat("pit-two-6", pits.get("pit-two-6"), is(0));
		assertThat("pit-two-big", pits.get("pit-two-big"), is(0));
	}

	@Test
	void moveStonesThroughPits__should_return_true_only_when_ended_in_players_big_pit() {
		boolean extraTurnEarned = game.moveStonesThroughPits("one", "two", "pit-one-1");
		assertThat(extraTurnEarned, is(true));
		extraTurnEarned = game.moveStonesThroughPits("two", "one", "pit-two-1");
		assertThat(extraTurnEarned, is(true));
		extraTurnEarned = game.moveStonesThroughPits("one", "two", "pit-one-3");
		assertThat(extraTurnEarned, is(false));
	}

	@Test
	void moveAllStonesToBigPits__should_move_every_remaining_stone_into_the_corresponding_players_big_pit() {
		rangeClosed(1, 6).forEach(i -> {
			game.getPits().put("pit-one-" + i, 3);
			game.getPits().put("pit-two-" + i, 9);
		});

		assertThat(game.getPits().get("pit-one-big"), is(0));
		assertThat(game.getPits().get("pit-two-big"), is(0));
		game.moveAllStonesToBigPits();

		assertThat(game.getPits().get("pit-one-big"), is(3 * 6));
		assertThat(game.getPits().get("pit-two-big"), is(9 * 6));
		rangeClosed(1, 6).forEach(i -> {
			assertThat(game.getPits().get("pit-one-" + i), is(0));
			assertThat(game.getPits().get("pit-two-" + i), is(0));
		});
	}

	@Test
	void canMakeMove__should_return_true_only_if_players_pit_and_turn() {
		game.setState(PLAYER_TWOS_TURN);
		assertThat("opponents turn and opponents pit", game.canMakeMove("one", "pit-one-1"), is(false));
		assertThat("opponents turn and players pit", game.canMakeMove("one", "pit-two-1"), is(false));
		assertThat("players turn and opponents pit", game.canMakeMove("two", "pit-one-1"), is(false));

		assertThat("players turn and players pit", game.canMakeMove("two", "pit-two-1"), is(true));
	}

	@Test
	void makeMove__should_only_change_pits_if_players_own_turn_and_pit() {
		game.setState(PLAYER_TWOS_TURN);
		game.makeMove(playerOne, "pit-one-1");
		this.assertPitsInStartingState(true);

		game.setState(PLAYER_ONES_TURN);
		game.makeMove(playerTwo, "pit-two-1");
		this.assertPitsInStartingState(true);

		game.makeMove(playerOne, "pit-two-1");
		this.assertPitsInStartingState(true);

		game.makeMove(playerOne, "pit-one-1");
		this.assertPitsInStartingState(false);
	}

	@Test
	void anyPlayersPitsAreAllEmpty__should_only_return_true_if_any_players_pits_are_all_empty() {
		assertThat("initial state should have 6 stones each", game.anyPlayersPitsAreAllEmpty(), is(false));

		game.getPits().put("pit-one-1", 1);
		game.getPits().put("pit-one-2", 0);
		game.getPits().put("pit-one-3", 0);
		game.getPits().put("pit-one-4", 0);
		game.getPits().put("pit-one-5", 0);
		game.getPits().put("pit-one-6", 0);
		game.getPits().put("pit-two-1", 1);
		game.getPits().put("pit-two-2", 0);
		game.getPits().put("pit-two-3", 0);
		game.getPits().put("pit-two-4", 0);
		game.getPits().put("pit-two-5", 0);
		game.getPits().put("pit-two-6", 0);
		assertThat("a single pit should have one stone", game.anyPlayersPitsAreAllEmpty(), is(false));

		game.getPits().put("pit-one-1", 0);
		game.getPits().put("pit-two-1", 1);
		assertThat("player ones pits should all be zeroes", game.anyPlayersPitsAreAllEmpty(), is(true));

		game.getPits().put("pit-one-1", 1);
		game.getPits().put("pit-two-1", 0);
		assertThat("player twos pits should all be zeroes", game.anyPlayersPitsAreAllEmpty(), is(true));

		game.getPits().put("pit-one-1", 0);
		game.getPits().put("pit-two-1", 0);
		assertThat("both players pits should all be zeroes", game.anyPlayersPitsAreAllEmpty(), is(true));
	}

	@Test
	void getOppositePitKey__should_return_valid_key_for_opposing_player_and_mirrored_index() {
		rangeClosed(1, 6).forEach(i -> {
			var expectedTwoKey = "pit-two-" + (7 - i);
			assertThat(game.getOppositePitKey("anything! goes-here, but..-" + i, "two"), is(expectedTwoKey));

			var expectedOneKey = "pit-one-" + (7 - i);
			assertThat(game.getOppositePitKey("must &nd with híphen and #-" + i, "one"), is(expectedOneKey));
		});
	}

	@Test
	void goToNextState__should_change_to_opposing_players_turn_if_not_finished() {
		assertThat(game.getState(), is(PLAYER_ONES_TURN));
		game.goToNextState();
		assertThat(game.getState(), is(PLAYER_TWOS_TURN));
		game.goToNextState();
		assertThat(game.getState(), is(PLAYER_ONES_TURN));
		game.goToNextState();
		assertThat(game.getState(), is(PLAYER_TWOS_TURN));
	}

	@Test
	void goToNextState__should_change_to_finished_if_game_ended() {
		rangeClosed(1, 6).forEach(i -> game.getPits().put("pit-one-" + i, 0));

		game.setState(PLAYER_ONES_TURN);
		game.goToNextState();
		assertThat(game.getState(), is(FINISHED));

		game.setState(PLAYER_TWOS_TURN);
		game.goToNextState();
		assertThat(game.getState(), is(FINISHED));
	}

	private void assertPitsInStartingState(boolean isInStartingState) {
		var pits = game.getSortedPits();

		List<Integer> pitValues = pits.stream().map(pit -> pit.getValue()).collect(toList());
		if (isInStartingState) {
			assertThat("pit values changed", pitValues, contains(
				6, 6, 6, 6, 6, 6, 0,
				6, 6, 6, 6, 6, 6, 0)
			);
		} else {
			assertThat("pit values should have changed", pitValues, not(contains(
				6, 6, 6, 6, 6, 6, 0,
				6, 6, 6, 6, 6, 6, 0)
			));
		}
	}
}
