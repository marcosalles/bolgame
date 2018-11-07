class HomeHandler {
	constructor() {
		this.socket = new SocketHandler();
		this.player = undefined;
		this.elements = {
			homeCall: $('#home-call')
		};
		this.endpoints = {
			user: {
				confirmation: {
					send: (id) => `user/${id}/home/user/confirm`,
					subscribe: (id) => `user/${id}/home/user/confirmed`
				},
				notFound: {
					subscribe: (id) => `user/${id}/home/user/not-found`
				}
			}
		}
	}

	confirmed(result) {
		this.player = result.player;
		this.socket.unsubscribe(this.endpoints.user.confirmation.subscribe(this.player.id));
		this.changeCallText(`I just confirmed you are real, ${this.player.username}!`, () => {
			if (result.game) {
				const game = result.game;
				const opponent = this.player.id == game.playerOne.id ? game.playerTwo : game.playerOne;
				this.changeCallText(`And I found a game in progress for you!`, () => {
					const continueGame = confirm(`Continue game against ${opponent.username}?`);
					if (continueGame) {
						this.changeCallText('Okay, I will redirect you to your game shortly!', () => {
							//TODO redirect to game
							console.error('//TODO redirect to game');
						});
					} else {
						this.changeCallText('Okay, I will make sure that game is abandoned.', () => {
							//TODO delete game and ask for play
							console.error('//TODO delete game and ask for play');
						})
					}
				});
			} else {
				this.changeCallText('Do you want me to find you a game?');
			}
		});
	}

	unconfirmed(confirmationId) {
		this.socket.unsubscribe(this.endpoints.user.notFound.subscribe(confirmationId));
		this.changeCallText(`I couldn't confirm your identity, so let's create a new one!`);
	}


	tryToConfirmPlayer(player) {
		this.socket.subscribe(this.endpoints.user.confirmation.subscribe(player.id), (result) => this.confirmed(result));
		this.socket.subscribe(this.endpoints.user.notFound.subscribe(player.id), () => this.unconfirmed(player.id));
		this.changeCallText(`I just found logs of a player named ${player.username} around here. Wait while I confirm this player exists indeed!`,
			() => this.socket.send(this.endpoints.user.confirmation.send(player.id))
		);
	}

	askForPlayerUsername() {
		this.elements.player.form.show();
		const player = Helper.STORAGE.getPlayerData();
		if (!player.id) {
			this.changeCallText('');
		} else {
			this.tryToConfirmPlayer(player);
		}
	}

	connected() {
		this.changeCallText('Hello there! I am Max and I will be your guide.', () => this.askForPlayerUsername());
	}

	changeCallText(callText, callback, delay = 1000) {
		this.elements.homeCall.html(callText);
		if (callback) setTimeout(() => callback(), delay);
	}

	initialize() {
		this.socket.initialize();
		this.socket.connect(() => this.connected());
	}
}

new HomeHandler().initialize();