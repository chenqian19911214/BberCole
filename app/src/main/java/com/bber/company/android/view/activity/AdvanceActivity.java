package com.bber.company.android.view.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class AdvanceActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private Button btn_over;
    private TextView text_balance, bank_name, card_num;
    private EditText edit_money;
    private int balance;
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() == 0) {
                btn_over.setEnabled(false);
            } else {
                btn_over.setEnabled(true);
                int money = Tools.StringToInt(charSequence + "");
                if (money > balance) {
                    edit_money.setText(balance + "");
                }
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_advance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        title.setText(R.string.advance);
        edit_money = findViewById(R.id.edit_money);
        text_balance = findViewById(R.id.text_balance);
        btn_over = findViewById(R.id.btn_over);
        bank_name = findViewById(R.id.bank_name);
        card_num = findViewById(R.id.card_num);
    }

    private void setListeners() {
        btn_over.setOnClickListener(this);
        edit_money.addTextChangedListener(watcher);
    }

    private void initData() {
        balance = getIntent().getIntExtra("balance", 0);
        text_balance.setText("￥" + balance);
        getCard();
    }

    private void getCard() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().getBuyerBankCard, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(AdvanceActivity.this, jsonObject, null)) {
                    LogUtils.e("返回的数据为空");
                    return;
                }
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("dataCollection");
                    String codeStr = jsonObject1.getString("bankId");
                    if (Tools.isEmpty(codeStr)) {
                        MyToast.makeTextAnim(AdvanceActivity.this, R.string.no_bind_card, 0, R.style.PopToast).show();
                        finish();
                    } else {
                        int code = Tools.StringToInt(codeStr);
                        String cardNum = jsonObject1.getString("bankCard");
                        String item = getResources().getStringArray(R.array.card)[code];
                        bank_name.setText(item);
                        cardNum = "尾号" + cardNum.substring(cardNum.length() - 4, cardNum.length());
                        card_num.setText(cardNum);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("eeeeeeeeeeee", "getBuyerBankCard JSONException:");
                }


            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getBuyerBankCard onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(AdvanceActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 鐎瑰本鍨氶崥搴ょ殶閻㈩煉绱濇径杈Е閿涘本鍨氶崝鐕傜礉闁?燁洣鐠嬪啰鏁?
            }
        });
    }

    private void advance() {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        DialogTool.createProgressDialog(this, true);
        String userID = SharedPreferencesUtils.get(this, Constants.USERID, "") + "";
        int money = Tools.StringToInt(edit_money.getText() + "");
        RequestParams params = new RequestParams();
        String head = new JsonUtil(this).httpHeadToJson(this);
        params.put("head", head);
        params.put("flag", "c");
        params.put("organId", userID);
        params.put("money", money);
        final String str = userID + "" + money + "c" + "seller";
        String key = Tools.md5(str);
        params.put("key", key);
        HttpUtil.post(Constants.getInstance().withdrawalRequest, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(AdvanceActivity.this, jsonObject, null)) {
                    return;
                }
                LayoutInflater inflater = LayoutInflater.from(AdvanceActivity.this);
                View layout = inflater.inflate(R.layout.custom_alertdialog, null);
                final AlertDialog dialog = DialogTool.createDialog(AdvanceActivity.this, layout, R.string.advance_succ, R.string.cancle_tip, R.string.sure_tip);
                layout.findViewWithTag(0).setVisibility(View.GONE);
                layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "withdrawalRequest onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(AdvanceActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(AdvanceActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_over:
                int money = Tools.StringToInt(edit_money.getText() + "");
                if (money == 0) {
                    MyToast.makeTextAnim(AdvanceActivity.this, R.string.unable_advance, 0, R.style.PopToast).show();
                    return;
                }
                advance();
                break;
        }
    }
}
