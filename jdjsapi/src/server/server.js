define("serverServer", ["coreCrypto", "coreCryptoUtils"], function (CoreCrypto, CryptoUtils) {
    /**
     * API to handle server calls
     */
    var JDAPIServer = function (jdapiCore) {
        this.jdapiCore = jdapiCore;
    };
    $.extend(JDAPIServer.prototype, {
        listDevices: function () {
            return this.jdapiCore.serverCall("listdevices");
        },
        /**
         * Calls that don't require authentication, and thus are made directly and not via the JDAPICore
         */
        getCaptcha: function () {
            var request = $.ajax({
                url: this.jdapiCore.API_ROOT + "/captcha/getCaptcha",
                type: "post",
                dataType: "json"
            });
            return request;
        },
        /* Register a new user account on the api */
        registerUser: function (data) {
            if (!(/.@./.test(data.email)) || !data.captchaChallenge || !data.captchaResponse) throw "Invalid parameters";
            data.referrer = "webui";
            var requestURL = this.jdapiCore.API_ROOT + "/my/requestregistrationemail?email=" + data.email + "&captchaResponse=" + data.captchaResponse + "&captchaChallenge=" + data.captchaChallenge + "&referer=" + data.referer;
            return $.ajax({
                url: requestURL,
                type: "POST",
                dataType: "text"
            });
        },
        /* send email validationkey to the server */
        confirmEmail: function (email, validationkey, pass) {
            if (!pass) throw "No credentials given";
            var action = "finishregistration";
            var loginSecret = CryptoUtils.hashPassword(email, pass, "server");
            var registerKey = CoreCrypto.enc.Hex.parse(validationkey);
            var iv = registerKey.firstHalf();
            var key = registerKey.secondHalf();
            var encrypted = CoreCrypto.AES.encrypt(loginSecret, key, {
                mode: CoreCrypto.mode.CBC,
                iv: iv
            });
            var stringEnc = CoreCrypto.enc.Hex.stringify(encrypted.ciphertext);
            var queryString = "/my/" + action + "?email=" + email + "&loginSecret=" + stringEnc;
            queryString += "&signature=" + CoreCrypto
                .HmacSHA256(CoreCrypto.enc.Utf8.parse(queryString), registerKey).toString(this.jdapiCore.TRANSFER_ENCODING);
            var confirm = $.ajax({
                url: this.jdapiCore.API_ROOT + queryString,
                type: "POST",
                dataType: "text"
            });
            return confirm;
        },
        /* send request to server to send password change email */
        requestPasswordChangeEmail: function (email, captchaChallenge, captchaResponse) {
            // craft query string
            var params = {};
            var action = "requestpasswordresetemail";

            params.email = email;
            params.captchaResponse = captchaResponse;
            params.captchaChallenge = captchaChallenge;

            var queryString = "/my/" + action + "?" + $.param(params);

            // issue authentication request
            var confirm = $.ajax({
                url: this.jdapiCore.API_ROOT + queryString,
                type: "POST",
                dataType: "text"
            });

            return confirm;
        },
        requestTerminationEmail: function (captchaChallenge, captchaResponse) {
            return this.jdapiCore.serverCall("requestterminationemail", {
                captchaResponse: captchaResponse,
                captchaChallenge: captchaChallenge
            });
        },
        finishTermination: function (email, pw, keyParam, captchaChallenge, captchaResponse) {
            var options = {email: email, pass: pw};
            CryptoUtils.processPassword(options);
            var action = "finishtermination";
            var keyHex = CoreCrypto.enc.Hex.parse(keyParam);
            var iv = keyHex.firstHalf();
            var key = keyHex.secondHalf();
            var encrypted = CoreCrypto.AES.encrypt(options.loginSecret, key, {
                mode: CoreCrypto.mode.CBC,
                iv: iv
            });
            var stringEnc = CoreCrypto.enc.Hex.stringify(encrypted.ciphertext);
            var queryString = "/my/" + action + "?email=" + email + "&loginSecret=" + stringEnc + "&captchaResponse=" + captchaResponse + "&captchaChallenge=" + captchaChallenge;
            queryString += "&signature=" + CoreCrypto
                .HmacSHA256(CoreCrypto.enc.Utf8.parse(queryString), keyHex).toString(this.jdapiCore.transferEncoding);
            var finish = $.ajax({
                url: this.jdapiCore.API_ROOT + queryString,
                type: "POST",
                dataType: "text"
            });
            return finish;
        },
        /* send request to server to change password */
        changePassword: function (email, newpass, key) {

            // craft query string
            var action = "finishpasswordreset";

            var loginSecret = CryptoUtils.hashPassword(email, newpass, "server");
            var registerKey = CoreCrypto.enc.Hex.parse(key);

            var iv = registerKey.firstHalf();
            var key = registerKey.secondHalf();

            var encrypted = CoreCrypto.AES.encrypt(loginSecret, key, {
                mode: CoreCrypto.mode.CBC,
                iv: iv
            });

            var stringEnc = CoreCrypto.enc.Hex.stringify(encrypted.ciphertext);
            var queryString = "/my/" + action + "?email=" + email + "&loginSecret=" + stringEnc;

            queryString += "&signature=" + CoreCrypto
                .HmacSHA256(CoreCrypto.enc.Utf8.parse(queryString), registerKey).toString(this.jdapiCore.TRANSFER_ENCODING);

            // issue authentication request
            var confirm = $.ajax({
                url: this.jdapiCore.API_ROOT + queryString,
                type: "POST",
                dataType: "text"
            });
            return confirm;
        },
        /* send feedback message to the server */
        feedback: function (data) {
            return this.jdapiCore.serverCall("feedback", data);
        }
    });
    return JDAPIServer;
});