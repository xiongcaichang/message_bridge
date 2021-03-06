#import "MessageBridgePlugin.h"
#import "MessageBridge.h"
@implementation MessageBridgePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"message_bridge"
            binaryMessenger:[registrar messenger]];
  MessageBridgePlugin* instance = [[MessageBridgePlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  }else if([@"setReportLocation" isEqual:call.method]){
      [MessageBridge shareInstance].isReportLocation = [call.arguments boolValue];
  }else if([@"setBaseUrl" isEqual:call.method]){
      [MessageBridge shareInstance].baseUrl =[call.arguments objectForKey:@"baseUrl"];
  }else if([@"setLoginInfo" isEqual:call.method]){
      if ([call.arguments objectForKey:@"uid"] && [call.arguments objectForKey:@"token"]) {
          [MessageBridge shareInstance].uid =[call.arguments objectForKey:@"uid"];
          [MessageBridge shareInstance].token =[call.arguments objectForKey:@"token"];
          
      }
    } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
