define("coreRequest", ["coreCrypto", "coreCryptoUtils"], function (CoreCrypto, CryptoUtils) {

    /**
     * Static variables and functions
     */
    var requestCount = 0;

    var processOptions = function (options) {
        // SERVER_CALL
        // if URL params are included build querystring with
        // sessiontoken and signature
        if (options.jdParams) {
            if (options.type === "GET") {
                var queryString = "/my/" + options.jdAction + "?sessiontoken=" + options.sessiontoken + "&rid=" + options.rid + $
                        .param(options.jdParams);
            } else {
                if (options.serverEncryptionToken) {
                    var queryString;
                    var unencrypted;
                    var payload;
                    if (options.jdAction === "requestterminationemail") {
                        queryString = "/my/" + options.jdAction + "?sessiontoken=" + options.sessiontoken + "&" + $
                            .param(options.jdParams) + "&rid=" + options.rid;
                    } else {
                        queryString = "/my/" + options.jdAction + "?sessiontoken=" + options.sessiontoken + "&rid=" + options.rid;
                        unencrypted = {
                            "apiVer": 1,
                            "params": [],
                            "url": queryString,
                            "rid": options.rid
                        }

                        if (JSON.stringify(options.jdParams) !== "{}") {
                            // Do net send empty param
                            unencrypted.params = [options.jdParams];
                        }

                        payload = CryptoUtils.encryptJSON(options.serverEncryptionToken, unencrypted);
                        options.data = payload;
                    }
                    options.contentType = "application/json; charset=utf-8";
                } else {
                    logger.error("[MYJD] [JSAPI] [REQUEST] [FAILED] Server encryption token missing. Action: " + JSON.stringify(options ? options.jdAction : "NO_ACTION"));
                }
            }
            queryString += "&signature=" + CoreCrypto
                .HmacSHA256(CoreCrypto.enc.Utf8.parse(queryString), options.serverEncryptionToken)
                .toString(options.TRANSFER_ENCODING);
            options.url = options.API_ROOT + queryString;
        } else {
            if (options.deviceEncryptionToken) {
            options.data = CryptoUtils.encryptJSON(options.deviceEncryptionToken, options.jdData);
            options.contentType = "application/json";
            options.url = options.API_ROOT + "/t_" + options.sessiontoken + "_" + options.deviceId + options.jdAction;
            } else {
                logger.error("[MYJD] [JSAPI] [REQUEST] [FAILED] " + JSON.stringify((options ? options.type : "NO_OPTIONS")) + " Error: Device encryption token missing!");
            }
        }
    };

    /**
     * Constructor for new JDAPIRequest Object
     */
    var JDAPIRequest = function (options) {

        // options.jdParams = options.jdParams || {};
        var self = this;
        /*
         * processes the options object e.g. encrypting everything
         * with the required secrets
         */
        processOptions(options);
        this.options = options;
    };
    $.extend(JDAPIRequest.prototype, {
        send: function () {
            var filter = $.Deferred();
            var self = this;
            var options = this.options;
            var apiRequest = $.ajax(this.options);

            apiRequest.done(function (response) {
                /*
                 * DEBUG: Trigger random reconnects
                 */
                // if (Math.random() < 0.2) {
                // 	filter.reject({
                // 		type: "TOKEN_INVALID"
                // 	});
                // 	return filter;
                // } else {
                logger.log("[MYJD] [JSAPI] [REQUEST] " + JSON.stringify(options ? options.type : "NO_TYPE") + " " + JSON.stringify(options ? options.url : "NO_URL") + "\nOPTIONS:\n" + JSON.stringify(options ? options : "NO_OPTIONS") + "\n\nRESPONSE:\n" + JSON.stringify(response));
                filter.resolve(response);
                // }
            });
            apiRequest.fail(function (error) {
                if (error.responseText) {
                    filter.reject(JSON.parse(error.responseText));
                } else {
                    filter.reject({
                        type: "UNKNOWN_ERROR"
                    });
                }
                logger.error("[MYJD] [JSAPI] [REQUEST] [FAILED] " + JSON.stringify(options ? options.type : "NO_TYPE") + " " + JSON.stringify(options ? options.url : "NO_URL") + "\nOPTIONS:\n" + JSON.stringify(options ? options : "NO_OPTIONS") + "\n\nRESPONSE:\n" + JSON.stringify(error));
            });
            return filter;
        }
    });
    return JDAPIRequest;
});