package com.bber.company.android.widget;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.SharedPreferencesUtils;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * 微信联机退款
 * Created by zaicent on 2016/6/15.
 */
public class CopyDialog extends Dialog implements View.OnClickListener {
    private EditText mEditText_code;
    private Button mButton_sure;
    private Button mImageView_back;
    private Context mContext;
    private String shareUrl, mInviteCode;
    private TextView chooseText;
    //剪切板管理工具类
    private ClipboardManager mClipboardManager;
    //剪切板Data对象
    private ClipData mClipData;
    //替换数据
    private String data;

    public CopyDialog(Context context, String shareUrl, String mInviteCode) {
        super(context, R.style.dialog_simple);
        mContext = context;
        this.shareUrl = shareUrl;
        this.mInviteCode = mInviteCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //剪切板管理工具类
        mClipboardManager = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        this.setContentView(R.layout.dialog_copy);
        initDialog();
    }

    private void initDialog() {
        String mynickName = (String) SharedPreferencesUtils.get(mContext, Constants.USERNAME, "");
        chooseText = findViewById(R.id.choose_copy);
        chooseText.setText("您的好友“" + mynickName + "”向您推荐:中国第一美模预定平台www.ooxxpapa.com,下载APP,注册填写推荐人“" + mynickName + "”,将获得受邀礼包！");
        mImageView_back = findViewById(R.id.back);
        mImageView_back.setOnClickListener(this);
        mButton_sure = findViewById(R.id.sure);
        mButton_sure.setOnClickListener(this);
    }

    public void sure() {


        data = chooseText.getText().toString();
        //创建一个新的文本clip对象
        mClipData = ClipData.newPlainText("copyText", data);
        //把clip对象放在剪贴板中
        mClipboardManager.setPrimaryClip(mClipData);
        MyToast.makeTextAnim(mContext, "推荐内容已复制，请粘贴发送给好友！", 0, R.style.PopToast).show();
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure:
                sure();
                break;
            case R.id.back:
                dismiss();
                break;
        }
    }

}
