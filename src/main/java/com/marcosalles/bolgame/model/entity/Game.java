package com.marcosalles.bolgame.model.entity;

import com.marcosalles.bolgame.converter.PitsJsonConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.sql.RowSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.marcosalles.bolgame.model.entity.GameState.PLAYER_ONES_TURN;
import static java.util.Comparator.comparing;
import static java.util.stream.IntStream.rangeClosed;

@Entity
@Table(name = "games")
@Getter
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
	private GameState state = PLAYER_ONES_TURN;

	@Convert(converter = PitsJsonConverter.class)
	private Map<String, Integer> pits;

	@Builder
	public Game(String id, Player playerOne, Player playerTwo) {
		this.id = id;
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;

		this.pits = new HashMap<>();
		this.pits.put("pit-one-1", 6);
		this.pits.put("pit-one-2", 6);
		this.pits.put("pit-one-3", 6);
		this.pits.put("pit-one-4", 6);
		this.pits.put("pit-one-5", 6);
		this.pits.put("pit-one-6", 6);
		this.pits.put("pit-one-big", 0);
		this.pits.put("pit-two-1", 6);
		this.pits.put("pit-two-2", 6);
		this.pits.put("pit-two-3", 6);
		this.pits.put("pit-two-4", 6);
		this.pits.put("pit-two-5", 6);
		this.pits.put("pit-two-6", 6);
		this.pits.put("pit-two-big", 0);
	}

	public List<Map.Entry<String, Integer>> getSortedPits() {
		return this.pits.entrySet()
			.stream()
			.sorted(comparing(Map.Entry::getKey))
			.collect(Collectors.toList());
	}

	public boolean makeMove(Player player, String pitId) {
		String currentPlayersPlace = this.playerOne.equals(player) ? "one" : "two";
		String otherPlayersPlace = this.playerOne.equals(player) ? "two" : "one";
		if (this.canMakeMove(currentPlayersPlace, pitId)) {
			boolean extraTurnEarned = this.moveStonesThroughPits(currentPlayersPlace, otherPlayersPlace, pitId);
			if (this.anyPlayersPitsAreAllEmpty()) {
				this.moveAllStonesToBigPits();
				this.goToNextState();
			} else if (!extraTurnEarned) {
				this.goToNextState();
			}
			return true;
		}
		return false;
	}

	protected void moveAllStonesToBigPits() {
		rangeClosed(1, 6).forEach(i -> {
			int pitOneValue = this.pits.replace("pit-one-" + i, 0);
			this.pits.put("pit-one-big", this.pits.get("pit-one-big") + pitOneValue);
			int pitTwoValue = this.pits.replace("pit-two-" + i, 0);
			this.pits.put("pit-two-big", this.pits.get("pit-two-big") + pitTwoValue);
		});
	}

	protected boolean anyPlayersPitsAreAllEmpty() {
		boolean allOfPlayerOnesPitsAreEmpty = rangeClosed(1, 6).map(i -> this.pits.get("pit-one-" + i)).allMatch(n -> n == 0);
		boolean allOfPlayerTwosPitsAreEmpty = rangeClosed(1, 6).map(i -> this.pits.get("pit-two-" + i)).allMatch(n -> n == 0);
		return allOfPlayerOnesPitsAreEmpty || allOfPlayerTwosPitsAreEmpty;
	}

	protected boolean moveStonesThroughPits(String currentPlayersPlace, String otherPlayersPlace, String pitId) {
		int stonesToMove = this.pits.replace(pitId, 0);
		final var sortedPits = this.getSortedPits();
		int index = sortedPits.indexOf(Map.entry(pitId, 0)) + 1;
		while (stonesToMove > 0) {
			if (index >= sortedPits.size()) {
				index = 0;
			}
			var pit = sortedPits.get(index);

			var isBigPit = pit.getKey().endsWith("-big");
			var isPlayersPit = pit.getKey().contains(currentPlayersPlace);
			if (isBigPit && !isPlayersPit) {
				index++;
				continue;
			}
			this.pits.put(pit.getKey(), pit.getValue() + 1);
			index++;
			stonesToMove--;
			if (stonesToMove == 0) {
				if (isBigPit && isPlayersPit) {
					return true;
				}

				if (isPlayersPit && !isBigPit && pit.getValue() == 1) {
					var oppositePitKey = this.getOppositePitKey(pit.getKey(), otherPlayersPlace);
					var oppositePitValue = this.pits.replace(oppositePitKey, 0);
					this.pits.put(pit.getKey(), oppositePitValue + 1);
				}
			}
		}
		return false;
	}

	protected String getOppositePitKey(String pitId, String otherPlayersPlace) {
		int pitNumber = 7 - Integer.valueOf(pitId.replaceAll(".*-", ""));
		return String.format("pit-%s-%d", otherPlayersPlace, pitNumber);
	}

	protected boolean canMakeMove(String playersPlace, String pitId) {
		return this.state.isAllowed(playersPlace) && pitId.contains(playersPlace);
	}

	protected void goToNextState() {
		boolean finished = this.anyPlayersPitsAreAllEmpty();
		this.setState(this.state.next(finished));
	}

	public Score buildScore() {
		var playerOneScore = this.pits.get("pit-one-big");
		var playerTwoScore = this.pits.get("pit-two-big");
		Player winner = null;
		if (playerOneScore > playerTwoScore) {
			winner = this.playerOne;
		} else if (playerOneScore < playerTwoScore) {
			winner = this.playerTwo;
		}
		return Score.builder()
			.game(this)
			.playerOneScore(playerOneScore)
			.playerTwoScore(playerTwoScore)
			.winner(winner)
			.build();
	}

	public boolean is(GameState state) {
		return this.state.equals(state);
	}

	public Player getOpponent(Player player) {
		return playerOne.equals(player) ? playerTwo : playerOne;
	}
}
