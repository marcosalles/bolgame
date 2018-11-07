package com.marcosalles.bolgame.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.marcosalles.bolgame.model.EventType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventTypeTest {

	@Test
	public void values__didnt_change() {
		assertThat(Arrays.asList(EventType.values()), contains(
			REFRESH,
			PLAYER_REGISTERED,
			QUEUE_UPDATED,
			GAME_STARTED,
			TURN_OVER,
			GAME_FINISHED,
			SCORE_SAVED
		));
	}

	@Test
	void triggersAutoMessage__should_return_false_if_no_endpoint() {
		assertThat(QUEUE_UPDATED.triggersAutoMessage(), is(false));
	}

	@Test
	void triggersAutoMessage__should_return_true_if_endpoint_present() {
		assertThat(REFRESH.triggersAutoMessage(), is(true));
	}

	@Test
	void getEndpoint__should_throw_usopex_if_no_enpoint() {
		assertThrows(
			UnsupportedOperationException.class,
			() -> QUEUE_UPDATED.getEndpoint(""),
			"No endpoint here"
		);
	}

	@Test
	void getEndpoint__should_build_endpoint_format_starting_with_user_and_hash() {
		for (EventType type : EventType.values()) {
			if (type.triggersAutoMessage()) {
				assertThat(type.getEndpoint("123"), startsWith("/user/123/"));
			}
		}
	}
}