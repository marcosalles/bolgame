package com.marcosalles.bolgame.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message<T extends Object> implements Serializable {

	private String playerId;
	private T contents;
}
