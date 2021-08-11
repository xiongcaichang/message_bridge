package auto.whiterhino.module.message_bridge;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** MessageBridgePlugin */
public class MessageBridgePlugin implements FlutterPlugin, MethodCallHandler {

  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "message_bridge");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
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
}
