// Simplify some cordova functions to be used
var exec = require('cordova/exec');
var argscheck = require('cordova/argscheck');
var getValue = argscheck.getValue;

/**
 * @exports fetch
 */
var fileDetailsExport = {};

/**
 * Fetch Details Function
 * @param {function} successCallback - function triggered if successful
 * @param {function} errorCallback - function triggered if failed
 */
fileDetailsExport.fetch = function (successCallback, errorCallback, options) {

    options = options || {};

    // Prepare the arguments to be passed to the native function
    var args = [];
    args.push(getValue(options.filePath, false)); // File Path

    exec(successCallback, errorCallback, 'FileDetails', 'fetch', args);
};

// Export the functions
module.exports = fileDetailsExport;