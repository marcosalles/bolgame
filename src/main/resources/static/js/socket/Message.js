class Message {
	constructor(contents, playerId) {
		this.contents = contents;
		this.playerId = playerId;
	}

	getPayload() {
		return {
			playerId: this.playerId,
			contents: this.contents
		};
	}

}