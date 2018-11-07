package com.marcosalles.bolgame.model.entity;

import com.marcosalles.bolgame.model.EventType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.marcosalles.bolgame.model.entity.GameState.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class GameStateTest {

	@Test
	public void values__didnt_change() {
		assertThat(Arrays.asList(GameState.values()), contains(PLAYER_ONES_TURN, PLAYER_TWOS_TURN, FINISHED));
	}

	@Test
	public void next__should_return_other_players_turn_if_not_finished() {
		final boolean finished = false;
		assertThat(PLAYER_ONES_TURN.next(finished), is(PLAYER_TWOS_TURN));
		assertThat(PLAYER_TWOS_TURN.next(finished), is(PLAYER_ONES_TURN));
	}

	@Test
	public void next__should_return_finished_if_finished() {
		final boolean finished = true;
		assertThat(PLAYER_ONES_TURN.next(finished), is(FINISHED));
		assertThat(PLAYER_TWOS_TURN.next(finished), is(FINISHED));
	}

	@Test
	public void next__should_return_other_self_if_not_turn_state() {
		assertThat(FINISHED.next(true), is(FINISHED));
		assertThat(FINISHED.next(false), is(FINISHED));
	}

	@Test
	public void isAllowed__should_return_false_if_finished() {
		assertThat(FINISHED.isAllowed(null), is(false));
		assertThat(FINISHED.isAllowed(""), is(false));
		assertThat(FINISHED.isAllowed("one"), is(false));
		assertThat(FINISHED.isAllowed("two"), is(false));
	}

	@Test
	public void isAllowed__should_return_false_if_not_one_or_two() {
		for (GameState state : GameState.values()) {
			assertThat(state.isAllowed(null), is(false));
			assertThat(state.isAllowed(""), is(false));
			assertThat(state.isAllowed("player"), is(false));
			assertThat(state.isAllowed("turn"), is(false));
			assertThat(state.isAllowed("fin"), is(false));
		}
	}

	@Test
	public void isAllowed__should_return_false_if_not_same_player() {
		assertThat(PLAYER_ONES_TURN.isAllowed("two"), is(false));
		assertThat(PLAYER_TWOS_TURN.isAllowed("one"), is(false));
	}

	@Test
	public void isAllowed__should_return_true_if_same_player() {
		assertThat(PLAYER_ONES_TURN.isAllowed("one"), is(true));
		assertThat(PLAYER_TWOS_TURN.isAllowed("two"), is(true));
	}

	@Test
	public void getEventToFire__should_return_event_type() {
		assertThat(PLAYER_ONES_TURN.getEventToFire(), is(EventType.TURN_OVER));
		assertThat(PLAYER_TWOS_TURN.getEventToFire(), is(EventType.TURN_OVER));
		assertThat(FINISHED.getEventToFire(), is(EventType.GAME_FINISHED));
	}
}