#import <Cordova/CDVPlugin.h>

@interface FileDetails : CDVPlugin <UIImagePickerControllerDelegate>
{}

@property (strong) NSMutableDictionary *metadata;

- (void)fetch:(CDVInvokedUrlCommand*)command;

@end