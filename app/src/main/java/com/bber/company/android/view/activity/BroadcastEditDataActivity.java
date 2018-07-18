package com.bber.company.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bber.company.android.R;
import com.bber.company.android.bean.livebroadcast.BroadcaseLabelBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.adapter.BroadcastSearchGridViewAdapter;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.widget.MyToast;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 主播信息 填写界面
 **/
public class BroadcastEditDataActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private FrameLayout details_back_id;
    private TextView title_name_id;
    private FrameLayout details_save_id;
    private EditText editText_id;
    private Intent intent;
    private BroadcaseLabelBean broadcaseLabelBean;
    private GridView broadcast_label_layout;
    private String name;
    private List<String> labelist, listitem;
    private StringBuffer data = new StringBuffer();

    private boolean[] isChacklist;

    /**
     * 根据 list 自定义TextView
     * <p>
     * qian
     */

    private boolean isbutton = false;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_broadcast_edit_data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_broadcast_edit_data);
        initData();
        //initView();
    }

    private void initData() {

        intent = getIntent();  //daname
        name = intent.getStringExtra("name");
        listitem = new ArrayList();
        switch (name) {
            case "edit_Name": //编辑昵称
                ininView();
                editText_id.getLayoutParams().height = 100;
                broadcast_label_layout.setVisibility(View.GONE);
                break;
            case "edit_jianjie": //个人简介

                ininView();
                editText_id.getLayoutParams().height = 300;
                editText_id.setGravity(View.SCROLL_INDICATOR_RIGHT);
                broadcast_label_layout.setVisibility(View.GONE);

                break;
            case "edit_biaoqian": //标签
                anchorInfo();
                break;
        }
    }

    int postion;
    boolean isChec = false;

    private void ininView() {
        details_back_id = findViewById(R.id.details_back_data_id);
        details_back_id.setOnClickListener(this);
        title_name_id = findViewById(R.id.title_name_id);
        details_save_id = findViewById(R.id.details_save_data_id);
        details_save_id.setOnClickListener(this);
        editText_id = findViewById(R.id.editText_id);
        title_name_id.setText(intent.getStringExtra("daname"));
        editText_id.setText(intent.getStringExtra("data"));
        broadcast_label_layout = findViewById(R.id.broadcast_label_layout);


        broadcast_label_layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



               boolean currentviewstatus = isChacklist[i];
                String itemdata = labelist.get(i) + ",";

               if (currentviewstatus){
                   isChacklist[i] = false;
                   if (listitem.contains(itemdata)) {
                       listitem.remove(itemdata);
                   }
                   view.setBackground(getResources().getDrawable(R.mipmap.textbackage));

               }else {
                   isChacklist[i] = true;
                   listitem.add(itemdata);
                   view.setBackgroundColor(getResources().getColor(R.color.Azure));

               }

            }
        });

    }

    private boolean[] getMenuItemsSelectState() {

        boolean[] selsctStates = new boolean[labelist.size()];
      /*  for (int j = 0; j < selsctStates.length; j++) {
            selsctStates[j] = false;
            view.setBackground(getResources().getDrawable(R.mipmap.textbackage));

        }
        selsctStates[position] = true;
        view.setBackgroundColor(getResources().getColor(R.color.Azure));*/


        for (int i = 0; i < selsctStates.length; i++) {
            selsctStates[i] = false;
        }

        return selsctStates;
    }

    private void anchorInfo() {
        DialogView.show(this, true);
        RequestParams params = new RequestParams();
        JsonUtil jsonUtil = new JsonUtil(getApplicationContext());
        String head = jsonUtil.httpHeadToJson(getApplicationContext());
        params.put("head", head);
        HttpUtil.post(Constants.getInstance().anchorInfo, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i1, Header[] headers, final JSONObject jsonObject) {

                ChenQianLog.i("获取主播标签：" + jsonObject);
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        broadcaseLabelBean = new Gson().fromJson(jsonObject.toString(), BroadcaseLabelBean.class);
                        ininView();
                        // customTextView();
                        labelist = broadcaseLabelBean.getDataCollection();
                        editText_id.setVisibility(View.GONE);
                        broadcast_label_layout.setAdapter(new BroadcastSearchGridViewAdapter(getApplicationContext(), labelist));

                        isChacklist = getMenuItemsSelectState();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getUserInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(getApplicationContext(), R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogView.dismiss(BroadcastEditDataActivity.this);
            }
        });
    }

    private void customTextView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < broadcaseLabelBean.getDataCollection().size(); i++) {
            final TextView textView = new TextView(getApplicationContext());
            // Drawable drawable= getResources().getDrawable(R.mipmap.livebroadcast_nearby_ok);
            // drawable.setBounds(0, 0, 45, 45);//必要,不然会不显示 45为宽高
            //  layoutParams.setMargins(0, 5, 10, 5);
            textView.setTextSize(12);
            textView.setBackgroundResource(R.mipmap.textbackage); //设置背景
            textView.setGravity(View.SCREEN_STATE_ON);
            // textView.setMarqueeRepeatLimit(20);
            // textView.setCompoundDrawables(drawable, null, null, null);
            textView.setBottom(R.drawable.icon_bottom);
            layoutParams.setMargins(10, 20, 0, 10);
            textView.setTag(i);
            textView.setText(broadcaseLabelBean.getDataCollection().get(i) + "");
            textView.setLayoutParams(layoutParams);

            textView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (!isbutton) {
                        isbutton = true;
                        textView.setBackgroundColor(R.mipmap.textbackagered);
                    } else {
                        textView.setBackgroundResource(R.mipmap.textbackage); //设置背景

                        isbutton = false;
                    }
                    ChenQianLog.i("view.getTag:" + view.getTag());
                }
            });

            broadcast_label_layout.addView(textView);
        }
    }

    private String submit() {
        // validate
        String id = editText_id.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, "id不能为空", Toast.LENGTH_SHORT).show();
            return "";
        }
        return id;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.details_back_data_id://返回

                finish();
                break;

            case R.id.details_save_data_id://完成
                Intent intent = new Intent();

                String editdata;
                if (name.equals("edit_biaoqian")) {
                    for (String str : listitem) {
                        data.append(str);
                    }

                    editdata = data.toString();
                } else {
                    editdata = submit();
                }
                intent.putExtra("dataname", editdata);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
