class GameSearchHandler {
	constructor() {
		console.log(`>> GameSearchHandler::constructor() <<`);

		this.player = {};
		this.socket = undefined;
		this.elements = {
			playerHeader: $('#player-header')
		};
		this.endpoints = {
			search: {
				send: id => `user/${id}/game/search`,
				subscribe: id => `user/${id}/game/started`
			}
		}
	}

	initialize(socket, player) {
		this.player = player;
		this.socket = socket;
	}

	gameStarted(game, gameStartedCallback) {
		console.log(`GameSearchHandler::gameStarted(${game.id})`);
		this.socket.unsubscribe(this.endpoints.search.subscribe(this.player.id));

		gameStartedCallback(game);
	}

	startSearch(gameStartedCallback) {
		console.log(`GameSearchHandler::startSearch()`);
		this.elements.playerHeader.html(`<em>${this.player.username}</em> is looking for a game...`);
		this.socket.subscribe(
			this.endpoints.search.subscribe(this.player.id),
			game => this.gameStarted(game, gameStartedCallback));

		const startedAt = new Date().toUTCString();
		this.socket.send(this.endpoints.search.send(this.player.id), new Message(startedAt, this.player.id));
	}
}