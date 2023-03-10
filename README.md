---
title: File Details
description: Fetch the file details.
---

# cordova-plugin-file-details

This is a cordova plugin that fetches the details embedded within a file.

## Installation

To install via repo url directly.

    cordova plugin add https://github.com/kepat/cordova-plugin-file-details

### Quirks

This requires cordova 10.0+.

---

# API Reference

* [fileDetails](#module_fileDetails)
    * [.fetch(successCallback, errorCallback, options)](#module_fileDetails.fetch)

---

<a name="module_fileDetails"></a>

## fileDetails

<a name="module_fileDetails.fetch"></a>

### fileDetails.fetch(filepath, successCallback, errorCallback)
Retrieves the file details based on the passed file path.
The details are passed to the success callback.

__Supported Platforms__

- Android
- iOS

__Inputs__

| Param | Type | Description |
| --- | --- | --- |
| filePath | <code>string</code> | The location of the file. |
| successCallback | <code>function</code> | The callback for success. |
| errorCallback | <code>function</code> | The callback for error. |

**Example**  
```js
let filePath = "/var/../..";

fileDetails.fetch(
    filePath,
    function(data) {
        console.log(data);
    }, 
    function(error) {
        console.log(error);
    }
);
```