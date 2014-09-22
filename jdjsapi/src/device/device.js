define("device",[], function() {
	/**
	 * API to handle device (device == JDownloader) calls
	 */
	var JDAPIDevice = function(jdAPICore, data) {
		this.jdAPICore = jdAPICore;
		this.state = "OFFLINE";
		this.deviceId = data.id;
		this.deviceName = data.name;
	};
	$.extend(JDAPIDevice.prototype, {
		call: function(action, params, localModeCallback) {
			if (this.localURL) {
				if(localModeCallback)	localModeCallback(true);
				return this.jdAPICore.localDeviceCall(this.localURL, this.deviceId, action, params);
			} else {
				if(localModeCallback)	localModeCallback(false);
				return this.jdAPICore.deviceCall(this.deviceId, action, params);
			}
		},
		setLocalURL: function(localURL) {
			this.localURL = localURL;
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