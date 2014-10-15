/**
 * Wrap console to disable .log output in production
 */
var LOGGING_ENABLED = false;
var logger = {
	log: function(msg) {
		if (LOGGING_ENABLED) console.log(msg);
	},
	warn: function(msg) {
		console.warn(msg);
	},
	error: function(msg) {
		console.error(msg);
	}
};
/**
 * Global config variables
 */
define("config/config", function() {
	return {
		TRANSFER_ENCODING: CryptoJS.enc.Hex,
		API_ROOT: "http://api.jdownloader.org",
		LOCAL_STORAGE_KEY: "api.transport.options",
		APP_KEY: "JD_WEBUI_NEW_399"
	};
});
/**
 * RequireJS config
 */
require.config({
	paths: {
		jdapi: "build/jdapi",
		jdapi_unit_tests: "qunit/unit_tests",
		coreCore: "src/core/core",
		coreCrypto: "src/core/crypto",
		coreCryptoUtils: "src/core/cryptoUtils",
		coreRequest: "src/core/request",
		coreRequestHandler: "src/core/coreRequestHandler",
		device: "src/device/device",
		deviceController: "src/device/deviceController",
		serverServer: "src/server/server",
		serviceService: "src/service/service",
		CryptoJS: "vendor/cryptojs"
	}
});
/**
 * Main Library Object, contains all functions the library makes available for clients
 */
define("jdapi_unit_tests", ["coreCore", "device", "serverServer", "serviceService", "deviceController"], function(JDAPICore, JDAPIDevice, JDAPIServer, JDAPIService, JDAPIDeviceController) {
	/* API Core */
	var APITests = function(options, onConnectedDeferred, APP_KEY) {
		this.options = options;
		this.onConnectedDeferred = onConnectedDeferred;
		this.APP_KEY = APP_KEY;
		//Setup API Components
		// this.jdAPICore = new JDAPICore(options, onConnectedDeferred, APP_KEY);
		// this.apiServer = new JDAPIServer(this.jdAPICore);
		// this.apiService = new JDAPIService(this.jdAPICore);
		// this.apiDeviceController = new JDAPIDeviceController(this.apiServer, this.jdAPICore);
	};
	APITests.prototype = {
		testCore : function(){
			module("Test JDAPI Core");
			test("JDAPI Core Set Up", function(){
				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
				ok("initialized");
			});
//			test("JDAPI Core Interface functions available", function(assert){
//				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
//				assert.ok($.isFunction(jdapiCore.connect), "connect available");
//				assert.ok($.isFunction(jdapiCore.reconnect), "reconnect available");
//				assert.ok($.isFunction(jdapiCore.disconnect), "disconnect available");
//
//				assert.ok($.isFunction(jdapiCore.serverCall), "serverCall available");
//				assert.ok($.isFunction(jdapiCore.deviceCall), "deviceCall available");
//				assert.ok($.isFunction(jdapiCore.localDeviceCall), "localDeviceCall available");
//
//				assert.ok($.isFunction(jdapiCore.getSessionToken), "getSessionToken available");
//				assert.ok($.isFunction(jdapiCore.addAPIStateChangeListener), "addAPIStateChangeListener available");
//				assert.ok($.isFunction(jdapiCore.getAPIState), "getAPIState available");
//				assert.ok($.isFunction(jdapiCore.getCurrentUser), "getCurrentUser available");
//			});
//			test("JDAPI Core Test Getter", function(assert){
//				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
//				assert.ok($.isNumeric(jdapiCore.getAPIState()), "getAPIState works");
//				var currentUser = jdapiCore.getCurrentUser();
//
//				assert.notEqual(currentUser.loggedIn, undefined, "loggedIn flag undefined");
//				assert.notEqual(currentUser.loggedIn, null, "loggedIn flag null");
//
//				// assert.notEqual(currentUser.name, undefined, "name undefined");
//				// assert.notEqual(currentUser.name, null, "name null");
//			});
		},
		testApiServer: function(){
			module("Test JDAPI Server");
			test("JDAPI Server Set Up", function(){
				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
				var apiServer = new JDAPIServer(jdapiCore);
				ok("initialized");
			});
			test("JDAPI Server Interface functions available", function(assert){
				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
				var apiServer = new JDAPIServer(jdapiCore);

				assert.ok($.isFunction(apiServer.listDevices), "listDevices available");
				assert.ok($.isFunction(apiServer.getCaptcha), "getCaptcha available");
				assert.ok($.isFunction(apiServer.registerUser), "registerUser available");
				assert.ok($.isFunction(apiServer.confirmEmail), "confirmEmail available");
				assert.ok($.isFunction(apiServer.requestPasswordChangeEmail), "requestPasswordChangeEmail available");
				assert.ok($.isFunction(apiServer.requestTerminationEmail), "requestTerminationEmail available");
				assert.ok($.isFunction(apiServer.finishTermination), "finishTermination available");
				assert.ok($.isFunction(apiServer.changePassword), "changePassword available");
				assert.ok($.isFunction(apiServer.feedback), "feedback available");
			});

			function isDeferred(object){
				return object !== undefined && object !== null && $.isFunction(object.then);
			}

			test("JDAPI Server Interface functions return types", function(assert){
				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
				var apiServer = new JDAPIServer(jdapiCore);
				
				assert.ok(isDeferred(apiServer.listDevices()), "listDevices return type correct");
				assert.ok(isDeferred(apiServer.getCaptcha()), "getCaptcha return type correct");
				assert.ok(isDeferred(apiServer.registerUser({email: "test@appwork.org", captchaChallenge: "asdf", captchaResponse: "asdfasdf"})), "registerUser return type correct");
				assert.ok(isDeferred(apiServer.confirmEmail("test@appwork.org", "test", "test123")), "confirmEmail return type correct");
				assert.ok(isDeferred(apiServer.requestPasswordChangeEmail()), "requestPasswordChangeEmail return type correct");
				assert.ok(isDeferred(apiServer.requestTerminationEmail()), "requestTerminationEmail return type correct");
				assert.ok(isDeferred(apiServer.finishTermination("test@appwork.org", "test", "asdf", "aasdfasdf", "asdfasdfasdf")), "finishTermination return type correct");
				assert.ok(isDeferred(apiServer.changePassword("test@appwork.org", "jklö", "asdfasdf")), "changePassword return type correct");
				/*assert.ok(isDeferred(apiServer.feedback()), "feedback return type correct");*/
			});
		},
		testApiService: function(){
			module("Test JDAPI Service");
			test("JDAPI Service Set Up", function(){
				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
				var apiService = new JDAPIService(jdapiCore);

				ok("initialized");
			});
			test("JDAPI Service Interface functions available", function(assert){
				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
				var apiService = new JDAPIService(jdapiCore);

				assert.ok($.isFunction(apiService.requestAccessToken), "requestAccessToken available");
				assert.ok($.isFunction(apiService.send), "send available");
			});
		},
		testDeviceController: function(){
			module("Test Device Controller");
			test("JDAPI DeviceController Set Up", function(){
				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
				var apiServer = new JDAPIServer(jdapiCore);
				var deviceController = new JDAPIDeviceController(apiServer, jdapiCore);
				
				ok("initialized");
			});
			test("JDAPI DeviceController functions available", function(assert){
				var jdapiCore = new JDAPICore(this.options, this.onConnectedDeferred, this.APP_KEY);
				var apiServer = new JDAPIServer(jdapiCore);
				var deviceController = new JDAPIDeviceController(apiServer, jdapiCore);

				assert.ok($.isFunction(deviceController.refreshAllDeviceAPIs), "refreshAllDeviceAPIs available");
				assert.ok($.isFunction(deviceController.getDeviceAPIForId), "getDeviceAPIForId available");
			});
		}
	};

	return APITests;
});