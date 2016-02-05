define("coreCore", ["coreCrypto", "coreCryptoUtils", "coreRequest", "coreRequestHandler"], function (CoreCrypto, CryptoUtils, JDAPIRequest, JDAPICoreRequestHandler) {
    "use strict";

    /**
     * CONSTANTS
     */
    var TRANSFER_ENCODING = CoreCrypto.enc.Hex;
    var API_ROOT = "//api.jdownloader.org";
    var LOCAL_STORAGE_KEY = "jdapi/src/core/core.js";

    // API States
    var CONNECTED_STATE = 0;
    var PENDING_STATE = 1;
    var RECONNECT_STATE = 2;
    var DISCONNECTED_STATE = 3;

    /**
     * GLOBAL STATE VARIABLES
     * TODO: Not compatible with multiple JDAPICore instances as it is global singleton
     */
    var APIState = (function () {
        // Private variables
        var apiState = PENDING_STATE;
        var onChangeCallbacks = new Array();
        // Public interface
        return {
            setAPIState: function (STATE) {
                apiState = STATE;
                onChangeCallbacks.forEach(function (callback) {
                    try {
                        callback(apiState);
                    } catch (e) {
                        // console.error(e);
                    }
                });
            },
            getAPIState: function () {
                return apiState;
            },
            addAPIStateChangeListener: function (callback) {
                if (callback && typeof callback === "function") {
                    onChangeCallbacks.push(callback);
                } else {
                    throw new TypeError("APIStateChangeListener must be function");
                }
            }
        };
    })();
    //Default App Key
    var APP_KEY = "MYJD_JS_DEFAULT_APP_KEY";

    // Global Request Queue
    var jdapiRequestQueue = new Array();
    var coreRequestHandler = new JDAPICoreRequestHandler(APP_KEY, TRANSFER_ENCODING, LOCAL_STORAGE_KEY, API_ROOT);

    // RID
    var lastrid = 0;
    var getRID = function () {
        var rid = (new Date()).getTime();
        if (lastrid === rid) {
            rid++;
        }
        lastrid = rid;
        return rid;
    };

    /**
     * Constructor for Main JDAPICore Object, that gets exported
     */
    var JDAPICore = function (options, onConnected, APP_KEY_PARAM) {
        if (APP_KEY_PARAM) APP_KEY = APP_KEY_PARAM;
        // If options object available, initialize with connect(options)
        APIState.setAPIState(PENDING_STATE);
        options = options || {};
        this.options = options;
        this.TRANSFER_ENCODING = TRANSFER_ENCODING;
        if (onConnected && $.isFunction(onConnected.promise)) {
            this.connect(this.options).then(function () {
                    //defer to next event loop event
                    setTimeout(function () {
                        onConnected.resolve();
                    }, 0);
                },
                function () {
                    //defer to next event loop event
                    setTimeout(function () {
                        onConnected.reject();
                    }, 0);
                });
        } else if (onConnected && $.isFunction(onConnected)) {
            this.connect(this.options).done(function () {
                //defer to next event loop event
                setTimeout(function () {
                    onConnected();
                }, 0);
            });
        } else {
            this.connect(this.options);
        }
        //initialize local storage listener to refresh options if reconnect happened in other browser tab
        window.addEventListener('storage', function (e) {
            if (e.key === LOCAL_STORAGE_KEY) {
                if (!e.newValue) {
                    this.disconnect();
                } else {
                    //NEW CONNECT WITH NO ARGUMENTS SHOULD TRIGGER REINITIALIZE WITH NEW LOCAL STORAGE STUFF
                    this.connect();
                }
            }
        }.bind(this), false);
    };
    $.extend(JDAPICore.prototype, {
        /**
         * Initialize Session either with given @email and @pass or with stored session
         */
        requestCount: 0,
        connect: function (options) {
            APIState.setAPIState(PENDING_STATE);
            var filter = $.Deferred();
            var connectCall = coreRequestHandler.connect(options);
            connectCall.done(function (options) {
                options.API_ROOT = API_ROOT;
                this.options = options;
                APIState.setAPIState(CONNECTED_STATE);
                this._handleEnqueuedRequestsAndSetConnected();
                filter.resolve(options);
            }.bind(this));
            connectCall.fail(function (err) {
                APIState.setAPIState(DISCONNECTED_STATE);
                filter.reject(err);
            }.bind(this));
            return filter;
        },
        reconnect: function () {
            var reconnectDef = $.Deferred();
            // IF RECONNECT NOT ALREADY IN PROGRESS
            if (APIState.getAPIState() == CONNECTED_STATE || APIState.getAPIState() == PENDING_STATE) {
                // START RECONNECTING
                APIState.setAPIState(RECONNECT_STATE);
                var reconnect = coreRequestHandler.reconnect(this.options, getRID());
                reconnect.done(function () {
                    this._handleEnqueuedRequestsAndSetConnected();
                }.bind(this));
                reconnect.fail(function () {
                    // FAIL FINALLY, TRIGGER LOGOUT ACTIONS ETC
                    this.disconnect();
                }.bind(this));
                reconnect.then(reconnectDef.resolve, reconnectDef.reject);
            } else {
                //RECONNECT ALREADY HAPPENING -> RESOLVE
                //TODO: Not dependent on reconnect outcome
                reconnectDef.resolve();
            }
            return reconnectDef;
        },
        disconnect: function () {
            //If already disconnected
            if (APIState.getAPIState() === DISCONNECTED_STATE) {
                var ret = $.Deferred();
                ret.resolve();
                return ret;
            }
            //Else make disconnect call
            var disconnectCall = coreRequestHandler.disconnect({
                serverEncryptionToken: this.options.serverEncryptionToken,
                sessiontoken: this.options.sessiontoken,
                rid: getRID()
            });
            disconnectCall.always(function () {
                APIState.setAPIState(DISCONNECTED_STATE);
            });
            return disconnectCall;
        },
        /* send request to list all available devices */
        serverCall: function (action, params, type) {
            // Create request object
            params = params || {};
            var reqOptions = {

                jdAction: action,
                jdParams: params,
                // url : this.options.apiRoot + queryString,
                type: type || "POST",
                dataType: "aesjson-server",
                serverEncryptionToken: this.options.serverEncryptionToken,
                TRANSFER_ENCODING: TRANSFER_ENCODING,
                API_ROOT: API_ROOT,
                sessiontoken: this.options.sessiontoken,
                rid: getRID()
            };
            // Send and return the deferred
            return this._call(reqOptions);
        },
        /* wraps jd api call in reconnect handling jQuery ajax call */
        deviceCall: function (deviceId, action, postData, rsaPublicKey, timeout) {
            postData = postData || [];
            var rID = getRID();
            var reqOptions = {
                jdAction: action,
                jdData: {
                    url: action,
                    params: postData,
                    apiVer: 1,
                    rid:rID
                },
                deviceId: deviceId,
                type: "POST",
                dataType: "aesjson-server",
                deviceEncryptionToken: this.options.deviceEncryptionToken,
                rsaPublicKey: rsaPublicKey,
                TRANSFER_ENCODING: TRANSFER_ENCODING,
                API_ROOT: API_ROOT,
                sessiontoken: this.options.sessiontoken,
                rid:rID
            };
            if (timeout) {
                reqOptions.timeout = timeout;
            }
            return this._call(reqOptions);
        },
        /* wraps jd local call in reconnect handling jQuery ajax call */
        localDeviceCall: function (localURL, deviceId, action, postData, rsaPublicKey, timeout) {
            postData = postData || [];
            var rID = getRID();
            var reqOptions = {
                jdAction: action,
                jdData: {
                    url: action,
                    params: postData,
                    apiVer: 1,
                    rid:rID
                },
                deviceId: deviceId,
                type: "POST",
                dataType: "aesjson-server",
                deviceEncryptionToken: this.options.deviceEncryptionToken,
                rsaPublicKey: rsaPublicKey,
                TRANSFER_ENCODING: TRANSFER_ENCODING,
                API_ROOT: localURL,
                sessiontoken: this.options.sessiontoken,
                rid: rID
            };
            if (timeout) {
                reqOptions.timeout = timeout;
            }
            return this._call(reqOptions);
        },
        getSessionToken: function () {
            return this.options.sessiontoken;
        },
        getSessionInfo: function () {
            return this.options;
        },
        addAPIStateChangeListener: function (callback) {
            APIState.addAPIStateChangeListener(callback);
        },
        getAPIState: function () {
            return APIState.getAPIState();
        },
        getAPIStatePlain: function () {
            switch (APIState.getAPIState()) {
                case 0:
                    return "CONNECTED_STATE";
                    break;
                case 1:
                    return "PENDING_STATE";
                    break;
                case 2:
                    return "RECONNECT_STATE";
                    break;
                case 3:
                    return "DISCONNECTED_STATE";
                    break;
                default:
                    return "INVALID";
            }
            ;
        },
        getCurrentUser: function () {
            return {
                loggedIn: (this.getAPIState() !== 3),
                name: this.options.email
            };
        },
        API_ROOT: API_ROOT,
        APP_KEY: APP_KEY,
        _rebuildRequestOptions: function (reqOptions) {
            if (reqOptions.deviceEncryptionToken) {
                reqOptions.deviceEncryptionToken = this.options.deviceEncryptionToken;
                reqOptions.deviceEncryptionTokenOld = this.options.deviceEncryptionTokenOld;
            }

            if (reqOptions.serverEncryptionToken) {
                reqOptions.serverEncryptionToken = this.options.serverEncryptionToken;
            }

            reqOptions.sessiontoken = this.options.sessiontoken;

            reqOptions.rid = getRID();
            return reqOptions;
        },
        _handleEnqueuedRequestsAndSetConnected: function () {
            APIState.setAPIState(CONNECTED_STATE);
            while (jdapiRequestQueue.length > 0) {
                var queuedRequest = jdapiRequestQueue.shift();
                var requestOptions = this._rebuildRequestOptions(queuedRequest.options);
                // fire async, TODO: Could fail ?!?!
                setTimeout(function () {
                    var call = this._call(requestOptions, 1);
                    call.done(queuedRequest.deferred.resolve);
                    call.fail(queuedRequest.deferred.reject);
                }.bind(this), 10);
            }
        },
        // function that either makes or enqueues the request dependent on API state
        _call: function (reqOptions, count) {
            count = count || 0;
            var def = $.Deferred();
            // IF CONNECTED, MAKE CALL!
            if (APIState.getAPIState() === CONNECTED_STATE) {
                var initialRequest = new JDAPIRequest(reqOptions).send();
                // Initial call successful, just resolve the deferred
                initialRequest.done(def.resolve);
                initialRequest.fail(function (error) {
                    if (error.type && "TOKEN_INVALID" === error.type && count === 0) {
                        jdapiRequestQueue.push({
                            deferred: def,
                            options: reqOptions
                        });
                        this.reconnect();
                    } else if (error.type && "AUTH_FAILED" === error.type) {
                        def.reject(error);
                        this.disconnect();
                    } else {
                        // TODO: Probably causes disc if listen fails
                        def.reject(error);
                        // this.disconnect();
                    }
                }.bind(this));
                // IF NOT CONNECTED, ENQUEUE FOR WHEN CONNECTED
            } else {
                jdapiRequestQueue.push({
                    deferred: def,
                    options: reqOptions
                });
            }
            return def;
        }
    });
    return JDAPICore;
});