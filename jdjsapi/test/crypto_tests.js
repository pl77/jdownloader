function equals(obj1, obj2) {
	return JSON.stringify(obj1) == JSON.stringify(obj2);
}

require(["coreCryptoUtils"], function(cryptoUtils) {

	module("src/core/crypto.js");

	test("Test WordArray Extension", function(assert) {
		assert.ok($.isFunction(CryptoJS.lib.WordArray.firstHalf), "firstHalf function available");
		assert.ok($.isFunction(CryptoJS.lib.WordArray.secondHalf), "secondHalf function available");

		//Set up test WordArray
		var wordArray = new CryptoJS.lib.WordArray.init([211734923, -1375825582, 1356349181, 769042015, 242038942, -746587025, 1216587888, 417472468]);
		var firstHalf = wordArray.firstHalf();
		var secondHalf = wordArray.secondHalf();

		assert.ok(firstHalf instanceof CryptoJS.lib.WordArray.init, "firstHalf is valid WordArray");
		assert.ok(secondHalf instanceof CryptoJS.lib.WordArray.init, "secondHalf is valid WordArray");

		assert.deepEqual(firstHalf.words, wordArray.words.slice(0, wordArray.words.length / 2), "firstHalf is first half of WordArray");
		assert.deepEqual(firstHalf.words, wordArray.words.slice(0, wordArray.words.length / 2), "secondHalf is second half of WordArray");
	});

	module("src/core/cryptoUtils.js");

	test("Hash Password", function(assert) {
		var testUser = "myjduser@jdownloader.org";
		var password = "supersecret";
		var domain = "device";

		var referenceHash = CryptoJS.SHA256(CryptoJS.enc.Utf8.parse(testUser.toLowerCase() + password + domain.toLowerCase()));
		var hashedPassword = cryptoUtils.hashPassword(testUser, password, domain);

		assert.ok(referenceHash.toString() == hashedPassword.toString(), "Hash correct");
	});

	test("Process Password", function(assert) {
		assert.throws(function() {
			cryptoUtils.processPassword({});
		}, "Bad Arguments Exception thrown");

		// create test options object
		var options = {
			email: "myjduser@jdownloader.org",
			pass: "supersecret"
		};
		cryptoUtils.processPassword(options);
		assert.ok(!options.pass, "Password correctly deleted from options");
		assert.ok(options.loginSecret && options.loginSecret instanceof CryptoJS.lib.WordArray.init, "LoginSecret correctly created on options object");
		assert.ok(options.deviceSecret && options.deviceSecret instanceof CryptoJS.lib.WordArray.init, "DeviceSecret correctly created on options object");
	});

	test("Initialise Connection", function(assert) {
		assert.throws(function() {
			cryptoUtils.initialiseConnection({});
		}, "Invalid Options Exception correct");
		assert.throws(function() {
			cryptoUtils.initialiseConnection({loginSecret: "test", serverEncryptionToken: "test"});
		}, "Missing deviceSecret Exception correct");

		// create test options object
		var options = {
			email: "myjduser@jdownloader.org",
			pass: "supersecret"
		};
		cryptoUtils.processPassword(options);
		
		assert.throws(function(){
			cryptoUtils.initialiseConnection(options);
		}, "Missing sessiontoken and regaintoken Exception correct");

		//initial call of initialiseConnection -> simulates initial connect
		cryptoUtils.initialiseConnection(options, "dummysessiontoken", "dummyrequesttoken");

		assert.ok(!options.loginSecret, "LoginSecret correctly removed from options");
		assert.ok(options.serverEncryptionToken && options.serverEncryptionToken instanceof CryptoJS.lib.WordArray.init, "ServerEncryptionToken correctly set");
		assert.ok(options.deviceEncryptionToken && options.deviceEncryptionToken instanceof CryptoJS.lib.WordArray.init, "DeviceEncryptionToken correctly set");

		//initial call of initialiseConnection -> simulates reconnect
		cryptoUtils.initialiseConnection(options, "dummyreconnectsessiontoken", "dummyreconnectrequesttoken");
		assert.ok(options.serverEncryptionToken && options.serverEncryptionToken instanceof CryptoJS.lib.WordArray.init, "ServerEncryptionToken correctly set after 'reconnect'");
		assert.ok(options.deviceEncryptionToken && options.deviceEncryptionToken instanceof CryptoJS.lib.WordArray.init, "DeviceEncryptionToken correctly set after 'reconnect'");

		assert.ok(options.serverEncryptionTokenOld && options.serverEncryptionTokenOld instanceof CryptoJS.lib.WordArray.init, "serverEncryptionTokenOld correctly set after 'reconnect'");
		assert.ok(options.deviceEncryptionTokenOld && options.deviceEncryptionTokenOld instanceof CryptoJS.lib.WordArray.init, "deviceEncryptionTokenOld correctly set after 'reconnect'");
	});
});