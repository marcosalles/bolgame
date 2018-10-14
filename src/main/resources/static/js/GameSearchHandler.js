class GameSearchHandler {
	constructor() {
		console.log(`>> GameSearchHandler::constructor() <<`);

		this.player = {};
		this.socket = undefined;
		this.endpoints = {
			search: {
				send: id => `user/${id}/game/search`,
				subscribe: id => `user/${id}/game/searching`
			},
			gameFound: {
				subscribe: id => `user/${id}/game/found`
			}
		}
	}

	initialize(socket, player) {
		console.log(`>> GameSearchHandler::initialize(${socket}, ${player}}) <<`);

		this.player = player;
		this.socket = socket;
		this.socket.subscribe(this.endpoints.search.subscribe(this.player.id), this.didStartSearching);
		this.socket.subscribe(this.endpoints.gameFound.subscribe(this.player.id), this.didFindGame);
	}

	startSearch() {
		console.log(`GameSearchHandler::startSearch()`);

		const startedAt = new Date().toUTCString();
		this.socket.send(this.endpoints.search.send(this.player.id), new Message(startedAt, this.player.id));
	}

	didStartSearching(result) {
		console.log(`GameSearchHandler::didStartSearching(${result})`);
	}

	didFindGame(result) {
		console.log(`GameSearchHandler::didFindGame(${result})`);
	}
}