const socketHandler = new SocketHandler();
const playerRegistryHandler = new PlayerRegistryHandler();
const gameSearchHandler = new GameSearchHandler();
const gameLogic = new GameLogic();

socketHandler.initialize();
socketHandler.connect(() => {
	playerRegistryHandler.initialize(socketHandler, player => {
		gameSearchHandler.initialize(socketHandler, player, game => {
			gameLogic.initialize(socketHandler, player);
			gameLogic.startGame(game);
		});
		gameSearchHandler.startSearch();
	});
});
