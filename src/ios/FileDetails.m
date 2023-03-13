#import "FileDetails.h"

@implementation FileDetails

- (void)fetch:(CDVInvokedUrlCommand*)command
{
    [self.commandDelegate runInBackground:^{
        
        // Result holder
        CDVPluginResult* pluginResult = nil;
        
        // Retrieve the file path passed
        NSString* filePath = [command argumentAtIndex:0 withDefault:nil];
        
        // Remove unecessary file:// in the file path
        filePath = [filePath stringByReplacingOccurrencesOfString:@"file://" withString:@""];
        
        // Retrieve the metadata
        NSURL *url = [NSURL fileURLWithPath:filePath];
        CGImageSourceRef source = CGImageSourceCreateWithURL( (CFURLRef) url, NULL);
        CFDictionaryRef dictRef = CGImageSourceCopyPropertiesAtIndex(source,0,NULL);
        NSDictionary* metadata = (__bridge NSDictionary *)dictRef;
        
        // Prepare the dictionary to be returned
        NSMutableDictionary *information = [[NSMutableDictionary alloc] init];
        
        // Exif
        if (metadata[@"{Exif}"] != nil) {
            if (metadata[@"{Exif}"][@"DateTimeOriginal"] != nil) {
                information[@"datetime"] = metadata[@"{Exif}"][@"DateTimeOriginal"];
            }
            if (metadata[@"{Exif}"][@"ApertureValue"] != nil) {
                information[@"aperture"] = metadata[@"{Exif}"][@"ApertureValue"];
            }
            if (metadata[@"{Exif}"][@"ExposureTime"] != nil) {
                information[@"exposureTime"] = metadata[@"{Exif}"][@"ExposureTime"];
            }
            if (metadata[@"{Exif}"][@"Flash"] != nil) {
                information[@"flash"] = metadata[@"{Exif}"][@"Flash"];
            }
            if (metadata[@"{Exif}"][@"WhiteBalance"] != nil) {
                information[@"whiteBalance"] = metadata[@"{Exif}"][@"WhiteBalance"];
            }
        }
        
        // TIFF
        if (metadata[@"{TIFF}"] != nil) {
            if (metadata[@"{TIFF}"][@"Orientation"] != nil) {
                information[@"orientation"] = metadata[@"{TIFF}"][@"Orientation"];
            }
        }

        // Hide the altitudes first
        // if (metadata[@"{GPS}"][@"Altitude"] != nil) {
        //         information[@"altitude"] = metadata[@"{GPS}"][@"Altitude"];
        // }
        // if (metadata[@"{GPS}"][@"AltitudeRef"] != nil) {
        //     information[@"altitudeRef"] = metadata[@"{GPS}"][@"AltitudeRef"];
        // }
        
        // GPS
        if (metadata[@"{GPS}"] != nil) {
            if (metadata[@"{GPS}"][@"Latitude"] != nil) {
                information[@"latitude"] = metadata[@"{GPS}"][@"Latitude"];
            }
            if (metadata[@"{GPS}"][@"LatitudeRef"] != nil) {
                information[@"latitudeRef"] = metadata[@"{GPS}"][@"LatitudeRef"];
            }
            if (metadata[@"{GPS}"][@"Longitude"] != nil) {
                information[@"longitude"] = metadata[@"{GPS}"][@"Longitude"];
            }
            if (metadata[@"{GPS}"][@"LongitudeRef"] != nil) {
                information[@"longitudeRef"] = metadata[@"{GPS}"][@"LongitudeRef"];
            }
        }
        
        // Release to avoid memory leakage
        CFRelease(dictRef);
        
        // Parse the retrieved data
        NSError* error = nil;
        NSData* jsonData = [NSJSONSerialization dataWithJSONObject:information options:0 error:&error];
        if (error != nil){
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: [error localizedDescription]];
        } else {
            NSString* jsonString = [[NSString alloc] initWithData: jsonData encoding:NSUTF8StringEncoding];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: jsonString];
        }
        
        // Callback
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
    }];
    
}

@end