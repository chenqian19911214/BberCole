package com.bber.company.android.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.databinding.ActivityRegisterBinding;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.Validator;
import com.bber.company.android.viewmodel.UserViewModel;
import com.bber.company.android.widget.MyToast;

/**
 * Created by Vecent on 2017/3/15.
 * 第一个注册界面
 */

public class RegistereActivity extends BaseActivity implements IactionListener<Object> {

    private ActivityRegisterBinding binding;
    private TextView agreement_text, guest_email;
    private EditText user_userName, password, password_again;
    private String username = "";
    private String psd = "", psd_agin;
    //传递过来的城市和邀请码
    private String city, key;
    private UserViewModel userViewModel;
    private RelativeLayout bottom_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        userViewModel = new UserViewModel(this);
        binding.setViewModel(userViewModel);
        userViewModel.setActionListener(this);
        //渲染控件
        initview();
        initdata();
    }

    private void initdata() {
        userViewModel.getQQinfo();
    }

    private void initview() {
        //设置啪啪的免责声明
        city = getIntent().getStringExtra("city");
        key = getIntent().getStringExtra("inviteCode");
        agreement_text = binding.agreementNorespond;
        agreement_text.setText(Html.fromHtml("<font color=#999999>" + "注册代表你已阅读并同意" + "</font>" + "<br/>" +
                "<font color=#ff0066>" + "  <u>  《啪啪免责声明》  </u>" + "</font>"));
        agreement_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistereActivity.this, AgreementActivity.class);
                startActivity(intent);
            }
        });
        bottom_layout = binding.bottomLayout;
        user_userName = binding.userUserName;
        password = binding.userPassword;
        password_again = binding.userPasAgain;
        guest_email = binding.guestEmail;

        bottom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegistereActivity.this, WebViewGuestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isHaveValue", false);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /***
     * 跳转下一步的按钮
     * @param view
     */
    public synchronized void nextOnclick(View view) {
        username = user_userName.getText().toString();
        psd = password.getText().toString();
        psd_agin = password_again.getText().toString();
        if (Tools.isEmpty(username)) {
            MyToast.makeTextAnim(this, R.string.nickname_error, 0, R.style.PopToast).show();
            return;
        }
        if (Validator.isDigit(username)) {
            MyToast.makeTextAnim(this, R.string.not_digit, 0, R.style.PopToast).show();
            return;
        }
        if (psd.isEmpty() || psd.length() < 6) {
            MyToast.makeTextAnim(this, R.string.set_psd_error, 0, R.style.PopToast).show();
            return;
        }
        if (!psd_agin.equals(psd)) {
            MyToast.makeTextAnim(this, R.string.repeat_psd_error, 0, R.style.PopToast).show();
            return;
        }
        if (MyApplication.getContext().isNetworkConnected()) {

            userViewModel.checkNameByName(city, key, username, psd);
        } else {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
        }

    }


    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle("注册");
    }

    @Override
    public void SuccessCallback(Object o) {
        if (o instanceof String) {
            String[] data = ((String) o).split(",");
//            guest_text.setText(Html.fromHtml("<font color=#999999>" + "客服协助QQ:" +
//                    "<font color=#ff0066>" + data[0]+ "</font>"));
            guest_email.setText(Html.fromHtml("<font color=#999999>" + "客服协助邮箱:" +
                    "<font color=#ff0066>" + data[1] + "</font>"));
        }
    }

    @Override
    public void FailCallback(String result) {

    }
}
