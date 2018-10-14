const Constants = {
	COOKIES: {
		playerId: 'bolgame.player.id'
	},

	HELPERS: {
		getIdFromCookie: (cookieName) => {
			const value = `; ${document.cookie}`;
			const parts = value.split(`; ${cookieName}=`);
			if (parts.length == 2) return parts.pop().split(';').shift();
		},
		setIdToCookie: (data) => {
			const {
				name, value, expiresInDays
			} = data;
			const date = new Date();
			date.setTime(date.getTime() + (expiresInDays * 24 * 60 * 60 * 1000));
			const expiration = date.toUTCString();
			document.cookie = `${name}=${value}; expires=${expiration}; path=/`;
		}
	}
};