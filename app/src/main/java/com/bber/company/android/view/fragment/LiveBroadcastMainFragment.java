package com.bber.company.android.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.http.HttpUtil;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.util.ChenQianLog;
import com.bber.company.android.view.activity.LiveBroadcastAuthenticationActivity;
import com.bber.company.android.view.activity.LiveBroadcastDetailsActivity;
import com.bber.company.android.view.activity.LiveBroadcastSearchActivity;
import com.bber.company.android.view.adapter.LiveBroadcastMainViewPgerAdapter;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.bber.company.android.widget.MyToast;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播 主 Fragment
 * Created by carlo.c on 2018/4/10.
 */

public class LiveBroadcastMainFragment extends BaseFragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private LinearLayout nearby, recommend, classification, search_model;
    private ViewPager libroadcast_main_viewpager;
    private LiveBroadcastMainViewPgerAdapter ViewPgerAdapter;
    private Fragment ClassificationFragment, NearbyFragment, RecommendFragment;
    private List<Fragment> list;
    /**
     * 我要当主播
     */
    private TextView my_anchor;

    private TextView nearby_text;
    private TextView recommend_text;
    private TextView classification_text;


    private ImageView nearby_icon;
    private ImageView recommend_icon;
    private ImageView classification_icon;

    private ImageView nearby_image;
    private ImageView recommend_image;
    private ImageView classification_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_livebroadcast_main_layout, container, false);
        getUserInfo();
        initView(view);
        initLinener();
        return view;
    }

    private void getUserInfo() {
        RequestParams params = new RequestParams();
        JsonUtil jsonUtil = new JsonUtil(getContext());
        String head = jsonUtil.httpHeadToJson(getContext());
        params.put("head", head);
        DialogView.show(getContext(), true);
        String getUserInfourl = Constants.getInstance().getUserInfo;
        params.put("id", SharedPreferencesUtils.get(getContext(), Constants.USERID, ""));
        HttpUtil.get(getUserInfourl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final JSONObject jsonObject) {

                ChenQianLog.i("getUserInfo：" + jsonObject);
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 1) {
                        JSONObject dataCollection = jsonObject.getJSONObject("dataCollection");
                        int broadcase_level = dataCollection.getJSONObject("BuyerUserEntity").getInt("anchorStatus");
                        SharedPreferencesUtils.put(getContext(), Constants.BROADCAST_LEVEL, broadcase_level);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e("eeeeeeeeeeee", "getUserInfo onFailure--throwable:" + throwable);
                MyToast.makeTextAnim(getContext(), R.string.getData_fail, 0, R.style.PopToast).show();
            }

            @Override
            public void onFinish() { // 完成后调用，失败，成功，都要调用
                DialogView.dismiss(getContext());
            }
        });
    }

    private void initLinener() {

        my_anchor.setOnClickListener(this);
        nearby.setOnClickListener(this);
        recommend.setOnClickListener(this);
        classification.setOnClickListener(this);
        search_model.setOnClickListener(this);
    }

    private void initView(View view) {
        search_model = view.findViewById(R.id.search_model_id);
        nearby = view.findViewById(R.id.nearby_id);
        recommend = view.findViewById(R.id.recommend_id);
        classification = view.findViewById(R.id.classification_id);
        my_anchor = view.findViewById(R.id.my_anchor);
        libroadcast_main_viewpager = view.findViewById(R.id.libroadcast_main_viewpager_id);
        list = new ArrayList<>();

        if (NearbyFragment == null) {
            NearbyFragment = new LiveBroadcastNearbyFragment();
        }
        if (ClassificationFragment == null) {
            ClassificationFragment = new LiveBroadcastClassificationFragment();
        }
        if (RecommendFragment == null) {
            RecommendFragment = new LiveBroadcastRecommendFragment();

        }
        list.add(NearbyFragment);
        list.add(RecommendFragment);
        list.add(ClassificationFragment);

        ViewPgerAdapter = new LiveBroadcastMainViewPgerAdapter(getChildFragmentManager(), list);
        libroadcast_main_viewpager.setAdapter(ViewPgerAdapter);
        libroadcast_main_viewpager.setCurrentItem(1);
        libroadcast_main_viewpager.setOnPageChangeListener(this);
        nearby_text = view.findViewById(R.id.nearby_text);
        nearby_image = view.findViewById(R.id.nearby_image);
        recommend_icon = view.findViewById(R.id.recommend_icon);
        nearby_icon = view.findViewById(R.id.nearby_icon);
        recommend_text = view.findViewById(R.id.recommend_text);
        recommend_image = view.findViewById(R.id.recommend_image);
        classification_icon = view.findViewById(R.id.classification_icon);
        classification_text = view.findViewById(R.id.classification_text);
        classification_image = view.findViewById(R.id.classification_image);

    }

    /**
     * 选择附近
     */
    private void selectNearby() {

        nearby_text.setTextColor(getResources().getColor(R.color.liveboadcast_textok));
        recommend_text.setTextColor(getResources().getColor(R.color.liveboadcast_texton));
        classification_text.setTextColor(getResources().getColor(R.color.liveboadcast_texton));


        nearby_icon.setImageResource(R.mipmap.livebroadcast_nearby_ok);
        recommend_icon.setImageResource(R.mipmap.livebroadcast_recommend_on);
        classification_icon.setImageResource(R.mipmap.livebroadcast_select_on);

        nearby_image.setVisibility(View.VISIBLE);
        recommend_image.setVisibility(View.GONE);
        classification_image.setVisibility(View.GONE);
    }

    /**
     * 选择分类
     */
    private void selectClassification() {

        nearby_text.setTextColor(getResources().getColor(R.color.liveboadcast_texton));
        recommend_text.setTextColor(getResources().getColor(R.color.liveboadcast_texton));
        classification_text.setTextColor(getResources().getColor(R.color.liveboadcast_textok));

        nearby_icon.setImageResource(R.mipmap.livebroadcast_nearby_on);
        recommend_icon.setImageResource(R.mipmap.livebroadcast_recommend_on);
        classification_icon.setImageResource(R.mipmap.livebroadcast_select_ok);

        nearby_image.setVisibility(View.GONE);
        recommend_image.setVisibility(View.GONE);
        classification_image.setVisibility(View.VISIBLE);
    }

    /**
     * 选择推荐
     */
    private void selectRecommend() {
        nearby_text.setTextColor(getResources().getColor(R.color.liveboadcast_texton));
        recommend_text.setTextColor(getResources().getColor(R.color.liveboadcast_textok));
        classification_text.setTextColor(getResources().getColor(R.color.liveboadcast_texton));

        nearby_icon.setImageResource(R.mipmap.livebroadcast_nearby_on);
        recommend_icon.setImageResource(R.mipmap.livebroadcast_recommend_ok);
        classification_icon.setImageResource(R.mipmap.livebroadcast_select_on);

        nearby_image.setVisibility(View.GONE);
        recommend_image.setVisibility(View.VISIBLE);
        classification_image.setVisibility(View.GONE);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        switch (position) {
            case 0:
                selectNearby();
                break;
            case 1:
                selectRecommend();
                break;
            case 2:
                selectClassification();
                break;
            default:
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.my_anchor: // 我要当主播

                int broadcaselenel = (int) SharedPreferencesUtils.get(getContext(), Constants.BROADCAST_LEVEL, 0);
                Intent intent;
                if (broadcaselenel == 3) {
                    intent = new Intent(getContext(), LiveBroadcastDetailsActivity.class);
                    intent.putExtra("fragmentname", "Main");

                } else {
                    intent = new Intent(getContext(), LiveBroadcastAuthenticationActivity.class);

                }
                startActivity(intent);
                break;
            case R.id.search_model_id: // 关键字搜索

                startActivity(new Intent(getContext(), LiveBroadcastSearchActivity.class));
                break;
            case R.id.nearby_id: // 附近

                selectNearby();
                libroadcast_main_viewpager.setCurrentItem(0);

                break;
            case R.id.recommend_id: // 推荐
                selectRecommend();
                libroadcast_main_viewpager.setCurrentItem(1);

                break;
            case R.id.classification_id: // 分类

                selectClassification();
                libroadcast_main_viewpager.setCurrentItem(2);

                break;
        }
    }
}
