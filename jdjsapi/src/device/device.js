define("device",[], function() {
	/**
	 * API to handle device (device == JDownloader) calls
	 *
	 * data.rsaPublicKey
	 */
	var JDAPIDevice = function(jdAPICore, data) {
		this.jdAPICore = jdAPICore;
		this.state = "OFFLINE";
		this.deviceId = data.id;
		this.rsaPublicKey = data.rsaPublicKey;
		this.deviceName = data.name;
	};
	$.extend(JDAPIDevice.prototype, {
		call: function(action, params, localModeCallback) {
			if (this.localURL) {
				if(localModeCallback)	localModeCallback(true);
				return this.jdAPICore.localDeviceCall(this.localURL, this.deviceId, action, this.rsaPublicKey, params);
			} else {
				if(localModeCallback)	localModeCallback(false);
				return this.jdAPICore.deviceCall(this.deviceId, action, this.rsaPublicKey, params);
			}
		},
		setLocalURL: function(localURL) {
			this.localURL = localURL;
		},
		setPublicKey: function (publicKey) {
			this.publicKey = publicKey;
		},
		getURL: function() {
			if (this.localURL) {
				return this.localURL;
			} else {
				return this.jdAPICore.API_ROOT;
			}
		},
		isInLocalMode: function() {
			if (this.localURL) return true;
			return false;
		}
	});
	return JDAPIDevice;
});