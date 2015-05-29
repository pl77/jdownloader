define("serviceService", ["coreCryptoUtils", "coreCrypto"], function (CryptoUtils, CoreCrypto) {
	/**
	 * API to handle service calls, eg the stats server
	 */
	var JDAPIService = function(jdapiCore) {
		this.jdapiCore = jdapiCore;
	};
	$.extend(JDAPIService.prototype, {
		/**
		 * Services Stuff
		 */
		serviceAccessTokens: {},
		requestAccessToken: function(servicename) {
			//token already available
			if (this.serviceAccessTokens[servicename]) {
				var tokens = $.Deferred();
				tokens.resolve(this.serviceAccessTokens[servicename]);
				return tokens;
			}
			//else request token first!
			//create container object
			this.serviceAccessTokens[servicename] = {};
			var serviceAccessTokensContainer = this.serviceAccessTokens[servicename];

			if (serviceAccessTokensContainer.requestOnTheWay) {
				var enqueueDef = $.Deferred();
				serviceAccessTokensContainer.deferredQueue = serviceAccessTokensContainer.deferredQueue || new Array();
				serviceAccessTokensContainer.deferredQueue.push(enqueueDef);
				return enqueueDef;
			}
			//flag request is on the way
			serviceAccessTokensContainer.requestOnTheWay = true;
			// if token request already sent, queue return value
			var params = {};
			var action = "";
			action = "requestaccesstoken";
			params.service = servicename;
			var request = this.jdapiCore.serverCall(action, params);
			request.done(function(tokens){
				$.extend(serviceAccessTokensContainer, tokens);
			});
			return request;
		},
		send: function(servicename, url, action, params) {
			var returnDeferred = $.Deferred();
			var self = this;
			this.requestAccessToken(servicename).done(function(tokens) {
				var queryString = action + "?" + params + "&rid=" + 12 + "&accesstoken=" + tokens.accessToken;
				queryString = encodeURI(queryString);
				queryString += "&signature=" + CoreCrypto.HmacSHA256(CoreCrypto.enc.Utf8.parse(queryString), CoreCrypto.enc.Hex.parse(tokens.accessSecret)).toString(CoreCrypto.enc.Hex);

				var confirm = $.ajax({
					url: url + queryString,
					type: "GET"
				});
				confirm.done(function(res) {
					//increment successful request count
					if (self.serviceAccessTokens[servicename]) {
						var tokens = self.serviceAccessTokens[servicename];
						tokens.used++;
					}
					returnDeferred.resolve(res);
				}).fail(function(res) {
					if (self.serviceAccessTokens[servicename] && self.serviceAccessTokens[servicename].count > 0) {
						//retry with new token
						delete self.serviceAccessTokens[servicename];
						self.sendServiceRequest(servicename, url, action, params).then(returnDeferred.resolve, returnDeferred.reject);
					} else {
						//fail finally
						delete self.serviceAccessTokens[servicename];
						returnDeferred.reject(res);
					}
				});
			}).fail(function(err) {
				returnDeferred.reject(err);
			});
			return returnDeferred;
		}
	});
	return JDAPIService;
});