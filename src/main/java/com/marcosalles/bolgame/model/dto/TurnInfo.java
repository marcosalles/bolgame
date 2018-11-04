package com.marcosalles.bolgame.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TurnInfo {
	private String pitId;
	private String gameId;
	private String playerId;
}
