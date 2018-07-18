package com.bber.company.android.view.customcontrolview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.bber.company.android.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by carlo.c on 2018/3/26.
 * 自定义 Dialog 动画
 * <p>
 * <p>
 * 使用方法 DialogView.show() 调动画
 */

public class DialogView extends Dialog {


    public static GifDrawable gifDrawable;
    private static DialogView loadDialog;
    private boolean cancelable;
    private GifImageView gifImageView;

    public DialogView(@NonNull Context context, boolean cancelable) throws IOException {
        super(context);
        this.cancelable = cancelable;
        this.getContext().setTheme(R.style.myDialog);
        setContentView(R.layout.dialog_layout);
        setParams();
        gifImageView = findViewById(R.id.gifImageview);
        gifDrawable = new GifDrawable(context.getResources(), R.drawable.loadinggif);
        gifImageView.setImageDrawable(gifDrawable);
        gifDrawable.start();
        // 必须放在加载布局后


    }

    public static void show(Context context) {
        show(context, true);
    }

    /**
     * @param context
     * @param cancelable 是否可以取消
     */
    public static void show(Context context, boolean cancelable) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }
        if (loadDialog != null && loadDialog.isShowing()) {
            return;
        }
        try {
            loadDialog = new DialogView(context, cancelable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //gifDrawable.start();
        loadDialog.show();
    }

    /**
     * 是否正在显示
     *
     * @return
     */
    public static boolean isNowShowing() {
        if (loadDialog == null) {
            return false;
        }
        return loadDialog.isShowing();
    }

    /**
     * 取消弹窗
     *
     * @param context
     */
    public static void dismiss(Context context) {
        try {
            if (context instanceof Activity) {
                if (((Activity) context).isFinishing()) {
                    loadDialog = null;
                    return;
                }
            }
            if (loadDialog != null && loadDialog.isShowing()) {
                Context loadContext = loadDialog.getContext();
                if (loadContext instanceof Activity) {
                    if (((Activity) loadContext).isFinishing()) {
                        loadDialog = null;
                        return;
                    }
                }
                loadDialog.dismiss();
                loadDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadDialog = null;
        }
    }

    /**
     * 设置参数
     */
    private void setParams() {
        this.setCancelable(cancelable);
        this.setCanceledOnTouchOutside(false);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        // Dialog宽度
        //        lp.width = display.getWidth();
        Window window = getWindow();
        // lp.alpha = 0.3f;
        window.setAttributes(lp);
        //        window.getDecorView().getBackground().setAlpha(0);
    }

    /**
     * 对返回键进行拦截
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!cancelable) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
