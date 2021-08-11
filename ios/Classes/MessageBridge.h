//
//  MessageBridge.h
//  Pods
//
//  Created by mac on 2021/7/29.
//

#import<Foundation/Foundation.h>

@interface MessageBridge : NSObject
+(instancetype) shareInstance ;
@property(nonatomic,copy) NSString *uid;
@property(nonatomic,copy) NSString *token;
@property(nonatomic,copy) NSString *baseUrl;
@property(nonatomic,assign) BOOL isReportLocation;
@end
