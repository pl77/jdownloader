/**
 * Wrap console to disable .log output in production
 */
var LOGGING_ENABLED = false;
var logger = {
    log: function (msg) {
        if (LOGGING_ENABLED) console.log(msg);
    },
    warn: function (msg) {
        if (LOGGING_ENABLED) console.warn(msg);
    },
    error: function (msg) {
        if (LOGGING_ENABLED)  console.error(msg);
    }
};
/**
 * Global config variables
 */
define("config/config", function () {
    return {
        TRANSFER_ENCODING: CryptoJS.enc.Hex,
        API_ROOT: "//api.jdownloader.org",
        LOCAL_STORAGE_KEY: "api.transport.options",
        APP_KEY: "MYJD_JS_DEFAULT_APP_KEY"
    };
});

/**
 * Main Library Object, contains all functions the library makes available for clients
 */
define("jdapi", ["coreCore", "device", "serverServer", "serviceService", "deviceController"], function (JDAPICore, JDAPIDevice, JDAPIServer, JDAPIService, JDAPIDeviceController) {
    /**
     *  Constructor method of the API
     *
     *    @param options Example: {email: 'myjd@jdownloader.org', pass: 'mysecretpass'}
     *  @param onConnectedCallback: a function or $.Deferred that gets called/resolved if connect was successful
     *  @param The AppKey to identify the application using the API
     */
    var API = function (options, onConnectedCallback, APP_KEY) {
        // Setup API Components
        this.jdAPICore = new JDAPICore(options, onConnectedCallback, APP_KEY);
        this.apiServer = new JDAPIServer(this.jdAPICore);
        this.apiService = new JDAPIService(this.jdAPICore);
        this.apiDeviceController = new JDAPIDeviceController(this.apiServer, this.jdAPICore);
    };

    $.extend(API.prototype, {
        /**
         * Get captcha challenge from the API used for 'Register Account' and 'Password Recovery'
         *  @returns  deferred object that resolves to captcha object which contains the captcha challenge id
         *              and the captcha challenge image as dataUrl
         */
        getCaptcha: function () {
            return this.apiServer.getCaptcha();
        },
        /**
         *   Connect to the API server and authenticate.
         *   Subscribe to handshake.done to see when the connection
         *   has been established.
         *   @param options: Example: {email: 'myjd@jdownloader.org', pass: 'mysecretpass'}
         *   @returns handshake deferred that gets resolved /rejected
         *            when the authentication has  succeeded/failed
         */
        connect: function (options) {
            return this.jdAPICore.connect(options);
        },
        /**
         *   Register new user at API server, requires a Captcha Challenge to be solved.
         *   Email will be verified via authentication link
         *   @param data: Example: {email: 'myjd@jdownloader.org', catpachaChallenge: '', captchaResponse: ''}
         *   @returns handshake deferred that gets resolved /rejected
         *            when the registration has succeeded/failed
         */
        registerUser: function (data) {
            return this.apiServer.registerUser(data);
        },
        reconnect: function () {
            return this.jdAPICore.reconnect();
        },
        /**
         *   Disconnect from the API server.
         *   @returns a deferred that gets resolved as soon you are disconnected.
         */
        disconnect: function () {
            return this.jdAPICore.disconnect();
        },
        confirmEmail: function (email, validationkey, pass) {
            return this.apiServer.confirmEmail(email, validationkey, pass);
        },
        requestConfirmEmail: function (pass) {
            return this.apiServer.requestConfirmEmail(pass);
        },
        requestPasswordChangeEmail: function (email, captchaChallenge, captchaResponse) {
            return this.apiServer.requestPasswordChangeEmail(email, captchaChallenge, captchaResponse);
        },
        requestTerminationEmail: function (captchaChallenge, captchaResponse) {
            return this.apiServer.requestTerminationEmail(captchaChallenge, captchaResponse);
        },
        finishTermination: function (email, pw, key, captchaChallenge, captchaResponse) {
            return this.apiServer.finishTermination(email, pw, key, captchaChallenge, captchaResponse);
        },
        /**
         * @param newpass The new password
         * @param key The password change validation key
         * @returns
         */
        changePassword: function (email, newpass, key) {
            return this.apiServer.changePassword(email, newpass, key);
        },
        sendServiceRequest: function (serviceName, url, action, params) {
            return this.apiService.send(serviceName, url, action, params);
        },
        listDevices: function () {
            return this.apiDeviceController.refreshAllDeviceAPIs();
        },
        /**
         * Set the device id to what device subsequent "send" calls should go
         */
        setActiveDevice: function (deviceId) {
            return this.activeDeviceId = deviceId;
        },
        getActiveDevice: function () {
            return this.activeDeviceId;
        },
        getDirectConnectionInfos: function () {
            return this.transport.getDirectConnectionInfos();
        },
        ping: function () {
            return this.transport.ping();
        },
        /**
         *   Post feedback to the server
         *   @returns a deferred that gets resolved as feedback post returns successfully
         */
        feedback: function (data) {
            return this.apiServer.feedback(data);
        },
        /**
         *   Start polling events from the server.
         *   Subscribe to listener.notify to receive events.
         *   @returns the listener Deferred
         */
        listen: function (subId) {
            return this.send("/events/listen", [subId]);
        },
        subscribe: function (subscriptions, exclusions) {
            return this.send("/events/subscribe", [JSON.stringify(subscriptions), JSON.stringify(exclusions)]);
        },
        unsubscribe: function (subId) {
            return this.send("/events/unsubscribe", [subId]);
        },
        setSubscriptionId: function (subId) {
            return this.send("/events/getSubscriptionId", [subId]);
        },
        getSubscriptionId: function () {
            return this.send("/events/getSubscriptionId", []);
        },
        addSubscription: function (subId, subscriptions, exclusions) {
            return this.send("/events/addSubscription", [subId, subscriptions, exclusions]);
        },
        setSubscription: function (subId, subscriptions, exclusions) {
            return this.send("/events/setsubscription", [subId, JSON.stringify(subscriptions), JSON.stringify(exclusions)]);
        },
        getSubscription: function (subId) {
            return this.send("/events/getSubscription", [subId]);
        },
        removeSubscription: function (subId, subscriptions, exclusions) {
            return this.send("/events/removeSubscription", [subId, subscriptions, exclusions]);
        },
        changeSubscriptionTimeouts: function (subId, polltimeout, maxkeepalive) {
            return this.send("/events/changeSubscriptionTimeouts", [subId, polltimeout, maxkeepalive]);
        },
        listPublisher: function () {
            return this.transport.listPublisher();
        },
        /**
         *   Stop polling events
         */
        stopListen: function () {
        },
        /**
         *   Send a message with the given params to the server.
         *   Please make sure that you specify the parameters in the correct order.
         *
         *   @param action: The desired action (e.g. "/linkgrabber/add") as a string
         *   @param params: An array of primitives containing the parameters for the action.
         *     @param localModeCallback: OPTIONAL, function that gets called with TRUE if request is sent via localmode
         *   @returns a $.Deferred that gets resolved as soon as the action has been done.
         *   If you are interested in the results, subscribe to returnedDeferred.done
         */
        send: function (action, params, localModeCallback) {
            var ret = $.Deferred();
            this.apiDeviceController.getDeviceAPIForId(this.getActiveDevice()).done(function (deviceAPI) {
                deviceAPI.call(action, params, localModeCallback).then(ret.resolve, ret.reject, ret.progress);
            });
            return ret;
        },
        fetchActiveDeviceURL: function () {
            var ret = $.Deferred();
            this.apiDeviceController.getDeviceAPIForId(this.getActiveDevice()).done(function (deviceAPI) {
                var url = deviceAPI.getURL();
                ret.resolve(url);
            });
            return ret;
        },
        fetchActiveDeviceTokenRoot: function () {
            var ret = $.Deferred();
            this.apiDeviceController.getDeviceAPIForId(this.getActiveDevice()).done(function (deviceAPI) {
                var url = deviceAPI.getTokenRoot();
                ret.resolve(url);
            });
            return ret;
        },
        isActiveDeviceInLocalMode: function () {
            var ret = $.Deferred();
            this.apiDeviceController.getDeviceAPIForId(this.getActiveDevice()).done(function (deviceAPI) {
                var local = deviceAPI.isInLocalMode();
                ret.resolve(local);
            });
            return ret;
        },
        /**
         *   @returns a dictionary (object) containing the necessary data
         *   for the next authentication.
         */
        getAuth: function () {
            return this.transport.getAuth();
        },
        // Register callback function that gets called if api logs out
        // callback function gets called with a 'reason' string object
        addAPIStateChangeListener: function (callback) {
            this.jdAPICore.addAPIStateChangeListener(callback);
        },
        getAPIState: function () {
            return this.jdAPICore.getAPIState();
        },
        getAPIStatePlain: function () {
            return this.jdAPICore.getAPIStatePlain();
        },
        getCurrentUser: function () {
            return this.jdAPICore.getCurrentUser();
        },
        getCurrentSessionInfo: function () {
            var sessionInfo = this.jdAPICore.getSessionInfo();
            var result = {
                s: sessionInfo.sessiontoken,
                r: sessionInfo.regaintoken,
                e: sessionInfo.serverEncryptionToken.toString(CryptoJS.enc.Base64),
                d: sessionInfo.deviceSecret.toString(CryptoJS.enc.Base64)
            };
            return result;
        }
    });
    /**
     * Export jdapi constructor
     */
    return API;
});