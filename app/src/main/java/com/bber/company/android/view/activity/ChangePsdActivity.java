package com.bber.company.android.view.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

public class ChangePsdActivity extends BaseAppCompatActivity implements View.OnClickListener {


    private EditText edit_old_psd, edit_new_psd, edit_repeat_psd;
    private Button btn;
    TextWatcher oldWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String new_psd = edit_new_psd.getText() + "";
            String repeat_psd = edit_repeat_psd.getText() + "";
            if (charSequence.length() == 0 || new_psd.length() == 0 || repeat_psd.length() == 0) {
                btn.setEnabled(false);
            } else {
                btn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private LinearLayout ll_old_psd;
    private boolean forgetPsd = false;
    TextWatcher newWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String old_psd = edit_old_psd.getText() + "";
            String repeat_psd = edit_repeat_psd.getText() + "";
            if (charSequence.length() == 0 || (old_psd.length() == 0 && forgetPsd == false) || repeat_psd.length() == 0) {
                btn.setEnabled(false);
            } else {
                btn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    TextWatcher repeatWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String old_psd = edit_old_psd.getText() + "";
            String new_psd = edit_new_psd.getText() + "";
            if (charSequence.length() == 0 || (old_psd.length() == 0 && forgetPsd == false) || new_psd.length() == 0) {
                btn.setEnabled(false);
            } else {
                btn.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private String mobliephone_num;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_change_psd;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.change_psd);
        edit_old_psd = findViewById(R.id.edit_old_psd);
        ll_old_psd = findViewById(R.id.ll_old_psd);
        edit_new_psd = findViewById(R.id.edit_new_psd);
        edit_repeat_psd = findViewById(R.id.edit_repeat_psd);
        btn = findViewById(R.id.btn);

    }

    private void setListeners() {
        btn.setOnClickListener(this);
        edit_old_psd.addTextChangedListener(oldWatcher);
        edit_new_psd.addTextChangedListener(newWatcher);
        edit_repeat_psd.addTextChangedListener(repeatWatcher);
    }

    private void initData() {
        forgetPsd = getIntent().getBooleanExtra("forgetPsd", false);
        mobliephone_num = getIntent().getStringExtra("mobliephone");
        if (forgetPsd == true) {
            ll_old_psd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn:
                if (forgetPsd == false) {
                    changeLoginPsd();
                } else {
                    changePsdByPhone();
                }

        }
    }

    private void changeLoginPsd() {

        String old_psd = edit_old_psd.getText() + "";
        if (old_psd.isEmpty() || old_psd.length() < 6) {
            MyToast.makeTextAnim(this, R.string.old_psd_type_error, 0, R.style.PopToast).show();
            return;
        }
        String new_psd = edit_new_psd.getText() + "";
        if (new_psd.isEmpty() || new_psd.length() < 6) {
            MyToast.makeTextAnim(this, R.string.new_psd_type_error, 0, R.style.PopToast).show();
            return;
        }
        String repeat_psd = edit_repeat_psd.getText() + "";
        if (repeat_psd.isEmpty() || repeat_psd.length() < 6 || !repeat_psd.equals(new_psd)) {
            MyToast.makeTextAnim(this, R.string.repeat_psd_error, 0, R.style.PopToast).show();
            return;
        }
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        DialogTool.createProgressDialog(this, true);
        RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        params.put("oldPassword", old_psd);
        params.put("newPassword", new_psd);
        HttpUtil.post(Constants.getInstance().updatePwd, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "updatePwd onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(ChangePsdActivity.this, jsonObject, null)) {
                    return;
                }
                MyToast.makeTextAnim(ChangePsdActivity.this, R.string.change_sus, 0, R.style.PopToast).show();
                finish();

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "updatePwd onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(ChangePsdActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(ChangePsdActivity.this);
            }
        });
    }

    private void changePsdByPhone() {
        String new_psd = edit_new_psd.getText() + "";
        if (new_psd.isEmpty() || new_psd.length() < 6) {
            MyToast.makeTextAnim(this, R.string.new_psd_type_error, 0, R.style.PopToast).show();
            return;
        }
        String repeat_psd = edit_repeat_psd.getText() + "";
        if (repeat_psd.isEmpty() || repeat_psd.length() < 6 || !repeat_psd.equals(new_psd)) {
            MyToast.makeTextAnim(this, R.string.repeat_psd_error, 0, R.style.PopToast).show();
            return;
        }
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        DialogTool.createProgressDialog(this, true);
        RequestParams params = new RequestParams();
        params.put("newPassword", new_psd);
        params.put("phone", mobliephone_num);
        HttpUtil.post(Constants.getInstance().updateBuyerPwdByPhone, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                Log.e("eeeeeeeeeeee", "updatePwd onSuccess--jsonObject:" + jsonObject);
                if (Tools.jsonResult(ChangePsdActivity.this, jsonObject, null)) {
                    return;
                }
                MyToast.makeTextAnim(ChangePsdActivity.this, R.string.change_sus, 0, R.style.PopToast).show();
                finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "updatePwd onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(ChangePsdActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(ChangePsdActivity.this);
            }
        });
    }

}
