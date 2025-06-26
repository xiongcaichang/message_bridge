package auto.whiterhino.module.message_bridge;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.R;
import com.amap.api.navi.AmapRouteActivity;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.gyf.immersionbar.OnKeyboardListener;


public class WRRouteActivity extends AmapRouteActivity {
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            setupImmersionBar();
        }, 1000);
    }

    private void setupImmersionBar() {
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)     // 状态栏背景色
                .navigationBarColor(R.color.white) // 导航栏背景色
                .statusBarDarkFont(true, 0.2f)     // 深色字体 + 透明度参数
                .navigationBarDarkIcon(true)       // 深色导航栏图标
                .fitsSystemWindows(true)           // 防止布局侵入系统栏
                .fullScreen(false)                 // 关键！禁用全屏模式
                .hideBar(BarHide.FLAG_SHOW_BAR)    // 强制显示系统栏
                .autoDarkModeEnable(true)         // 自动适应黑暗模式
                .init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupImmersionBar();
    }


    @Override
    protected void onPause() {
        super.onPause();
        setupImmersionBar();
    }
}
