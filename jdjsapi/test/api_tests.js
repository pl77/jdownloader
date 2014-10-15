module("Intial Connection Tests");

test("all variables available", function() {
	ok(EMAIL && EMAIL !== "", "Valid Email");
	ok(PASSWORD && PASSWORD !== "", "Valid Password");
	ok(window.jd.API, "API Constructor available");
});

asyncTest("connect, reconnect and disconnect call", function() {
	expect(3);
	var deferred = $.Deferred();
	deferred.done(function() {
		ok(true, "Connected");
		api.reconnect().done(function() {
			ok(true, "Reconnect success");
			api.disconnect().done(function() {
				ok(true, "Disconnect success");
				start();
			}).fail(function() {
				ok(false, "Disconnect failed");
				start();
			});
		}).fail(function() {
			ok(false, "Reconnect failed");
			start();
		});
	});
	deferred.fail(function(){
		//Connect failed
	});
	var api = new window.jd.API({
		email: EMAIL,
		pass: PASSWORD
	}, deferred);
});

module("Server Calls Tests", {
	//Setup up API
	setup: function() {
		//interrupt execution while API initializes
		stop();
		api = new window.jd.API({
			email: EMAIL,
			pass: PASSWORD
		}, function() {
			start();
		});
	},
	//Disconnect API
	teardown: function() {
		stop();
		api.disconnect().always(function() {
			start();
		});
	}
});

asyncTest("list devices", function() {
	expect(1);
	api.listDevices().done(function(devices) {
		ok(devices, "DEVICE LIST RECEIVED");
		start();
	});
});

asyncTest("get captchas", function() {
	expect(1);
	var api = new window.jd.API();
	api.getCaptcha().done(function(captcha) {
		ok(captcha, "done");
		start();
	});
});

asyncTest("register user", function() {
	expect(1);
	var api = new window.jd.API();
	api.registerUser({
		email: "testdummy@testmail.de",
		captchaChallenge: "asdf",
		captchaResponse: "asdf"
	}).fail(function(success) {
		//successfully failed, test by hand because of captcha challenge
		ok(success, "fail");
		start();
	});
});

asyncTest("Make service call", function() {
	expect(1);
	api.sendServiceRequest("db", "http://stats.appwork.org", "/data/db/getLiveView", "null&null").done(function(res) {
		ok(res, "Response received");
		start();
	});
});

asyncTest("Test request queueing", function() {
	expect(1);
	api.reconnect();
	api.reconnect();
	//Make call without waiting for reconnect to return
	api.listDevices().done(function(devices) {
		ok(devices, "DEVICE LIST RECEIVED");
		start();
	});
});

/**
 * Possible API Errors
 */
var emailInvalidError = {
	src: "MYJD",
	type: "EMAIL_INVALID"
};
var tokenInvalidError = {
	src: "MYJD",
	type: "TOKEN_INVALID"
};
var authFailedError = {
	src: "MYJD",
	type: "AUTH_FAILED"
};

/**
 * Retrive encryption token to mock server
 */
//require(["coreCryptoUtils"], function(cryptoUtils) {
//
//	var setupAjaxMock = function() {
//		var restoredOptions = JSON.parse(localStorage.getItem("jdapi/src/core/core.js"));
//		var encryptionToken = {};
//		encryptionToken.server = CryptoJS.lib.WordArray.random(restoredOptions.serverEncryptionToken.sigBytes);
//		encryptionToken.device = CryptoJS.lib.WordArray.random(restoredOptions.deviceEncryptionToken.sigBytes);
//
//		var iv = encryptionToken.server.firstHalf();
//		var key = encryptionToken.server.secondHalf();
//
//		console.log("IV " + iv);
//		console.log("KEY " + key);
//
//		var testCipherText = cryptoUtils.encryptJSON(key, {'test': 0});
//
//		console.log(cryptoUtils.decryptJSON(encryptionToken.server, null, testCipherText));
//
//		$.mockjax({
//			url: 'http://api.jdownloader.org/my/listdevices*',
//			status: 200,
//			responseTime: 200,
//			responseText: cryptoUtils.encryptJSON(encryptionToken.server, JSON.stringify({
//				data : {
//					list: []
//				}
//			}))
//		});
//	};
//
//	module("Test with mock objects", {
//		//Setup up API
//		setup: function() {
//			//interrupt execution while API initializes
//			stop();
//			api = new window.jd.API({
//				email: EMAIL,
//				pass: PASSWORD
//			}, function() {
//				setupAjaxMock();
//				start();
//			});
//		},
//		//Disconnect API
//		teardown: function() {
//			stop();
//			api.disconnect().always(function() {
//				start();
//			});
//		}
//	});
//
//	asyncTest("list devices", function() {
//		expect(1);
//		api.listDevices().done(function(devices) {
//			console.log(devices);
//			ok(devices, "DEVICE LIST RECEIVED");
//			start();
//		}).fail(function(err) {
//			ok(true, "DEVICE LIST RECEIVED FAILED");
//			console.error(err);
//		});
//	});
//});