const Helper = {
	KEYS: {
		player: {
			id: 'bolgame.player.id',
			username: 'bolgame.player.username',
		}
	},

	STORAGE: {
		getPlayerData: () => {
			return {
				id: localStorage.getItem(Helper.KEYS.player.id) || '',
				username: localStorage.getItem(Helper.KEYS.player.username) || ''
			}
		},
		savePlayerData: (player) => {
			localStorage.setItem(Helper.KEYS.player.id, player.id);
			localStorage.setItem(Helper.KEYS.player.username, player.username);
		}
	},

	GAME_STATE: {
		opponentsTurn: 'game.state.opponentsTurn',
		playersTurn: 'game.state.playersTurn',
		finished: 'game.state.finished'
	},

	FUNCTIONS: {
		random: (charCount = 20) => {
			let randomString = '';
			while (randomString.length < charCount) {
				randomString += Math.random().toString(36).replace(/[^\w-]+/g, '');
			}
			return randomString.substr(0, charCount);
		}
	}
};