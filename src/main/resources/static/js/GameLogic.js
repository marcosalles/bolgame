const GameState = {
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
			player: undefined,
			opponent: undefined
		};

		this.elements = {
			playerHeader: $('#player-header'),
			players: {
				one: $('#game-player-one'),
				two: $('#game-player-two')
			},
			game: {
				board: $('.game'),
				turnMarker: $('#players-turn'),
				pitsFor: (player) => $(`.pit[data-owner="${player}"]:not(data-stones="0"])`),
				allPits: $('.pit'),
				pitWithId: (id) => $(`.pit#${id}`)
			}
		};

		this.endpoints = {
			turn: {
				send: id => `user/${id}/game/makemove`,
				subscribe: id => `user/${id}/game/turnover`
			},
			refresh: {
				subscribe: id => `user/${id}/game/refresh`
			},
			finished: {
				subscribe: id => `user/${id}/game/finished`
			},
			scoreSaved: {
				subscribe: id => `user/${id}/game/score/saved`
			}
		}
	}

	startGame(game) {
		console.log(`>> GameLogic::startGame(${JSON.stringify(game)}}) <<`);
		this.disableBoard();
		Helper.FUNCTIONS.actions.hideAction(Helper.KEYS.actions.scores);
		Helper.FUNCTIONS.actions.showAction(Helper.KEYS.actions.quit);

		this.socket.subscribe(
			this.endpoints.refresh.subscribe(this.player.id),
			result => this.turnOver(result));
		this.socket.subscribe(
			this.endpoints.turn.subscribe(this.player.id),
			result => this.turnOver(result));
		this.socket.subscribe(
			this.endpoints.finished.subscribe(this.player.id),
			result => this.gameFinished(result));
		this.socket.subscribe(
			this.endpoints.scoreSaved.subscribe(this.player.id),
			result => this.scoreSaved(result));

		this.elements.game.board.removeClass('game--inactive');

		this.updateGame(game);

		this.turnOrder.player = this.thisIsPlayerOne() ? 'one' : 'two';
		this.turnOrder.opponent = this.thisIsPlayerOne() ? 'two' : 'one';

		this.setPlayerNames();

		this.elements.game.pitsFor(this.turnOrder.player).on('click', event => {
			this.sendMove($(event.target));
		});

		this.updateBoard();
		this.hideActions();
	}

	updateGame(game) {
		this.game = {
			id: game.id,
			playerone: game.playerOne,
			playertwo: game.playerTwo,
			state: game.state,
			pits: game.pits
		};
	}

	thisPlayersTurnState() {
		return this.thisIsPlayerOne() ? GameState.PLAYER_ONES_TURN : GameState.PLAYER_TWOS_TURN;
	}

	thisIsPlayerOne() {
		return this.player.id == this.game.playerone.id;
	}

	thisPlayersTurn() {
		return this.game.state == this.thisPlayersTurnState();
	}

	disableBoard() {
		this.elements.game.allPits.css('pointer-events', 'none');
	}

	enableBoard() {
		let events = 'none';
		if (this.thisPlayersTurn()) {
			events = 'auto';
		}
		this.elements.game.pitsFor(this.turnOrder.player).css('pointer-events', events);
	}

	updateTurnMarker(score) {
		if (score) {
			let message = 'It was a draw!';
			if (score.winner) {
				message = `The winner is <em>${score.winner.username}</em>!`;
			}
			this.elements.game.turnMarker.html(`Game finished! ${message}`);
		} else {
			const turnOwner = this.game.state == GameState.PLAYER_ONES_TURN ? 'one' : 'two';
			const currentPlayer = this.game[`player${turnOwner}`];
			this.elements.game.turnMarker.html(`Currently playing: <em>${currentPlayer.username}</em>`);
		}
	}

	updateBoard(game) {
		if (game) {
			this.updateGame(game);
		}
		this.updateTurnMarker();

		const pitValues = this.game.pits;
		Object.keys(pitValues).forEach(id => {
			this.elements.game.pitWithId(id).attr('data-stones', pitValues[id]);
		});
		this.enableBoard();
	}

	setPlayerNames() {
		let playerUsername = this.playerForOrder(this.turnOrder.player).username;
		let opponentUsername = this.playerForOrder(this.turnOrder.opponent).username;
		this.elements.playerHeader.html(`<em>${playerUsername}</em> is playing against <em>${opponentUsername}</em>`);

		this.elements.players.one.html(`Player 1: ${this.game.playerone.username}`);
		this.elements.players.two.html(`Player 2: ${this.game.playertwo.username}`);
		this.elements.players[this.turnOrder.player].append(' (YOU)');
	}

	playerForOrder(order) {
		return this.game[`player${order}`];
	}

	initialize(socket, player) {
		this.player = player;
		this.socket = socket;
	}

	sendMove(pit) {
		console.log(`>> GameLogic::sendMove ${pit.attr('id')} <<`);
		this.disableBoard();
		if (!this.thisPlayersTurn()) {
			alert('Nah nah nah!');
			return;
		}
		this.socket.send(
			this.endpoints.turn.send(this.player.id),
			new Message({
				pitId: pit.attr('id'),
				gameId: this.game.id,
				playerId: this.player.id
			})
		);
	}

	turnOver(game) {
		console.log(`>> GameLogic::turnOver(${JSON.stringify(game)}}) <<`);
		this.updateBoard(game);
	}

	gameFinished(game) {
		console.log(`>> GameLogic::gameFinished(${JSON.stringify(game)}) <<`);
		this.updateBoard(game);
		this.disableBoard();
	}

	scoreSaved(score) {
		console.log(`>> GameLogic::scoreSaved(${JSON.stringify(score)}) <<`);
		this.updateTurnMarker(score);
		Helper.FUNCTIONS.actions.showAction(Helper.KEYS.actions.play);
		Helper.FUNCTIONS.actions.hideAction(Helper.KEYS.actions.quit);
		Helper.FUNCTIONS.actions.showAction(Helper.KEYS.actions.scores);
	}
}