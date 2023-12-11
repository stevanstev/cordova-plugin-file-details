/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.kepat.cordova.file.details;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.InputStream;
import java.io.IOException;

import androidx.exifinterface.media.ExifInterface;

import org.json.JSONObject;
import org.json.JSONException;

public class FileHelper {
    
     // Original Exif properties
     private String aperture = null;
     private String datetime = null;
     private String exposureTime = null;
     private String flash = null;
     private String focalLength = null;
     private String gpsAltitude = null;
     private String gpsAltitudeRef = null;
     private String gpsDateStamp = null;
     private String gpsLatitude = null;
     private String gpsLatitudeRef = null;
     private String gpsLongitude = null;
     private String gpsLongitudeRef = null;
     private String gpsProcessingMethod = null;
     private String gpsTimestamp = null;
     private String iso = null;
     private String make = null;
     private String model = null;
     private String orientation = null;
     private String whiteBalance = null;

    // Not depend on Exif plugin
    private String mimeType = null;
    private String fileName = null;

    /**
     * Retrieve the file details
     * 
     * @param context
     * @param uri
     */
    public void readDetails(Context context, Uri uri) throws IOException {
        // Get the input stream
        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        // EXIF data
        readExifData(inputStream);

        // A MIME type for the content, or null if the URL is invalid or the type is unknown
        this.mimeType = context.getContentResolver().getType(uri);

        // Retrieve the file name
        this.fileName = readFileName(context, uri);
    }
    
    /**
     * Generate the json object that contains the information
     * 
     * @return JSONObject
     * @throws JSONException
     */
    public JSONObject generateJSON() throws JSONException, IOException{

        // Prepare the result
        JSONObject result = new JSONObject();
        
        // Store the details into the json
        result.put("datetime", this.datetime);

        // Image Stuff
        result.put("aperture", this.aperture);
        result.put("exposureTime", this.exposureTime);
        result.put("flash", this.flash);
        result.put("whiteBalance", this.whiteBalance);
        result.put("orientation", this.orientation);

        // Hide the altitudes first
        // result.put("altitude", this.gpsAltitude);
        // result.put("altitudeRef", this.gpsAltitudeRef);

        // GPS Stuff
        result.put("latitude", convertDMSToDecimal(this.gpsLatitude, this.gpsLatitudeRef));
        result.put("latitudeRef", this.gpsLatitudeRef);
        result.put("longitude", convertDMSToDecimal(this.gpsLongitude, this.gpsLongitudeRef));
        result.put("longitudeRef", this.gpsLongitudeRef);

        // MIME Type
        result.put("mimeType",this.mimeType);

        // File Name
        result.put("fileName", this.fileName);

        return result;

    }

    /**
     * Reads all the EXIF data from the input stream.
     */
    private void readExifData(InputStream inputStream) throws IOException {
        // Read the exif
        ExifInterface inFile = new ExifInterface(inputStream);

        this.aperture = inFile.getAttribute(ExifInterface.TAG_F_NUMBER);
        this.datetime = inFile.getAttribute(ExifInterface.TAG_DATETIME);
        this.exposureTime = inFile.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        this.flash = inFile.getAttribute(ExifInterface.TAG_FLASH);
        this.focalLength = inFile.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        this.gpsAltitude = inFile.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);
        this.gpsAltitudeRef = inFile.getAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF);
        this.gpsDateStamp = inFile.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
        this.gpsLatitude = inFile.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        this.gpsLatitudeRef = inFile.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        this.gpsLongitude = inFile.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        this.gpsLongitudeRef = inFile.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        this.gpsProcessingMethod = inFile.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
        this.gpsTimestamp = inFile.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
        this.iso = inFile.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);
        this.make = inFile.getAttribute(ExifInterface.TAG_MAKE);
        this.model = inFile.getAttribute(ExifInterface.TAG_MODEL);
        this.orientation = inFile.getAttribute(ExifInterface.TAG_ORIENTATION);
        this.whiteBalance = inFile.getAttribute(ExifInterface.TAG_WHITE_BALANCE);
    }

    /**
     * Get the file name
     */
    public static String readFileName(Context context, Uri uri) {
        String displayName = null;

        // Try to query the file name using the ContentResolver
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // If the ContentResolver query fails, try to extract the file name from the Uri itself
        if (displayName == null) {
            String uriString = uri.toString();
            int slashIndex = uriString.lastIndexOf('/');
            if (slashIndex != -1) {
                displayName = uriString.substring(slashIndex + 1);
            }
        }

        return displayName;
    }

    /**
     * Convert latitude or longitude 
     * from DMS format to decimal format.
     * 
     * @return String
     */
    private String convertDMSToDecimal(String dmsString, String indicator){
        // Make sure it is not null
        if (dmsString == null) {
            return null;
        }

        String[] dms = dmsString.split(",");
        
        int days = Integer.parseInt(dms[0].split("/")[0]);
        int minutes = Integer.parseInt(dms[1].split("/")[0]);

        double secondsNumerator = Double.parseDouble(dms[2].split("/")[0]);
        double secondsDenominator = Double.parseDouble(dms[2].split("/")[1]);
        
        String value = String.valueOf(days + (minutes / 60.0) + ((secondsNumerator/secondsDenominator) / 3600.0));

        // Check the positive or negative value
        if (indicator.equals("S") || indicator.equals("W"))  {
            return "-" + value;
        }
            
        return value;
    }

}
