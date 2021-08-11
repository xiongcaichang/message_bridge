
import 'dart:async';

import 'package:flutter/services.dart';

class MessageBridge {
  static const MethodChannel _channel =
      const MethodChannel('message_bridge');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
