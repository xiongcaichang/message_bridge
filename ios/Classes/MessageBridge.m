//
//  MessageBridge.m
//  message_bridge
//
//  Created by mac on 2021/7/29.
//
#import "MessageBridge.h"
#import <Foundation/Foundation.h>


@implementation MessageBridge : NSObject


static MessageBridge* _instance = nil;
 
+(instancetype) shareInstance
{
    static dispatch_once_t onceToken ;
    dispatch_once(&onceToken, ^{
        _instance = [[super allocWithZone:NULL] init] ;
    }) ;
    
    return _instance ;
}
 
+(id) allocWithZone:(struct _NSZone *)zone
{
    return [MessageBridge shareInstance] ;
}
 
-(id) copyWithZone:(struct _NSZone *)zone
{
    return [MessageBridge shareInstance] ;
}



@end
