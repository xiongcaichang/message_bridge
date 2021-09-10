package auto.whiterhino.module.message_bridge;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;
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
              .permission(Permission.ACCESS_FINE_LOCATION)
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
      permissions.add(Permission.ACCESS_BACKGROUND_LOCATION);
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
    } else {
      result.notImplemented();
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
