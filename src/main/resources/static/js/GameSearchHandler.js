class GameSearchHandler {
	constructor() {
		console.log(`>> GameSearchHandler::constructor() <<`);

		this.player = {};
		this.socket = undefined;
		this.endpoints = {
			search: {
				send: id => `user/${id}/game/search`,
				subscribe: id => `user/${id}/game/started`
			}
		}
	}

	initialize(socket, player, gameStartedCallback) {
		this.player = player;
		this.socket = socket;
		this.socket.subscribe(this.endpoints.search.subscribe(this.player.id), result => gameStartedCallback(result));
	}

	startSearch() {
		console.log(`GameSearchHandler::startSearch()`);

		const startedAt = new Date().toUTCString();
		this.socket.send(this.endpoints.search.send(this.player.id), new Message(startedAt, this.player.id));
	}
}