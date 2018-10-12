class SocketHandler {
	constructor() {
		this.socketId = '/bolgame-socket';
	}

	name() {
		return 'SocketHandler';
	}

	connect(socketId, callback) {
		const socket = new SockJS(socketId);
		const stomp = Stomp.over(socket);
		stomp.connect({},
			frame => !!callback && callback(frame)
		);
		return stomp;
	}

	disconnect(callback) {
		if (!this.stomp) return;
		this.stomp.disconnect(callback);
	}

	subscribe(path, callback) {
		if (!this.stomp) return;
		this.stomp.subscribe(path, (response) => {
			console.log(`RESPONSE: ${response}`);
			if (callback) callback(response.body);
		});
	}

	send(endpoint, payload) {
		if (!this.stomp) return;
		const {
			send,
			headers = {}
		} = endpoint;
		this.stomp.send(`/socket${send}`, headers, JSON.stringify(payload));
	}

	initialize(subscribers = []) {
		this.stomp = this.connect(this.socketId, frame => {
			console.log(`Connected: ${frame}`);
			subscribers.forEach(subscriber => {
				subscriber.initialize(this);
				subscriber.setupSubscriptions();
			});
		});
	}
}

const socketHandler = new SocketHandler();