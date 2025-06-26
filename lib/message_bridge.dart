
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


  static void startNavigation(NavigationParams params) {
    _channel.invokeMethod('startNavigation', params.toMap());
  }

}

// navigation_params.dart

class NavigationParams {
  final LocationPoint start;
  final List<LocationPoint> waypoints;
  final LocationPoint end;
  final NavigationType naviType;
  final NavigationTheme theme;

  const NavigationParams({
    required this.start,
    this.waypoints = const [],
    required this.end,
    this.naviType = NavigationType.driver,
    this.theme = NavigationTheme.defaultTheme,
  });

  // 工厂方法：从 Map 转换（带数据校验）
  factory NavigationParams.fromMap(Map<String, dynamic> map) {
    // 必要参数校验
    if (!map.containsKey('start') || !map.containsKey('end')) {
      throw ArgumentError('必须包含 start 和 end 参数');
    }

    return NavigationParams(
      start: LocationPoint.fromMap(map['start'] as Map<String, dynamic>),
      waypoints: (map['waypoints'] as List<dynamic>?)
          ?.map((e) => LocationPoint.fromMap(e as Map<String, dynamic>))
          .toList() ?? [],
      end: LocationPoint.fromMap(map['end'] as Map<String, dynamic>),
      naviType: _parseNavigationType(map['naviType']),
      theme: _parseNavigationTheme(map['theme']),
    );
  }

  // 转换为平台通道需要的 Map 格式
  Map<String, dynamic> toMap() => {
    'start': start.toMap(),
    'waypoints': waypoints.map((p) => p.toMap()).toList(),
    'end': end.toMap(),
    'naviType': naviType.name,
    'theme': theme.name,
  };

  // 枚举类型解析
  static NavigationType _parseNavigationType(dynamic type) {
    final String name = (type as String?)?.toLowerCase() ?? 'driver';
    return NavigationType.values.firstWhere(
          (e) => e.name == name,
      orElse: () => NavigationType.driver,
    );
  }

  static NavigationTheme _parseNavigationTheme(dynamic theme) {
    final String name = (theme as String?)?.toLowerCase() ?? 'default';
    return NavigationTheme.values.firstWhere(
          (e) => e.name == name,
      orElse: () => NavigationTheme.defaultTheme,
    );
  }
}

// 位置点对象
class LocationPoint {
  final String name;
  String? id;
  final double latitude;
  final double longitude;

  LocationPoint({
    required this.name,
    this.id,
    required this.latitude,
    required this.longitude,
  });

  factory LocationPoint.fromMap(Map<String, dynamic> map) {
    // 坐标范围校验
    final lat = map['lat'] as double;
    final lng = map['lng'] as double;
    if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
      throw ArgumentError('非法的坐标值: ($lat, $lng)');
    }

    return LocationPoint(
      name: map['name'] as String,
      id: map['id'] as String,
      latitude: lat,
      longitude: lng,
    );
  }

  Map<String, dynamic> toMap() => {
    'name': name,
    'id': id,
    'lat': latitude,
    'lng': longitude,
  };
}

// 枚举定义
enum NavigationType { driver, walk, ride, truck }
enum NavigationTheme { defaultTheme, white, dark }
