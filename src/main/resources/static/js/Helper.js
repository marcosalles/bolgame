const Helper = {
	KEYS: {
		player: {
			id: 'bolgame.player.id',
			username: 'bolgame.player.username',
		},
		actions: {
			play: 'play',
			scores: 'scores',
			quit: 'quit'
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
		},
		actions: {
			setScoresPlayerId: id => {
				$(`#action-${Helper.KEYS.actions.scores}`).attr('href', `/player/${id}/scores`);
			},
			showAction: (name, action) => {
				const button = $(`#action-${name}`);
				if (!button.length) return;
				button.removeClass('hidden');
				button.off('click');
				button.on('click', () => action());
				if ($('#actions .btn:not(.hidden)').length) {
					$('#actions').removeClass('hidden');
				}
			},
			hideAction: name => {
				$(`#action-${name}`).addClass('hidden')
				if (!$('#actions .btn:not(.hidden)').length) {
					$('#actions').addClass('hidden');
				}
			}
		}
	}
};