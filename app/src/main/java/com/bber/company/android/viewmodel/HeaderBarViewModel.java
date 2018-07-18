package com.bber.company.android.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.customview.utils.LogUtils;


/**
 * author:Vencent<Vencent@sharing8.cn>
 * date: 2016/1/12
 * description:
 * headerBarViewModel.setBarTitle("登陆").setRightImg(gContext.getResources().getDrawable(R.drawable.top_rule));
 * headerBarViewModel.setBarTitle("登陆").setRightText("右边");
 * headerBarViewModel.setBarTitle("登陆");
 * headerBarViewModel.setLeftDisable();
 */
public class HeaderBarViewModel {
    /**
     * 标题
     */
    public ObservableField<String> barTitle = new ObservableField<>("标题");

    public ObservableBoolean isShowLeft = new ObservableBoolean(true);
    public ObservableBoolean isShowAll = new ObservableBoolean(true);
    /**
     * 左边图片
     */
    public ObservableField<Drawable> leftImage = new ObservableField<>();
    /**
     * 左边文字
     */
    public ObservableField<String> leftText = new ObservableField<>();
    /**
     * 右边图片
     */
    public ObservableField<Drawable> rightImage = new ObservableField<>();
    /**
     * 右边文字
     */
    public ObservableField<String> rightText = null;

    /**
     * 背景色，如不为零，则改变背景色
     */
    public ObservableInt backColor = new ObservableInt(MyApplication.getContext().getResources().getColor(R.color.main_theme));
    /**
     * 状态栏颜色
     */
    public ObservableInt statusBarColor = new ObservableInt(0);

    /**
     * 点击事件
     */
    public headerclickListener clickListener;
    public headerTitleListener titleclickListener;


    public HeaderBarViewModel() {
        clickListener = new headerclickListener() {
            @Override
            public void leftClickListener(View v) {
                if (AppManager.getAppManager().currentActivity() != null) {
                    AppManager.getAppManager().currentActivity().onBackPressed();
                }
            }

            @Override
            public void rightClickListener(View v) {

            }
        };
        titleclickListener = new headerTitleListener() {
            @Override
            public void titleListener(View v) {

            }
        };
        LogUtils.e("走到这里来了吗");
        leftImage.set(MyApplication.getContext().getResources().getDrawable(R.mipmap.back));
    }

    /**
     * 设置标题
     *
     * @param _title 标题
     */
    public HeaderBarViewModel setBarTitle(String _title) {
        barTitle.set(_title);
        return this;
    }

    /**
     * 设置标题
     *
     * @param _titlesid 字符串资源id
     */
    public HeaderBarViewModel setBarTitleResourceId(int _titlesid) {

        return this.setBarTitle(MyApplication.getContext().getResources().getString(_titlesid));
    }

    /**
     * 设置右边文字
     *
     * @param _righttxt
     * @return
     */
    public HeaderBarViewModel setRightText(String _righttxt) {
        rightText = new ObservableField<>(_righttxt);
        return this;
    }

    /**
     * 设置标题
     *
     * @param _righttextsid
     */
    public HeaderBarViewModel setRightTextResourceId(int _righttextsid) {
        return this.setRightText(MyApplication.getContext().getResources().getString(_righttextsid));
    }

    /**
     * 设置左边文字
     *
     * @param _lefttxt
     * @return
     */
    public HeaderBarViewModel setLeftText(String _lefttxt) {
        leftText.set(_lefttxt);
        return this;
    }

    /**
     * 设置左边标题
     *
     * @param _lefttxt
     */
    public HeaderBarViewModel setLeftTextResourceId(int _lefttxt) {
        return this.setLeftText(MyApplication.getContext().getResources().getString(_lefttxt));
    }

    /**
     * 设置左边图标
     *
     * @param _leftimg
     * @return
     */
    public HeaderBarViewModel setLeftImg(Drawable _leftimg) {
        leftImage.set(_leftimg);
        return this;
    }

    public HeaderBarViewModel setLeftImgResourceId(int _rightimgid) {
        return this.setLeftImg(MyApplication.getContext().getResources().getDrawable(_rightimgid));
    }

    /**
     * 设置右边图标
     *
     * @param _rightimg
     * @return
     */
    public HeaderBarViewModel setRightImg(Drawable _rightimg) {
        rightImage.set(_rightimg);
        return this;
    }

    /**
     * 设置右边图标
     *
     * @param _rightimgid
     * @return
     */
    public HeaderBarViewModel setRightImgResourceId(int _rightimgid) {
        return this.setRightImg(MyApplication.getContext().getResources().getDrawable(_rightimgid));
    }

    /**
     * 设置左边返回隐藏
     *
     * @return
     */
    public HeaderBarViewModel setLeftDisable() {
        isShowLeft = new ObservableBoolean(false);
        return this;
    }

    /**
     * 设置全部返回隐藏
     *
     * @return
     */
    public HeaderBarViewModel setAllDisable(boolean isShow) {
        isShowAll.set(isShow);
        return this;
    }


    public HeaderBarViewModel setBackColorResourceId(int _bgcolor) {
        backColor.set(MyApplication.getContext().getResources().getColor(_bgcolor));
        return this;
    }

    /**
     * 设置点击事件
     *
     * @param _clicklistener
     */
    public void setOnClickListener(headerclickListener _clicklistener) {
        clickListener = _clicklistener;
    }

    /**
     * 设置点击事件
     *
     * @param _clicklistener
     */
    public void setHeadListener(headerTitleListener _clicklistener) {
        titleclickListener = _clicklistener;
    }

    public interface headerclickListener {
        /**
         * 左边点击
         */
        void leftClickListener(View v);

        /**
         * 右边点击事件
         */
        void rightClickListener(View v);
    }

    public interface headerTitleListener {
        /**
         * 中间点击事件
         */
        void titleListener(View v);

    }
}
