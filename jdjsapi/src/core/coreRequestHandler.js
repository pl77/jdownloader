define("coreRequestHandler", ["coreCrypto", "coreCryptoUtils"], function (CoreCrypto, CryptoUtils) {
    var LOCAL_STORAGE_RECONNECT_LOCK_KEY = "jdapi/src/core/coreRequestHandler.js";
    var JDAPICoreRequestHandler = function (appKey, transferEncoding, LOCAL_STORAGE_KEY, apiRoot) {
        this.appKey = appKey;
        this.transferEncoding = transferEncoding;
        this.LOCAL_STORAGE_KEY = LOCAL_STORAGE_KEY;
        this.apiRoot = apiRoot;
    };
    $.extend(JDAPICoreRequestHandler.prototype, {
        connect: function (options) {
            var handshake = $.Deferred();
            //PERFORM LOGIN
            if (options && options.email && options.pass) {
                // Generate login secret
                CryptoUtils.processPassword(options);

                // craft query string
                var params = {};
                var action;

                params.email = options.email;
                action = "connect";
                params.appkey = this.appKey;
                params.rid = 0;

                var queryString = "/my/" + action + "?" + $.param(params);
                queryString += "&signature=" + CoreCrypto.HmacSHA256(CoreCrypto.enc.Utf8.parse(queryString), options.loginSecret).toString(this.transferEncoding);

                // issue authentication request
                var auth = $.ajax({
                    url: this.apiRoot + queryString,
                    type: "POST",
                    async: true,
                    dataType: "aesjson-server",
                    converters: {
                        "* aesjson-server": CryptoUtils.decryptJSON.bind(this, options.loginSecret.firstHalf(),options.loginSecret.secondHalf())
                    }
                });
                // if authentication fails, reject the handshake
                auth.fail(handshake.reject);
                // if the handshake gets rejected beforehand (e.g. because of a disconnect() call), abort the authentication request
                handshake.fail(auth.abort);
                // if authentication is successful, initialize connection
                auth.done((function (data) {
                    if (data.rid !== params.rid) return handshake.reject(undefined, "replay attack");
                    CryptoUtils.initialiseConnection(options, data.sessiontoken, data.regaintoken);
                    localStorage.setItem(this.LOCAL_STORAGE_KEY, JSON.stringify(options));
                    handshake.resolve(options);
                }).bind(this));

                // ATTEMPT RESUME FROM LOCALSTORAGE
            } else {
                var restoredOptions;
                try {
                    restoredOptions = JSON.parse(localStorage.getItem(this.LOCAL_STORAGE_KEY));
                } catch (e) {

                }
                if (!restoredOptions) {
                    handshake.reject();
                } else {
                    options = $.extend({}, restoredOptions);
                    //Convert JSON back to CoreCrypto.lib.WordArray instance
                    if (restoredOptions.serverEncryptionToken) {
                        options.serverEncryptionToken = CoreCrypto.lib.WordArray.random(restoredOptions.serverEncryptionToken.sigBytes);
                        options.serverEncryptionToken.words = restoredOptions.serverEncryptionToken.words;
                    }
                    if (restoredOptions.deviceEncryptionToken) {
                        options.deviceEncryptionToken = CoreCrypto.lib.WordArray.random(restoredOptions.deviceEncryptionToken.sigBytes);
                        options.deviceEncryptionToken.words = restoredOptions.deviceEncryptionToken.words;
                    }
                    if (restoredOptions.deviceSecret) {
                        options.deviceSecret = CoreCrypto.lib.WordArray.random(restoredOptions.deviceSecret.sigBytes);
                        options.deviceSecret.words = restoredOptions.deviceSecret.words;
                    }
                    handshake.resolve(options);
                }
            }
            return handshake;
        },
        reconnect: function (options, rid) {
            var handshake = $.Deferred();
            /*
             * ReconnectLock localStorage functionality to handle multiple browser tabs
             */
            // If untimeouted lock exists reconnect yourself, else wait
            // for other tab to finish reconnect
            var reconnectLock;
            if (localStorage.getItem(LOCAL_STORAGE_RECONNECT_LOCK_KEY)) {
                try {
                    reconnectLock = JSON.parse(localStorage.getItem(LOCAL_STORAGE_RECONNECT_LOCK_KEY));
                } catch (e) {
                }
            }
            //lock exists and is not timeouted
            if (reconnectLock && (new Date().getTime() - reconnectLock.time) < 5000) {
                // wait for reconnect
                // listen to local storage
                window.addEventListener('storage', function (event) {
                    if (event.key === this.LOCAL_STORAGE_KEY) {
                        if (!e.newValue) {
                            handshake.reject();
                        } else {
                            handshake.resolve();
                        }
                    }
                }, false);
            } else {
                //create lock
                localStorage.setItem(LOCAL_STORAGE_RECONNECT_LOCK_KEY, JSON.stringify({
                    time: new Date().getTime()
                }));
                // do the reconnect in this tab
                // craft query string
                var params = {};
                var action;

                action = "reconnect";
                params.sessiontoken = options.sessiontoken;
                params.regainToken = options.regaintoken;
                params.rid = rid;

                var queryString = "/my/" + action + "?" + $.param(params);
                queryString += "&signature=" + CoreCrypto.HmacSHA256(CoreCrypto.enc.Utf8.parse(queryString), options.serverEncryptionToken).toString(this.transferEncoding);

                var self = this;
                //reconnect yourself
                var reauth = $.ajax({
                    url: options.API_ROOT + queryString,
                    type: "POST",
                    async: true,
                    dataType: "aesjson-server",
                    converters: {
                        "* aesjson-server": CryptoUtils.decryptJSON.bind(this, options.serverEncryptionToken.firstHalf(),options.serverEncryptionToken.secondHalf())
                    }
                });
                // if authentication fails, reject the handshake
                reauth.fail(handshake.reject.bind(handshake));
                // if the handshake gets rejected beforehand (e.g. because of a disconnect() call), abort the authentication request
                handshake.fail(reauth.abort.bind(reauth));
                // if authentication is successful, initialize connection
                reauth.done((function (data) {
                    CryptoUtils.initialiseConnection(options, data.sessiontoken, data.regaintoken);
                    localStorage.removeItem(LOCAL_STORAGE_RECONNECT_LOCK_KEY);
                    localStorage.setItem(this.LOCAL_STORAGE_KEY, JSON.stringify(options));
                    handshake.resolve();
                }).bind(this));
            }
            return handshake;
        },
        disconnect: function (options) {
            localStorage.removeItem(this.LOCAL_STORAGE_KEY);

            var params =
            {
                rid: options.rid,
                sessiontoken: options.sessiontoken
            };
            var queryString = "/my/disconnect?" + $.param(params);
            queryString += "&signature=" + CoreCrypto.HmacSHA256(CoreCrypto.enc.Utf8.parse(queryString), options.serverEncryptionToken).toString(this.transferEncoding);

            var disconnect = $.ajax({
                url: this.apiRoot + queryString,
                async: true,
                type: "POST",
                dataType: "aesjson-server",
                converters: {
                    "* aesjson-server": CryptoUtils.decryptJSON.bind(this, options.serverEncryptionToken.firstHalf(),options.serverEncryptionToken.secondHalf())
                }
            }).done((function (data) {
                if (data.rid !== options.rid) return this.connection.reject(undefined, "replay attack");
            }).bind(this));

            return disconnect;
        },
        requestTerminationEmail: function (options) {
            var params =
            {
                rid: options.rid,
                sessiontoken: options.sessiontoken,
                captchaChallenge: options.captchaChallenge,
                captchaResponse: options.captchaResponse
            };
            var queryString = "/my/disconnect?" + $.param(params);
            queryString += "&signature=" + CoreCrypto.HmacSHA256(CoreCrypto.enc.Utf8.parse(queryString), options.serverEncryptionToken).toString(this.transferEncoding);

            var disconnect = $.ajax({
                url: this.apiRoot + queryString,
                async: true,
                type: "POST",
                dataType: "aesjson-server",
                converters: {
                    "* aesjson-server": CryptoUtils.decryptJSON.bind(this, options.serverEncryptionToken.firstHalf(),options.serverEncryptionToken.secondHalf())
                }
            }).done((function (data) {
                if (data.rid !== options.rid) return this.connection.reject(undefined, "replay attack");
            }).bind(this));

            return disconnect;
        }
    });

    return JDAPICoreRequestHandler;
});