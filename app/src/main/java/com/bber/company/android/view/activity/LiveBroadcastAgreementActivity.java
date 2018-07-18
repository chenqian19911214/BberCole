package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.widget.MyToast;
import com.facebook.common.internal.ByteStreams;

import java.io.IOException;
import java.io.InputStream;

/**
 * 成为主播协议认证
 */
public class LiveBroadcastAgreementActivity extends BaseAppCompatActivity implements View.OnClickListener {

    /*是否同意条款*/
    private static boolean isChecked = true;
    private Button agreement_next_id;
    private CheckBox agreement_checkbox_id;
    private TextView agreement_text_id;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_live_broadcast_agreement;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText("主播认证");
        // setContentView(R.layout.activity_live_broadcast_agreement);

        initView();
    }

    private void initView() {
        agreement_next_id = findViewById(R.id.agreement_next_id);
        agreement_checkbox_id = findViewById(R.id.agreement_checkbox_id);
        agreement_text_id = findViewById(R.id.agreement_text_id);
        agreement_text_id.setMovementMethod(new ScrollingMovementMethod());
        try {
            InputStream stream = getAssets().open("mkLiveAgreeProtocol.html");

            String str = new String(ByteStreams.toByteArray(stream));
            agreement_text_id.setText(Html.fromHtml(str));
        } catch (IOException e) {
            e.printStackTrace();
        }
        agreement_next_id.setOnClickListener(this);
        agreement_checkbox_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                LiveBroadcastAgreementActivity.isChecked = isChecked;

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agreement_next_id://下一步

                if (!isChecked) {

                    MyToast.makeTextAnim(getApplicationContext(), "请先同意以上条款", 0, R.style.PopToast).show();

                } else {

                    startActivity(new Intent(getApplicationContext(), LiveBroadcastAuthenticationPhotoActivity.class));
                    finish();

                }
                break;
        }
    }
}
