package com.kepat.cordova.file.details;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import android.net.Uri;
import android.util.Log;
import android.content.Context;

import java.io.InputStream;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class FileDetails extends CordovaPlugin {

    private static final String ACTION_FETCH_DETAILS = "fetch";

    public static final String TAG = "FD_CORDOVA_PLUGIN_" + FileDetails.class.getSimpleName();

    private CallbackContext callbackContext; // Cordova plugin callback context

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     *
     * @return                  A PluginResult object with a status and message.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // Store the callback context within this class
        this.callbackContext = callbackContext;

        // Fetch the details of the file
        if (action.equals(this.ACTION_FETCH_DETAILS)) {
            // Get the arguments required 
            String filePath = args.getString(0);

            // Fetch the details
            this.fetch(filePath);
            return true;
        }

        return false;
    }

    /**
     * Get image from gallery
     * @param filePath {string} - File path of the file.
     */
    private void fetch(String filePath) {

        // Prepare the result
        JSONObject result = new JSONObject();

        // Retrieve the Uri from the filePath
        Uri uri =  Uri.parse(filePath);

        // Prepare the input stream and Helper
        InputStream inputStream;
        Helper exif = new Helper();
        
        try {
            // Get the input stream and context
            inputStream = this.cordova.getActivity().getApplicationContext().getContentResolver().openInputStream(uri);
            Context context = this.cordova.getActivity().getApplicationContext();

            // Retrieve the file details
            exif.createInFile(inputStream);
            exif.readExifData(context, uri);
            
            result = exif.generateJSON();
        } catch (JSONException | IOException e) {
            Log.d(TAG, e.getMessage());
            callbackContext.error(e.getMessage());
        }

        // Success callback
        callbackContext.success(result.toString());
        
    }

}