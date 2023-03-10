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
 * @param {string} filePath - file path of the file to be processed
 * @param {function} successCallback - function triggered if successful
 * @param {function} errorCallback - function triggered if failed
 */
fileDetailsExport.fetch = function (filePath, successCallback, errorCallback) {
    // Prepare the arguments to be passed to the native function
    var args = [];
    args.push(getValue(filePath, false)); // File Path

    exec(successCallback, errorCallback, 'FileDetails', 'fetch', args);
};

// Export the functions
module.exports = fileDetailsExport;