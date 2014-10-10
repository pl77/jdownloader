//Gruntfile
module.exports = function(grunt) {

  //Initializing the configuration object
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    requirejs: {
      compile: {
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
            serviceService: "src/service/service",
            CryptoJS: "vendor/cryptojs"
          },
          out: "build/jdapi.js"
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
      all_tests: ['test/*.html']
      // individual_tests: {
      //   files: [
      //     {src: 'test/*1.html'},
      //     {src: 'test/*{1,2}.html'},
      //   ]
      // },
      // urls: {
      //   options: {
      //     urls: [
      //       'http://localhost:9000/test/qunit1.html',
      //       'http://localhost:9001/qunit2.html',
      //     ]
      //   },
      // },
      // urls_and_files: {
      //   options: {
      //     urls: '<%= qunit.urls.options.urls %>',
      //   },
      //   src: 'test/*{1,2}.html',
      // },
      // noglobals: {
      //   options: {
      //     noGlobals: true,
      //     force: true
      //   },
      //   src: 'test/qunit3.html'
      // }
    }
  });

  // Plugin loading
  grunt.loadNpmTasks('grunt-contrib-requirejs');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-qunit');

  // Task definition
  grunt.registerTask('default', ['requirejs']);
};