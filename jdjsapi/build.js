({
	baseUrl: "./",
	/*name: "src/jdapi",*/
	name: "jdapi",
	paths: {
		jdapi: "src/jdapi",
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
	},
	out: "build/jdapi.js"
})