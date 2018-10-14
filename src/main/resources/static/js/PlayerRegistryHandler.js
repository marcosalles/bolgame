class PlayerRegistryHandler {
	constructor() {
		console.log(`>> PlayerRegistryHandler::constructor() <<`);
		this.elements = {
			form: $('#register-for-game'),
			username: $('#username')
		};
		this.endpoints = {
			register: {
				send: 'game/register-player',
				subscribe: 'sub/registered-player'
			}
		};
	}

	registerPlayer(callback) {
		console.log(`>> PlayerRegistryHandler::registerPlayer(${callback}) <<`);
		this.socket.subscribe(this.endpoints.register.subscribe, player => {
			this.didRegister(player, callback);
		});

		const playerId = Constants.HELPERS.getIdFromCookie(Constants.COOKIES.playerId);
		const playerName = this.elements.username.val();
		this.socket.send(this.endpoints.register.send, new Message(playerName, playerId));
	}

	didRegister(player, callback) {
		console.log(`>> PlayerRegistryHandler::didRegister(${player}, ${callback}) <<`);

		Constants.HELPERS.setIdToCookie({
			name: Constants.COOKIES.playerId,
			value: player.id,
			expiresInDays: 180
		});

		if (callback) callback(player);
	}

	initialize(socket, callback) {
		console.log(`>> PlayerRegistryHandler::initialize(${socket}, ${callback}) <<`);
		this.socket = socket;
		this.elements.form.on('submit', event => {
			event.preventDefault();
			this.registerPlayer(callback);
		});
	}
}