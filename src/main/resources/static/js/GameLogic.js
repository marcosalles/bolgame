const GameState = {
	STARTED: 'STARTED',
	PLAYER_ONES_TURN: 'PLAYER_ONES_TURN',
	PLAYER_TWOS_TURN: 'PLAYER_TWOS_TURN',
	FINISHED: 'FINISHED'
};
class GameLogic {
	constructor() {
		console.log(`>> GameLogic::constructor() <<`);
		this.player = {};
		this.socket = undefined;
		this.turnOrder = {
			player: 'one',
			opponent: 'two'
		};

		this.elements = {
			playerHeader: $('#player-header'),
			players: {
				one: $('#game-player-one'),
				two: $('#game-player-two')
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
			playerone: game.playerOne,
			playertwo: game.playerTwo,
			state: game.state
		};

		this.elements.board.on('click', '.action-packed', event => {
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
		this.turnOrder.player = this.thisIsPlayerOne() ? 'one' : 'two';
		this.turnOrder.opponent = this.thisIsPlayerOne() ? 'two' : 'one';

		let playerUsername = this.player.username;
		let opponentUsername = this.game[`player${this.turnOrder.opponent}`].username;
		this.elements.playerHeader.text(`<em>${playerUsername}</em> is playing against <em>${opponentUsername}</em>`);

		this.elements.players.one.text(this.game.playerone.username);
		this.elements.players.two.text(this.game.playertwo.username);
		this.elements.players[this.turnOrder.player].append(' (YOU)');
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