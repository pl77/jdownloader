define("coreCryptoUtils",["coreCrypto"], function(CoreCrypto) {
	/**
	 * Utility Object for cryptography functions
	 */
	var CryptoUtils = {
		/* hash a password with the given pass and domain as salt */
		hashPassword: function(email, pass, domain) {
			return CoreCrypto.SHA256(CoreCrypto.enc.Utf8.parse(email.toLowerCase() + pass + domain.toLowerCase()));
		},
		/* convert pass to secret hashes and delete it afterwards */
		processPassword: function(options) {
			if (!options.email || !options.pass) {
				throw "processPassword requires set options.email and options.pass";
			}

			options.loginSecret = this.hashPassword(options.email, options.pass, "server");
			options.deviceSecret = this.hashPassword(options.email, options.pass, "device");
			delete options.pass;
		},
		/* initialise the tokens after a successful handshake or update tokens after a reconnect */
		initialiseConnection: function(options, sessiontoken, regaintoken) {
			if (!options.loginSecret && !options.serverEncryptionToken) {
				throw "either loginSecret or serverEncryptionToken must be set, probably should call processPassword(options) first";
			} else if(!options.deviceSecret){
				throw "deviceSecret not set";
			} else if(!sessiontoken || !regaintoken){
				throw "sessiontoken and regaintoken are required to initialise connection";
			}
			options.sessiontoken = sessiontoken;
			options.regaintoken = regaintoken;

			var ses = CoreCrypto.enc.Hex.parse(options.sessiontoken);
			if (options.loginSecret) {
				// calculate initial secret
				var tot = options.loginSecret.concat(ses);
				// keep old token to decrypt requests that got sent before a reconnect
				options.serverEncryptionTokenOld = options.serverEncryptionToken;
				options.serverEncryptionToken = CoreCrypto.SHA256(tot);
				delete options.loginSecret;
			} else {
				// calculate new secret
				options.serverEncryptionTokenOld = options.serverEncryptionToken;
				var tot = options.serverEncryptionToken.concat(ses);
				options.serverEncryptionToken = CoreCrypto.SHA256(tot);
			}

			var deviceSecret = options.deviceSecret.clone();
			var totDev = deviceSecret.concat(ses);
			// keep old device encryption token for decrypting old
			// requests after reconnect
			options.deviceEncryptionTokenOld = options.deviceEncryptionToken;
			// set new device encryption token
			options.deviceEncryptionToken = CoreCrypto.SHA256(totDev);
		},
		/*
		 * @param secret: CoreCrypto.lib.WordArray used as secret
		 * @param plain: the JSON object to encrypt
		 */
		encryptJSON: function (secret, plain, rsaPublicKey) {
            if (!secret) {
				var result = {"data": plain};
				return result;
            }

			var iv;
			var key;
			if (rsaPublicKey) {
				var keySize = 32; // AES256
				var cryptoObj = window.crypto || window.msCrypto;
				if (Uint8Array && cryptoObj && cryptoObj.getRandomValues) {
					try {
						iv = new Uint8Array(16);
						key = new Uint8Array(keySize);
						cryptoObj.getRandomValues(iv);
						cryptoObj.getRandomValues(key);

						// No WordArray constructor that takes Uint8Array, thus Hex as intermediate (Uint8Array -> Hex String -> WordArray)
						var ivHex = this.ua2hex(iv);
						var keyHex = this.ua2hex(key);
						iv = CryptoJS.enc.Hex.parse(ivHex);
						key = CryptoJS.enc.Hex.parse(keyHex);
					} catch (exception) {
						// Browser failed to do his job
					}
				}
				if (!(iv && key)) {
					// we still need keys
					iv = CoreCrypto.lib.WordArray.random(16);
					key = CoreCrypto.lib.WordArray.random(keySize);
				}
			} else {
				iv = secret.firstHalf();
				key = secret.secondHalf();
			}

			var aesEncrypted = CoreCrypto.AES.encrypt(JSON.stringify(plain), key, {
				mode: CoreCrypto.mode.CBC,
				iv: iv
			});

			var result = {};

			if (rsaPublicKey) {
				var encrypt = new JSEncrypt();
				encrypt.setPublicKey(rsaPublicKey);
				var stringHexIv = CryptoJS.enc.Hex.stringify(iv);
				var stringHexKey = CryptoJS.enc.Hex.stringify(key);
				var rsaEncrypted = encrypt.encrypt(stringHexIv + stringHexKey);
				result["rsa"] = rsaEncrypted;
				result["contentType"] = "application/rsajson; charset=utf-8";
				result["iv"] = iv;
				result["key"] = key;
				// Delimiter between rsa key and aes encrypted content is |
				result["data"] = rsaEncrypted + "|" + aesEncrypted.toString();
			} else {
				result["contentType"] = "application/aesjson; charset=utf-8";
				result["iv"] = secret.firstHalf();
				result["key"] = secret.secondHalf();
				result["data"] = aesEncrypted.toString();
			}
			return result;
		},
		ua2hex: function (ua) {
			// Converts Uint8Array to Hex String
			var h = '';
			for (var i = 0; i < ua.length; i++) {
				var temp = ua[i].toString(16);
				if (temp.length < 2) {
					temp = "0" + temp;
				}
				h += temp;
			}
			return h;
		},
		decryptJSON: function (iv, key, encrypted) {
			try {
				var plain = null;
				var plain_raw = CoreCrypto.AES.decrypt(encrypted, key, {
					mode: CoreCrypto.mode.CBC,
					iv: iv
				}).toString(CoreCrypto.enc.Utf8);
				if (plain_raw && typeof plain_raw === "string") {
					plain = JSON.parse(plain_raw);
				}
				return plain;
			} catch (e) {
				return encrypted;
			}
		},

		decryptRsaJSON: function (rsaIv, rsaKey, encrypted) {
			var jsEncrypt = new JSEncrypt();
			jsEncrypt.setPrivateKey(rsaIv + rsaKey);
			var rsaDecrypted = jsEncrypt.decrypt(encrypted);
			try {
				return this.decryptJSON(rsaDecrypted.token.firstHalf(), rsaDecrypted.token.secondHalf(), rsaDecrypted.data);
			} catch (e) {
				return encrypted;
			}
		}

	};


	return CryptoUtils;
});