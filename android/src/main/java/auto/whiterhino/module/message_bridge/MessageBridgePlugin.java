package auto.whiterhino.module.message_bridge;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviTheme;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.NaviSetting;
import com.autonavi.amap.navicore.AMapNaviCoreManager;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** MessageBridgePlugin */
public class MessageBridgePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

  private static final String TAG = "NavigationHandler";
  private MethodChannel channel;
  private Activity mActivity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "message_bridge");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(MethodCall call, final Result result) {
    if (call.method.equals("getAwaysLocationPermission")) {

      XXPermissions.with(mActivity)
              .permission(Permission.ACCESS_COARSE_LOCATION)
              .permission(Permission.ACCESS_BACKGROUND_LOCATION)
              .request(new OnPermissionCallback() {

                @Override
                public void onGranted(List<String> permissions , boolean all) {
                  if (all) {
                    Log.e("-----------", "1");
                      try{
                        result.success(true);
                      } catch (IllegalStateException e) {

                      }
                  }
                }

                @Override
                public void onDenied(List<String> permissions, boolean never) {
                  if (never) {
                    try{
                      result.success(false);
                    } catch (IllegalStateException e) {

                    }
                    XXPermissions.startPermissionActivity(mActivity, permissions);
                  }
                }
              });

    }if (call.method.equals("jumpPermissionSetting")) {
      List<String> permissions = new ArrayList<>();
      permissions.add(Permission.ACCESS_FINE_LOCATION);
//      permissions.add(Permission.ACCESS_BACKGROUND_LOCATION);
      XXPermissions.startPermissionActivity(mActivity,permissions);
    }else if(call.method.equals("setBaseUrl")) {
      if (call.hasArgument("baseUrl")) {
        MessageBridge.getInstance().setBaseUrl((String) call.argument("baseUrl"));
      }
    }else if(call.method.equals("setReportLocation")) {
        MessageBridge.getInstance().setReportLocation((boolean) call.arguments);
    } else if(call.method.equals("setLoginInfo")) {
      if (call.hasArgument("uid") && call.hasArgument("token")) {
        MessageBridge.getInstance().setUserId((String) call.argument("uid"));
        MessageBridge.getInstance().setToken((String) call.argument("token"));
      }
    } else if (call.method.equals("startNavigation")) {
      new Handler(Looper.getMainLooper()).post(() -> {
        try {
          // 参数容器校验
          HashMap<String, Object> paramsMap = (HashMap<String, Object>) call.arguments;
          if (paramsMap == null) {
            result.error("INVALID_PARAM", "参数容器为空", null);
            return;
          }

          // 关键参数存在性校验
          if (!paramsMap.containsKey("start") || !paramsMap.containsKey("end")) {
            result.error("MISSING_PARAM", "必须包含start和end参数", null);
            return;
          }

          // 解析核心参数
          Poi start = parsePoiFromMap((HashMap<String, Object>) paramsMap.get("start"));
          Poi end = parsePoiFromMap((HashMap<String, Object>) paramsMap.get("end"));

          // 解析途径点（最多支持5个）
          List<Poi> waypoints = new ArrayList<>();
          if (paramsMap.containsKey("waypoints")) {
            List<HashMap<String, Object>> waypointMaps =
                    (List<HashMap<String, Object>>) paramsMap.get("waypoints");

            if (waypointMaps.size() > 16) {
              result.error("TOO_MANY_WAYPOINTS", "最多支持5个途径点", null);
              return;
            }

            for (HashMap<String, Object> wpMap : waypointMaps) {
              waypoints.add(parsePoiFromMap(wpMap));
            }
          }
          NaviSetting.updatePrivacyShow(mActivity, true, true);
          NaviSetting.updatePrivacyAgree(mActivity, true);

          // 构建导航参数
          AmapNaviParams naviParams = new AmapNaviParams(
                  start,
                  waypoints,
                  end,
                  getNaviType(paramsMap),
                  AmapPageType.NAVI
          );

          // 应用可选配置
          applyTheme(naviParams, paramsMap);

          // 执行导航（已在主线程，无需额外处理）
          AmapNaviPage.getInstance().showRouteActivity(
                  mActivity,
                  naviParams,
                  null,
                  WRRouteActivity.class
          );

          // 返回成功结果
          result.success(true);
        } catch (IllegalArgumentException e) {
          Log.e(TAG, "参数解析错误", e);
          result.error("PARSE_ERROR", e.getMessage(), null);
        } catch (Exception e) {
          Log.e(TAG, "导航启动异常", e);
          result.error("NAVIGATION_FAILED", "导航系统错误: " + e.getMessage(), null);
        }
      });
    }
    else {
      result.notImplemented();
    }
  }

  // 解析POI对象
// 增强的POI解析方法
  private Poi parsePoiFromMap(HashMap<String, Object> map) throws IllegalArgumentException {
    // 必要字段校验（移除了id检查）
    if (!map.containsKey("name")
            || !map.containsKey("lat")
            || !map.containsKey("lng")) {
      throw new IllegalArgumentException("POI参数缺失必要字段[name/lat/lng]");
    }

    try {
      String name = Objects.requireNonNull((String) map.get("name"));

      // 处理可选id字段
      String id = "";
      if (map.containsKey("id")) {
        Object idObj = map.get("id");
        if (idObj != null) {
          id = idObj.toString(); // 支持数字类型自动转换
        }
      }

      // 处理数值类型差异
      Number latNumber = (Number) map.get("lat");
      Number lngNumber = (Number) map.get("lng");
      double lat = latNumber.doubleValue();
      double lng = lngNumber.doubleValue();

      // 坐标范围校验
      if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
        throw new IllegalArgumentException("非法坐标值 (" + lat + ", " + lng + ")");
      }

      return new Poi(name, new LatLng(lat, lng), id);
    } catch (NullPointerException e) {
      throw new IllegalArgumentException("POI字段存在空值", e);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException("POI字段类型错误", e);
    }
  }


  // 增强的导航类型解析
  private AmapNaviType getNaviType(HashMap<String, Object> params) {
    if (!params.containsKey("naviType")) {
      return AmapNaviType.DRIVER;
    }

    String type = ((String) params.get("naviType")).toLowerCase(Locale.US);
    switch (type) {
      case "walk": return AmapNaviType.WALK;
      case "ride": return AmapNaviType.RIDE;
      case "driver": return AmapNaviType.DRIVER;
      case "motorcycle": return AmapNaviType.MOTORCYCLE;
      default:
        Log.w(TAG, "未知导航类型: " + type + "，使用默认驾车导航");
        return AmapNaviType.DRIVER;
    }
  }

  // 增强的主题解析方法
  private void applyTheme(AmapNaviParams params, HashMap<String, Object> paramsMap) {
    if (!paramsMap.containsKey("theme")) return;

    String theme = ((String) paramsMap.get("theme")).toLowerCase(Locale.US);
    switch (theme) {
      case "white":
        params.setTheme(AmapNaviTheme.WHITE);
        break;
      case "blue":
        params.setTheme(AmapNaviTheme.BLUE);
        break;
      case "dark":
      case "black":
        params.setTheme(AmapNaviTheme.BLACK);
        break;
      default:
        Log.i(TAG, "使用默认导航主题");
    }
  }


  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    mActivity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }
}
