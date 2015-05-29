/*
 *  MyJD Javascript API Gruntfile.js
 *
 * What this script does:
 * 1. Get all dependencies from npm
 * 2. Get all dependencies from bower
 * 3. Replace console logging switch value with false
 * 4. Concat all files and generate a single, optimized jdapi.js file located in build/jdapi.js and
 *    a minified version in build/jdapi.min.js
 */

module.exports = function (grunt) {

    //Initializing the configuration object
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        'string-replace': {
            dist: {
                files: [{
                    expand: true,
                    src: 'src/jdapi.js'
                }],
                options: {
                    replacements: [{
                        pattern: 'var LOGGING_ENABLED = true;',
                        replacement: 'var LOGGING_ENABLED = false;'
                    }]
                }
            },
            version: {
                files: [{
                    expand: true,
                    src: ['build/jdapi.js', 'build/jdapi.min.js']
                }],
                options: {
                    replacements: [{
                        pattern: /@version \d+\.\d+\.\d+?/g,
                        replacement: ( '@version <%= pkg.version %>')
                    }]
                }
            }
        },
        concat: {
            cryptojs: {
                files: {
                    'vendor/cryptojs.js': ['bower_components/crypto-js/core.js', 'bower_components/crypto-js/hmac-sha256.js', 'bower_components/crypto-js/aes.js']
                }
            },
            header: {
                files: {
                    'build/jdapi.js': ['src/lib_header.js', 'build/jdapi.js'],
                    'build/jdapi.min.js': ['src/lib_header.js', 'build/jdapi.min.js']
                }
            }
        },
        requirejs: {
            compile: {
                options: {
                    baseUrl: "./",
                    name: "jdapi",
                    optimize: "none",
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
                        serviceService: "src/service/service"
                    },
                    out: "build/jdapi.js"
                }
            }, compileMin: {
                options: {
                    baseUrl: "./",
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
                        serviceService: "src/service/service"
                    },
                    out: "build/jdapi.min.js"
                }
            }
        },
        watch: {
            requirejs: {
                files: ["src/**/*.js", "test/**/*.js", "*.html"],
                tasks: ["requirejs"],
                options: {
                    livereload: true
                }
            }
        },
        // Unit tests.
        qunit: {
            all: ['qunit-crypto.html']
        },
        bower: {
            install: {
                options: {
                    targetDir: "bower_components"
                }
            }
        }
    });

    // Plugin loading
    grunt.loadNpmTasks('grunt-contrib-requirejs');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-qunit');
    grunt.loadNpmTasks('grunt-bower-task');
    grunt.loadNpmTasks('grunt-npm-install');
    grunt.loadNpmTasks('grunt-string-replace');

    // Task definition
    grunt.registerTask('default', ['npm-install', 'bower', 'concat:cryptojs', 'string-replace:dist', 'requirejs:compile', 'requirejs:compileMin', 'concat:header', 'string-replace:version']);
};