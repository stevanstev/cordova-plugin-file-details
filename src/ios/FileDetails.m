#import "FileDetails.h"
#import "Foundation/Foundation.h"

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
        
        NSURLRequest* fileUrlRequest = [[NSURLRequest alloc] initWithURL:url cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:.1];
        dispatch_group_t group = dispatch_group_create();
        dispatch_group_enter(group);

        // Prepare the dictionary to be returned
        NSMutableDictionary *information = [[NSMutableDictionary alloc] init];
        __block NSString* MIMEType;
        NSURLSession *session = [NSURLSession sharedSession];
        NSURLSessionDataTask *uploadTask = [session dataTaskWithRequest:fileUrlRequest completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error){
            if(!error){
               MIMEType = [response MIMEType];
            }
            dispatch_group_leave(group);
           }
        ];
        [uploadTask resume];
        dispatch_group_wait(group, DISPATCH_TIME_FOREVER);
        if(MIMEType){
            information[@"mimeType"] = MIMEType;
        }
        // Check is it IMAGE file or not
        UIImage *image = [UIImage imageWithContentsOfFile:filePath];
        if (image != nil) {
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
        }
        
        // Parse the retrieved data
        NSError* error = nil;
        NSData* jsonData = [NSJSONSerialization dataWithJSONObject:information options:0 error:&error];
        if (error != nil){
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString: @"QWe"];
        } else {
            NSString* jsonString = [[NSString alloc] initWithData: jsonData encoding:NSUTF8StringEncoding];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: jsonString];
        }
        
        // Callback
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
    }];
    
}

@end