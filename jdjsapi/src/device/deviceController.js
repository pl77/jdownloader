define("deviceController", ["device"], function (JDAPIDevice) {

    /* Timeouts for direct connection mode detection */
    var GET_DIRECT_CONNECTION_INFOS_TIMEOUT = 800;
    var PING_TIMEOUT = 400;

    /* Iterator http://stackoverflow.com/questions/12079417/javascript-iterators */
    var Iterator = function (items) {
        this.index = 0;
        this.items = items || [];
    };

    $.extend(Iterator.prototype, {
        first: function () {
            this.reset();
            return this.next();
        },
        next: function () {
            return this.items[this.index++];
        },
        hasNext: function () {
            return this.index < this.items.length;
        },
        reset: function () {
            this.index = 0;
        },
        each: function (callback) {
            for (var item = this.first(); this.hasNext(); item = this.next()) {
                callback(item);
            }
        }
    });

    var JDAPIDeviceController = function (jdAPIServer, jdAPICore) {
        this.jdAPIServer = jdAPIServer;
        this.jdAPICore = jdAPICore;
        this.devices = {};
    };

    $.extend(JDAPIDeviceController.prototype, {
        refreshAllDeviceAPIs: function () {
            var ret = $.Deferred();
            var listCall = this.jdAPIServer.listDevices();
            var self = this;

            listCall.done(function (devices) {
                self._iterateAndCheckForLocalMode(new Iterator(devices.list), function () {
                    //Attach localMode flag to devices
                    for (var i = 0; i < devices.list.length; i++) {
                        devices.list[i].localMode = self.devices[devices.list[i].id].isInLocalMode();
                    }
                    ret.resolve(devices.list);
                });
                self._iterateAndCheckForSessionPublicKey(new Iterator(devices.list), function () {
                    for (var i = 0; i < devices.list.length; i++) {

                    }
                });
            });
            return ret;
        },
        _iterateAndCheckForSessionPublicKey: function (iterator, finishedCallback) {
            var dev = iterator.next();
            //Last element reached
            if (!dev) {
                finishedCallback();
                return;
            }
            //put device on device list
            this.devices[dev.id] = this.devices[dev.id] || new JDAPIDevice(this.jdAPICore, dev);
            var self = this;
            this.jdAPICore.deviceCall(dev.id, "/device/getSessionPublicKey", [], undefined).done(function (result) {
                if (!result || !result.data) {
                    self._iterateAndCheckForSessionPublicKey(iterator, finishedCallback);
                } else {
                    self.devices[dev.id].rsaPublicKey = "-----BEGIN RSA PRIVATE KEY-----" + result.data + "-----END RSA PRIVATE KEY-----";
                    console.log(JSON.stringify(self.devices[dev.id]));
                    self.jdAPICore.deviceCall(dev.id, "/update/isUpdateAvailable", [], self.devices[dev.id].rsaPublicKey).done(function (result) {
                        console.log(JSON.stringify(result));
                    }).fail(function (result) {
                        console.log(JSON.stringify(result));
                    });
                }
            }).fail(function () {
                //on fail, just skip this device
                self._iterateAndCheckForSessionPublicKey(iterator, finishedCallback);
            });
        },
        _iterateAndCheckForLocalMode: function (iterator, finishedCallback) {
            var dev = iterator.next();
            //Last element reached
            if (!dev) {
                finishedCallback();
                return;
            }
            //put device on device list
            this.devices[dev.id] = new JDAPIDevice(this.jdAPICore, dev);
            var self = this;
            this.jdAPICore.deviceCall(dev.id, "/device/getDirectConnectionInfos", [], undefined, GET_DIRECT_CONNECTION_INFOS_TIMEOUT).done(function (result) {
                if (!result || !result.data || !result.data.infos || !result.data.infos.length === 0) {
                    self._iterateAndCheckForLocalMode(iterator, finishedCallback);
                } else {
                    var resultIter = new Iterator(result.data.infos);
                    self._iterateAndPingForAvailability(resultIter, dev.id, function () {
                        self._iterateAndCheckForLocalMode(iterator, finishedCallback);
                    });
                }
            }).fail(function () {
                //on fail, just skip this device
                self._iterateAndCheckForLocalMode(iterator, finishedCallback);
            });
        },
        _iterateAndPingForAvailability: function (iterator, deviceId, finishedCallback) {
            var deviceAddress = iterator.next();
            var self = this;
            var localURL = "//" + deviceAddress.ip + ":" + deviceAddress.port;
            if (window.location.protocol && window.location.protocol == "https:") {
                // dyndns service for wildcard certificate
                localURL = "//" + deviceAddress.ip.replace(new RegExp("\\.", 'g'), "-") + ".mydns.jdownloader.org:" + deviceAddress.port;
            }
            //make test call
            var pingCall = self.jdAPICore.localDeviceCall(localURL, deviceId, "/device/ping", [], undefined, PING_TIMEOUT);
            pingCall.done(function (result) {
                self.devices[deviceId].setLocalURL(localURL);
            });
            pingCall.always(function () {
                if (iterator.hasNext()) {
                    self._iterateAndPingForAvailability(iterator, deviceId, finishedCallback);
                } else {
                    if (finishedCallback && typeof finishedCallback === "function") {
                        finishedCallback();
                    }
                }
            });
        },

        /**
         * Construct reusable deviceAPI Interface for given deviceId
         */
        getDeviceAPIForId: function (deviceId) {
            var ret = $.Deferred();
            var self = this;

            var deviceQuery = $.Deferred();
            if (!self.devices[deviceId]) {
                deviceQuery = self.refreshAllDeviceAPIs();
            } else {
                deviceQuery.resolve();
            }

            deviceQuery.done(function () {
                ret.resolve({
                    call: function (action, params) {
                        var innerRet = $.Deferred();
                        var device = self.devices[deviceId];
                        if (!device) {
                            innerRet.reject({
                                responseText: {
                                    type: "DEVICE_NOT_EXISTING"
                                }
                            });
                        } else {
                            device.call(action, params).then(innerRet.resolve, innerRet.reject, innerRet.progress);
                        }
                        return innerRet;
                    },
                    getURL: function () {
                        var device = self.devices[deviceId];
                        if (!device) throw "Device with id " + deviceId + " not found";
                        return device.getURL();
                    },
                    getTokenRoot: function () {
                        var device = self.devices[deviceId];
                        if (!device) throw "Device with id " + deviceId + " not found";
                        return device.getURL() + "/t_" + self.jdAPICore.getSessionToken() + "_" + deviceId;
                    },
                    isInLocalMode: function () {
                        var device = self.devices[deviceId];
                        return device.isInLocalMode();
                    },
                    deviceId: deviceId
                });
            });

            return ret;
        }
    });

    return JDAPIDeviceController;
});