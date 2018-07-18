package com.bber.company.android.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.bber.company.android.R;
import java.io.IOException;

/**
 * Created by carlo.c on 2018/5/17.
 */

public class VideoErrorDialog extends Dialog {

    private VideoErrorDialog loadDialog;
    private TextView textView;

    public VideoErrorDialog(@NonNull Context context, View dialoglayout) throws IOException {
        super(context);

        loadDialog = this;

        this.getContext().setTheme(R.style.myDialog);
        setContentView(dialoglayout);
        //  dialoglayout.setTag(0);
        textView = dialoglayout.findViewById(R.id.button_sure_id);
        textView.setTag(0);
        setParams();
        // 必须放在加载布局后
        loadDialog.show();
    }
    /**
     * 是否正在显示
     *
     * @return
     */
    public boolean isNowShowing() {
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
    public void dismiss(Context context) {
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
        //  this.setCancelable(cancelable);
        this.setCanceledOnTouchOutside(false);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        // Dialog宽度
        //lp.width = display.getWidth();
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
            /*if (!cancelable) {
                return true;
            }*/
        }
        return super.onKeyDown(keyCode, event);
    }

}
