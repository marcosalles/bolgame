class SearchGameHandler {
	constructor() {
		this.elements = {
			form: $('#register-for-game'),
			username: $('#username')
		};
		this.endpoints = {
			register: {
				send: '/user/register',
				subscribe: '/user/registered'
			}
		};
	}

	registerUser() {
		const user = {username: this.elements.username.val()};
		this.socket.send(this.endpoints.register, user);
	}

	didRegister(user) {
		console.log(`SearchGameHandler::didRegister(${user})`);
	}

	setupSubscriptions() {
		this.socket.subscribe(this.endpoints.register.subscribe, user => this.didRegister(user));
	}

	setupListeners() {
		this.elements.form.on('submit', event => {
			event.preventDefault();
			this.registerUser();
		});
	}

	initialize(socket) {
		this.socket = socket;
		this.setupListeners();
	}
}

const searchGameHandler = new SearchGameHandler();
socketHandler.initialize([searchGameHandler]);
