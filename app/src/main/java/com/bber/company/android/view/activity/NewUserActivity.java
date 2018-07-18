package com.bber.company.android.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.bber.company.android.ActivityNewUserBinding;
import com.bber.company.android.R;
import com.bber.company.android.view.adapter.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * 浏览聊天和原图界面
 * Created by Vencent on 17-3-7.
 */
public class NewUserActivity extends BaseActivity {

    private ActivityNewUserBinding binding;
    private ViewPager new_user_viewpager;
    private List<View> imageViews;
    private List<Integer> pictureList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载databinding的双向绑定
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_user);
        binding.setHeaderBarViewModel(headerBarViewModel);
        //加载6长图片
        initPic();
        //加载页面层
        initViews();

        //获取数据层
        initData();

//        setHeaderBar();
    }

    private void initPic() {
        pictureList = new ArrayList<>();
        pictureList.add(R.drawable.icon_user_guide1);
        pictureList.add(R.drawable.icon_user_guide2);
        pictureList.add(R.drawable.icon_user_guide3);
        pictureList.add(R.drawable.icon_user_guide4_);
        pictureList.add(R.drawable.icon_user_guide5);
        pictureList.add(R.drawable.icon_user_guide6);
        imageViews = new ArrayList<>();
        for (int i = 0; i < pictureList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(this);
            final View view = inflater.inflate(R.layout.browse_item, null);
            final SimpleDraweeView imageView = view.findViewById(R.id.image_back);
            Glide.with(view.getContext()).load(pictureList.get(i)).dontAnimate().into(imageView);

            imageViews.add(view);
        }
    }


    private void initViews() {
        new_user_viewpager = binding.newUserViewpager;
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, imageViews);
        new_user_viewpager.setAdapter(viewPagerAdapter);
    }

    private void initData() {
    }

    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle("新手教程");
    }
}
