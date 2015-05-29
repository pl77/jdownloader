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
		encryptJSON: function(secret, plain) {
            if (!secret) {
                return plain;
            }
			var iv = secret.firstHalf();
			var key = secret.secondHalf();

			var encrypted = CoreCrypto.AES.encrypt(JSON.stringify(plain), key, {
				mode: CoreCrypto.mode.CBC,
				iv: iv
			});

			return encrypted.toString();
		},
		/*
		 * @param secret: CoreCrypto.lib.WordArray used as secret
		 * @param secretOld: The secret from before a reconnect, can be null
		 * @param plain: the ciphtext to decrypt
		 */
		decryptJSON: function(secret, secretOld, encrypted) {
			var plain = null;
			var exception = null;
			try {
				var iv = secret.firstHalf();
				var key = secret.secondHalf();

				var plain_raw = CoreCrypto.AES.decrypt(encrypted, key, {
					mode: CoreCrypto.mode.CBC,
					iv: iv
				}).toString(CoreCrypto.enc.Utf8);

				plain = JSON.parse(plain_raw);
			} catch (e) {
				console.warn(e);
				exception = e;
				plain = null;
			}
			//if decryption did not work, try with old tokens before failing finally
			if (plain) {
				return plain;
			} else {
				//Try with old token if available
				if (secretOld) {
					return this.decryptJSON(secretOld, null, encrypted);
				} else {
					//return raw
					return encrypted;
				}
			}
		}
	};

	return CryptoUtils;
});