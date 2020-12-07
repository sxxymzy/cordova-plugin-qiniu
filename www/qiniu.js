
var cordova = require('cordova');

var qiniu = {
    upload: function (filePath, remoteKey, token, callbacks, options) {
        cordova.exec(callbacks.successCbk, callbacks.errorCbk, 'upload', 'uploadFile',
            [filePath, remoteKey, token, callbacks.progressCbk, options]);
    }
}

module.exports = qiniu
