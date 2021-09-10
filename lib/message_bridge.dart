
import 'dart:async';

import 'package:flutter/services.dart';

class MessageBridge {
  static const MethodChannel _channel =
      const MethodChannel('message_bridge');

  static Future<bool> getAwaysLocationPermission() async {
    final bool granted = await _channel.invokeMethod('getAwaysLocationPermission');
    return granted;
  }

  static void setLoginInfo(String uid, String token) {
    _channel.invokeMethod('setLoginInfo', {'uid': uid, 'token': token});
  }

  static void setReportLocation(bool isReport) {
    _channel.invokeMethod('setReportLocation', isReport);
  }

  static void setBaseUrl(String baseUrl) {
    _channel.invokeMethod('setBaseUrl', {'baseUrl': baseUrl});
  }

}
