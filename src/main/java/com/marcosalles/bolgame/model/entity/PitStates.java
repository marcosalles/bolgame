package com.marcosalles.bolgame.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class PitStates implements Serializable {

	private Map<String, Integer> pits;

	public PitStates(boolean brandNewGame) {
		if (brandNewGame) {
			this.pits = new HashMap<>();
			this.pits.put("one-big", 0);
			this.pits.put("one-small-1", 6);
			this.pits.put("one-small-2", 6);
			this.pits.put("one-small-3", 6);
			this.pits.put("one-small-4", 6);
			this.pits.put("one-small-5", 6);
			this.pits.put("one-small-6", 6);
			this.pits.put("two-big", 0);
			this.pits.put("two-small-1", 6);
			this.pits.put("two-small-2", 6);
			this.pits.put("two-small-3", 6);
			this.pits.put("two-small-4", 6);
			this.pits.put("two-small-5", 6);
			this.pits.put("two-small-6", 6);
		}
	}
}
