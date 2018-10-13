class SearchGameHandler {
	constructor() {
		this.elements = {
			form: $('#register-for-game'),
			username: $('#username')
		};
		this.endpoints = {
			register: {
				send: '/player/register',
				subscribe: '/player/registered'
			}
		};
	}

	registerPlayer() {
		const player = {username: this.elements.username.val()};
		this.socket.send(this.endpoints.register, player);
	}

	didRegister(player) {
		console.log(`SearchGameHandler::didRegister(${player})`);


	}

	setupSubscriptions() {
		this.socket.subscribe(this.endpoints.register.subscribe, player => this.didRegister(player));
	}

	setupListeners() {
		this.elements.form.on('submit', event => {
			event.preventDefault();
			this.registerPlayer();
		});
	}

	initialize(socket) {
		this.socket = socket;
		this.setupListeners();
	}
}

const searchGameHandler = new SearchGameHandler();
socketHandler.initialize([searchGameHandler]);
