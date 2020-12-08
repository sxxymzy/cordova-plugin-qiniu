
var cordova = require('cordova');

var qiniu = {
    upload: function (filePath, remoteKey, token, callbacks, options) {

        function successOrProgressCbk(rsp) {
            if (rsp.type === 'progress') {
                callbacks.progressCbk(rsp);
            } else {
                callbacks.successCbk(rsp);
            }
        }

        cordova.exec(successOrProgressCbk, callbacks.errorCbk, 'upload', 'uploadFile',
            [filePath, remoteKey, token, options]);
    }
}

module.exports = qiniu
