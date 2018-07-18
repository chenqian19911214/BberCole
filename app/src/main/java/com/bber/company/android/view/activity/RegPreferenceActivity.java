package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.constants.preferenceConstants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.listener.HomeWatcher;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.FlowTagLayout;
import com.bber.customview.OptionsPickerView;
import com.bber.customview.adapter.TagAdapter;
import com.bber.customview.listener.OnTagSelectListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册第四步 用户名
 * *
 */
public class RegPreferenceActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ArrayList<String> options1Items = new ArrayList<String>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
    private OptionsPickerView pvOptions;

    private TextView text_age, text_heigt;
    private RelativeLayout view_age, view_height;
    private Button btn_login;
    private FlowTagLayout feature_tag, cup_tag;
    private String ageMax, ageMin;
    private String heightMax, heightMin;
    private int nowFlag;
    private TagAdapter<String> mCupTagAdapter;
    private TagAdapter<String> mFeatureTagAdapter;
    private String feature = "";
    private String cup = "";
    private boolean isRegister = false;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_preference;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MyApplication.getContext();
        app.allActivity.add(this);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        text_age = findViewById(R.id.text_age);
        text_heigt = findViewById(R.id.text_heigt);
        btn_login = findViewById(R.id.btn_login);
        feature_tag = findViewById(R.id.feature_tag);
        cup_tag = findViewById(R.id.cup_tag);
        view_age = findViewById(R.id.view_age);
        view_height = findViewById(R.id.view_height);
    }


    private void setListeners() {
        text_age.setOnClickListener(this);
        text_heigt.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        view_height.setOnClickListener(this);
        view_age.setOnClickListener(this);
    }


    private void initData() {
        HomeWatcher.mCamHomeKeyDown = false;
        pvOptions = new OptionsPickerView(this);

        mCupTagAdapter = new TagAdapter<>(this);
        mFeatureTagAdapter = new TagAdapter<>(this);
        btn_login.setEnabled(true);
        title.setText(R.string.preference);
        Bundle bundle = getIntent().getExtras();
        isRegister = bundle.getBoolean("isregister");
//        isRegister = getIntent().getBooleanExtra("isregister",false);
        initModelTag();
        if (isRegister == false) { //不是从注册端进来
            getBuyerUserInfo();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_age:
            case R.id.text_age:
                nowFlag = 0;
                getChooseValue(preferenceConstants.AGE_LIST, "年龄范围(岁)");
                pvOptions.show();
                break;
            case R.id.view_height:
            case R.id.text_heigt:
                nowFlag = 1;
                getChooseValue(preferenceConstants.HEIGHT_LIST, "身高范围（CM）");
                pvOptions.show();
                break;
            case R.id.btn_login:
                updateBuyerUserLeaning();
                break;
        }

    }

    /**
     * 更新个人偏好设置
     */

    private void updateBuyerUserLeaning() {
        String buyerId = SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "";
        DialogTool.createProgressDialog(this, true);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("uBuyer", buyerId);
        params.put("ubMinage", ageMin);
        params.put("ubMaxage", ageMax);
        params.put("ubMintall", heightMin);
        params.put("ubMaxtall", heightMax);
        params.put("ubCup", cup);
        params.put("ubCharactery", feature);
        HttpUtil.get(Constants.getInstance().updateBuyerUserLeaning, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                if (Tools.jsonResult(RegPreferenceActivity.this, jsonObject, null)) {
                    return;
                }
                try {
                    String resultCode = jsonObject.getString("resultCode");
                    if (resultCode.equals("1")) {
                        if (isRegister == true) {
                            String adPicture = (String) SharedPreferencesUtils.get(RegPreferenceActivity.this, "adPicture", "");
                            if (!Tools.isEmpty(adPicture)) {
                                Intent intent = new Intent(RegPreferenceActivity.this, ADActivity.class);
                                intent.putExtra("isLogin", true);
                                intent.putExtra("chen", "5");

                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(RegPreferenceActivity.this, MainActivity.class);
                                intent.putExtra("isLogin", true);
                                startActivity(intent);
                            }
                        }
                        finish();
                    } else {
                        String resultMessage = jsonObject.getString("resultMessage");
                        MyToast.makeTextAnim(RegPreferenceActivity.this, resultMessage, 0, R.style.PopToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(RegPreferenceActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(RegPreferenceActivity.this);
            }
        });
    }

    private void getBuyerUserInfo() {
        String buyerId = SharedPreferencesUtils.get(this, Constants.USERID, "-1") + "";
        DialogTool.createProgressDialog(this, true);
        RequestParams params = new RequestParams();
        final JsonUtil jsonUtil = new JsonUtil(this);
        String head = jsonUtil.httpHeadToJson(this);
        params.put("head", head);
        params.put("uBuyer", buyerId);
        HttpUtil.get(Constants.getInstance().getBuyerUserLeaning, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {
                try {
                    String resultCode = jsonObject.getString("resultCode");

                    if (resultCode.equals("1")) {
                        JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                        String ubMinage = dataCollection.getString("ubMinage");
                        String ubMaxage = dataCollection.getString("ubMaxage");
                        String ubMintall = dataCollection.getString("ubMintall");
                        String ubMaxtall = dataCollection.getString("ubMaxtall");
                        String ubCup = dataCollection.getString("ubCup");
                        String ubCharactery = dataCollection.getString("ubCharactery");

                        ageMin = ubMinage;
                        ageMax = ubMaxage;
                        heightMax = ubMaxtall;
                        heightMin = ubMintall;
                        cup = ubCup;
                        feature = ubCharactery;

                        setDeafultInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                MyToast.makeTextAnim(RegPreferenceActivity.this, R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogTool.dismiss(RegPreferenceActivity.this);
            }
        });
    }

    private void getChooseValue(String firstList[], String title) {

        for (int i = 0; i < firstList.length - 1; i++) {
            options1Items.add(firstList[i]);
        }
        //选项2
        for (int j = 0; j < firstList.length; j++) {
            ArrayList<String> options2Items_tmp = new ArrayList<String>();
            for (int k = j + 1; k < firstList.length; k++) {
                options2Items_tmp.add(firstList[k]);
            }
            options2Items.add(options2Items_tmp);
        }
        pvOptions.setPicker(options1Items, options2Items, true);
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(0, 0, 0);
        pvOptions.setTitle(title);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                if (options1 >= 0 && options1Items.size() > options1
                        && options2 >= 0 && options2Items.get(options1).size() > options2) {
                    switch (nowFlag) {
                        case 0:
                            ageMin = options1Items.get(options1);
                            ageMax = options2Items.get(options1).get(options2);
                            text_age.setText(ageMin + "-" + ageMax + "岁");
                            break;
                        case 1:
                            heightMin = options1Items.get(options1);
                            heightMax = options2Items.get(options1).get(options2);
                            text_heigt.setText(heightMin + "-" + heightMax + "CM");
                            break;
                    }
                    options1Items.clear();
                    options2Items.clear();
                }
            }
        });
    }

    private void initModelTag() {
        //移动研发标签
        feature_tag.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_MULTI);
        feature_tag.setTagCheckedMax(3);
        cup_tag.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        cup_tag.setAdapter(mCupTagAdapter);
        feature_tag.setAdapter(mFeatureTagAdapter);
        cup_tag.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    cup = "";
                    for (int i : selectedList) {
                        cup = String.valueOf(i + 1);
                    }
                }
            }
        });
        feature_tag.setOnTagSelectListener(new OnTagSelectListener() {
            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                if (selectedList != null && selectedList.size() > 0) {
                    feature = "";
                    for (int i : selectedList) {
                        feature = feature + String.valueOf(i + 1);
                    }
                }
            }
        });
        addToList();
    }

    private void addToList() {
        List<String> CupdataSource = new ArrayList<>();
        for (int i = 0; i < preferenceConstants.CUPSIZE_LIST.length; i++) {
            CupdataSource.add(preferenceConstants.CUPSIZE_LIST[i]);
        }
        mCupTagAdapter.onlyAddAll(CupdataSource);

        List<String> featuredataSource = new ArrayList<>();
        for (int i = 0; i < preferenceConstants.FEATURE_LIST.length; i++) {
            featuredataSource.add(preferenceConstants.FEATURE_LIST[i]);
        }
        mFeatureTagAdapter.onlyAddAll(featuredataSource);
    }

    private void setDeafultInfo() {
        if (!ageMin.equals("null") && !ageMax.equals("null")) {
            text_age.setText(ageMin + "-" + ageMax + "岁");
        }
        if (!heightMin.equals("null") && !heightMax.equals("null")) {
            text_heigt.setText(heightMin + "-" + heightMax + "CM");
        }
        for (int i = 0; i < cup.length(); i++) {
            String s = cup.substring(i, i + 1);
            int index = Tools.StringToInt(s) - 1;
            if (index >= 0 && index < mCupTagAdapter.getCount()) {
                cup_tag.setmCheckedTagArray(index);
            }
        }
        for (int i = 0; i < feature.length(); i++) {
            String s = feature.substring(i, i + 1);
            int index = Tools.StringToInt(s) - 1;
            if (index >= 0 && index < mFeatureTagAdapter.getCount()) {
                feature_tag.setmCheckedTagArray(index);
            }
        }

    }


}
