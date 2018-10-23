class SocketHandler {
	constructor() {
		console.log(`>> SocketHandler::constructor() <<`);
		this.socketId = '/bolgame-socket';
	}

	name() {
		return 'SocketHandler';
	}

	connect(callback, playerId) {
		console.log(`>> SocketHandler::connect(${callback}, ${playerId}) <<`);
		this.stomp.connect({}, frame => callback(frame));
	}

	disconnect(callback) {
		console.log(`>> SocketHandler::disconnect(${callback}) <<`);

		if (!this.stomp) return;
		this.stomp.disconnect(callback);
	}

	subscribe(path, callback) {
		console.log(`>> SocketHandler::subscribe(${path}, ${callback}) <<`);
		const headers = {
			id: path
		};

		if (!this.stomp) return;
		this.stomp.subscribe(`/${path}`, response => {
			console.log(`RESPONSE: ${response}`);
			if (callback) callback(JSON.parse(response.body));
		}, headers);
	}

	unsubscribe(hash) {
		this.stomp.unsubscribe(hash);
	}

	send(url, message, headers = {}) {
		console.log(`>> SocketHandler::send(${url}, ${message}, ${headers}) <<`);

		if (!this.stomp) return;
		this.stomp.send(`/${url}`, headers, JSON.stringify(message.getPayload()));
	}

	initialize() {
		console.log(`>> SocketHandler::initialize() <<`);

		const socket = new SockJS(this.socketId);
		this.stomp = Stomp.over(socket);
		this.stomp.debug = null;
	}
}