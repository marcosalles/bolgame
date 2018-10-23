class PlayerRegistryHandler {
	constructor() {
		console.log(`>> PlayerRegistryHandler::constructor() <<`);
		this.elements = {
			form: $('#register-for-game'),
			username: $('#username')
		};
		this.endpoints = {
			register: {
				send: (hash) => `user/${hash}/register`,
				subscribe: (hash) => `user/${hash}/registered`
			}
		};
	}

	registerPlayer(username, callback) {
		console.log(`>> PlayerRegistryHandler::registerPlayer(${username}) <<`);
		const registryHash = Helper.FUNCTIONS.random();
		this.endpoints.register.sub = this.socket.subscribe(this.endpoints.register.subscribe(registryHash), player => {
			this.didRegister(player, callback);
		});

		const player = Helper.STORAGE.getPlayerData();
		this.socket.send(this.endpoints.register.send, new Message(username, player.id));
	}

	didRegister(player, callback) {
		console.log(`>> PlayerRegistryHandler::didRegister(${JSON.stringify(player)}) <<`);
		this.endpoints.register.sub.unsubscribe();

		Helper.STORAGE.savePlayerData(player);
		callback(player);
	}

	initialize(socket, playerRegisteredCallback) {
		this.socket = socket;
		this.elements.form.on('submit', event => {
			const username = this.elements.username.val();
			this.elements.username.val('');
			if (username.length >= 3) {
				event.preventDefault();
				this.elements.form.remove();
				this.registerPlayer(username, playerRegisteredCallback);
			}
		});
	}
}