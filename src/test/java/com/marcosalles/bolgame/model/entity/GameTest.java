package com.marcosalles.bolgame.model.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.marcosalles.bolgame.model.entity.GameState.FINISHED;
import static com.marcosalles.bolgame.model.entity.GameState.PLAYER_ONES_TURN;
import static com.marcosalles.bolgame.model.entity.GameState.PLAYER_TWOS_TURN;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

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
		var pits = this.game.getPits();
		this.game.moveStonesThroughPits("one", "two", "pit-one-4");
		this.game.moveStonesThroughPits("one", "two", "pit-one-5");
		this.game.moveStonesThroughPits("one", "two", "pit-one-6");

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
		var pits = this.game.getPits();
		this.game.moveStonesThroughPits("one", "two", "pit-one-1");
		this.game.moveStonesThroughPits("one", "two", "pit-one-2");
		this.game.moveStonesThroughPits("one", "two", "pit-one-6");
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
		boolean extraTurnEarned = this.game.moveStonesThroughPits("one", "two", "pit-one-1");
		assertThat(extraTurnEarned, is(true));
		extraTurnEarned = this.game.moveStonesThroughPits("two", "one", "pit-two-1");
		assertThat(extraTurnEarned, is(true));
		extraTurnEarned = this.game.moveStonesThroughPits("one", "two", "pit-one-3");
		assertThat(extraTurnEarned, is(false));
	}

	@Test
	void moveAllStonesToBigPits__should_move_every_remaining_stone_into_the_corresponding_players_big_pit() {
		rangeClosed(1, 6).forEach(i -> {
			this.game.getPits().put("pit-one-"+i, 3);
			this.game.getPits().put("pit-two-"+i, 9);
		});

		assertThat(this.game.getPits().get("pit-one-big"), is(0));
		assertThat(this.game.getPits().get("pit-two-big"), is(0));
		this.game.moveAllStonesToBigPits();

		assertThat(this.game.getPits().get("pit-one-big"), is(3*6));
		assertThat(this.game.getPits().get("pit-two-big"), is(9*6));
		rangeClosed(1, 6).forEach(i -> {
			assertThat(this.game.getPits().get("pit-one-"+i), is(0));
			assertThat(this.game.getPits().get("pit-two-"+i), is(0));
		});
	}

	@Test
	void canMakeMove__should_return_true_only_if_players_pit_and_turn() {
		this.game.setState(PLAYER_TWOS_TURN);
		assertThat("opponents turn and opponents pit", this.game.canMakeMove("one", "pit-one-1"), is(false));
		assertThat("opponents turn and players pit", this.game.canMakeMove("one", "pit-two-1"), is(false));
		assertThat("players turn and opponents pit", this.game.canMakeMove("two", "pit-one-1"), is(false));

		assertThat("players turn and players pit", this.game.canMakeMove("two", "pit-two-1"), is(true));
	}

	@Test
	void makeMove__should_only_change_pits_if_players_own_turn_and_pit() {
		this.game.setState(PLAYER_TWOS_TURN);
		this.game.makeMove(playerOne, "pit-one-1");
		this.assertPitsInStartingState(true);

		this.game.setState(PLAYER_ONES_TURN);
		this.game.makeMove(playerTwo, "pit-two-1");
		this.assertPitsInStartingState(true);

		this.game.makeMove(playerOne, "pit-two-1");
		this.assertPitsInStartingState(true);

		this.game.makeMove(playerOne, "pit-one-1");
		this.assertPitsInStartingState(false);
	}

	@Test
	void anyPlayersPitsAreAllEmpty__should_only_return_true_if_any_players_pits_are_all_empty() {
		assertThat("initial state should have 6 stones each", this.game.anyPlayersPitsAreAllEmpty(), is(false));

		this.game.getPits().put("pit-one-1", 1);
		this.game.getPits().put("pit-one-2", 0);
		this.game.getPits().put("pit-one-3", 0);
		this.game.getPits().put("pit-one-4", 0);
		this.game.getPits().put("pit-one-5", 0);
		this.game.getPits().put("pit-one-6", 0);
		this.game.getPits().put("pit-two-1", 1);
		this.game.getPits().put("pit-two-2", 0);
		this.game.getPits().put("pit-two-3", 0);
		this.game.getPits().put("pit-two-4", 0);
		this.game.getPits().put("pit-two-5", 0);
		this.game.getPits().put("pit-two-6", 0);
		assertThat("a single pit should have one stone", this.game.anyPlayersPitsAreAllEmpty(), is(false));

		this.game.getPits().put("pit-one-1", 0);
		this.game.getPits().put("pit-two-1", 1);
		assertThat("player ones pits should all be zeroes", this.game.anyPlayersPitsAreAllEmpty(), is(true));

		this.game.getPits().put("pit-one-1", 1);
		this.game.getPits().put("pit-two-1", 0);
		assertThat("player twos pits should all be zeroes", this.game.anyPlayersPitsAreAllEmpty(), is(true));

		this.game.getPits().put("pit-one-1", 0);
		this.game.getPits().put("pit-two-1", 0);
		assertThat("both players pits should all be zeroes", this.game.anyPlayersPitsAreAllEmpty(), is(true));
	}

	@Test
	void getOppositePitKey__should_return_valid_key_for_opposing_player_and_mirrored_index() {
		rangeClosed(1, 6).forEach(i -> {
			var expectedTwoKey = "pit-two-" + (7 - i);
			assertThat(this.game.getOppositePitKey("anything! goes-here, but..-"+i, "two"), is(expectedTwoKey));

			var expectedOneKey = "pit-one-" + (7 - i);
			assertThat(this.game.getOppositePitKey("must &nd with híphen and #-"+i, "one"), is(expectedOneKey));
		});
	}

	@Test
	void goToNextState__should_change_to_opposing_players_turn_if_not_finished() {
		assertThat(this.game.getState(), is(PLAYER_ONES_TURN));
		this.game.goToNextState();
		assertThat(this.game.getState(), is(PLAYER_TWOS_TURN));
		this.game.goToNextState();
		assertThat(this.game.getState(), is(PLAYER_ONES_TURN));
		this.game.goToNextState();
		assertThat(this.game.getState(), is(PLAYER_TWOS_TURN));
	}

	@Test
	void goToNextState__should_change_to_finished_if_game_ended() {
		rangeClosed(1, 6).forEach(i -> this.game.getPits().put("pit-one-"+i, 0));

		this.game.setState(PLAYER_ONES_TURN);
		this.game.goToNextState();
		assertThat(this.game.getState(), is(FINISHED));

		this.game.setState(PLAYER_TWOS_TURN);
		this.game.goToNextState();
		assertThat(this.game.getState(), is(FINISHED));
	}

	private void assertPitsInStartingState(boolean isInStartingState) {
		var pits = this.game.getSortedPits();

		List<Integer> pitValues = pits.stream().map(pit -> pit.getValue()).collect(toList());
		if (isInStartingState) {
			assertThat("pit values changed", pitValues, contains(
				6,6,6,6,6,6,0,
				6,6,6,6,6,6,0)
			);
		} else {
			assertThat("pit values should have changed", pitValues, not(contains(
				6,6,6,6,6,6,0,
				6,6,6,6,6,6,0)
			));
		}
	}
}
