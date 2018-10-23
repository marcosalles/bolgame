const GameState = {
	STARTED: 'STARTED',
	PLAYER_ONES_TURN: 'PLAYER_ONES_TURN',
	PLAYER_TWOS_TURN: 'PLAYER_TWOS_TURN',
	FINISHED: 'FINISHED'
};
class GameLogic {
	constructor() {
		console.log(`>> GameLogic::constructor() <<`);
		this.elements = {
			players: {
				one: $('#playerOne'),
				two: $('#playerTwo')
			},
			board: $('#game')
		};

		this.endpoints = {
			turn: {
				send: id => `user/${id}/game/makemove`,
				subscribe: id => `user/${id}/game/turnover`
			},
			finished: {
				subscribe: id => `user/${id}/game/finished`
			}
		}
	}

	startGame(game) {
		console.log(`>> GameLogic::startGame(${JSON.stringify(game)}}) <<`);

		this.socket.subscribe(
			this.endpoints.turn.subscribe(this.player.id),
			result => this.turnEnded(result));
		this.socket.subscribe(
			this.endpoints.finished.subscribe(this.player.id),
			result => this.gameFinished(result));

		this.game = {
			id: game.id,
			playerOne: game.playerOne,
			playerTwo: game.playerTwo,
			state: game.state
		};

		this.elements.board.on('click', event => {
			console.log(`Tapped ${event.target.id}`);
		});

		this.setPlayerNames();

		this.updateBoardInteraction();
	}

	thisPlayersTurnState() {
		return this.thisIsPlayerOne() ? GameState.PLAYER_ONES_TURN : GameState.PLAYER_TWOS_TURN;
	}

	thisIsPlayerOne() {
		return this.player.id == this.game.playerOne.id;
	}

	thisPlayersTurn() {
		return this.game.state == this.thisPlayersTurnState();
	}

	updateBoardInteraction() {
		let pointerEvents = 'none';
		if (this.thisPlayersTurn()) {
			pointerEvents = 'auto';
		}
		this.elements.board.css('pointer-events', pointerEvents);
	}

	setPlayerNames() {
		this.elements.players.one.text(this.game.playerOne.username);
		this.elements.players.two.text(this.game.playerTwo.username);
		const playerLabel = this.thisIsPlayerOne() ? this.elements.players.one : this.elements.players.two;
		playerLabel.text(`${playerLabel.text()} (YOU)`);
	}

	turnEnded(result) {
		console.log(`>> GameLogic::turnEnded(${JSON.stringify(result)}}) <<`);

	}

	gameFinished(result) {
		console.log(`>> GameLogic::gameFinished(${JSON.stringify(result)}) <<`);

	}

	initialize(socket, player) {
		this.player = player;
		this.socket = socket;
	}
}