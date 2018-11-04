class Message {
	constructor(contents, playerId) {
		this.contents = contents;
		this.playerId = playerId;
	}

	getPayload() {
		const payload = {};
		if (this.contents) payload.contents = this.contents;
		if (this.playerId) payload.playerId = this.playerId;
		return payload;
	}

}