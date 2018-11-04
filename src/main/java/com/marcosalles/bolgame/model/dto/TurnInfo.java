package com.marcosalles.bolgame.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TurnInfo {
	private final String pitId;
	private final String gameId;
	private final String playerId;
}
