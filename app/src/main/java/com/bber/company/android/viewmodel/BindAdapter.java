package com.bber.company.android.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.widget.LoadMoreRecyclerView;
import com.bber.company.android.widget.NumAnim;
import com.bber.company.android.widget.RecyclerItemAdapter;
import com.bber.company.android.widget.RecyclerItemClickListener;
import com.bber.customview.utils.LogUtils;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Vencent on 2016/1/14.
 * Description: databinding自定义bindadapter，公用方法
 */
public class BindAdapter {
    private final static String TAG = "BindAdapter";

    /***
     * @param bindableString
     * @return
     * @description binding方法，用于xml中自动转换
     */
    @BindingConversion
    public static String convertBindableToString(
            BindableString bindableString) {
        return bindableString.get();
    }

    @BindingConversion
    public static int convertBindable(ObservableInt _int) {
        return _int.get();
    }

    /**
     * @param view
     * @param bindableString
     * @description bindingadapter方法, 在xml中edittext使用app:binding，实现双向绑定
     */
    @BindingAdapter({"app:binding"})
    public static void ebindEditText(EditText view, final BindableString bindableString) {
        if (view.getTag(R.id.bindEditTextTag) != bindableString) {
            view.setTag(R.id.bindEditTextTag, bindableString);
            view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    bindableString.set(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        String newValue = bindableString.get();
        if (!view.getText().toString().equals(newValue)) {
            view.setText(newValue);
        }
    }

    /***
     * @param view
     * @param error
     */
    @BindingAdapter({"app:error"})
    public static void bindEditTextError(EditText view, final BindableString error) {
        if (error.get().isEmpty() == false) {
            view.setError(error.get());
        }
    }

    /**
     * 设置listview itemlayout
     *
     * @param view
     * @param itemlayoutid
     */
//    @android.databinding.BindingAdapter({"app:itemList", "app:itemLayout"})
//    public static void bindItemLayout(ListView view, List itemlist, int itemlayoutid) {
//        ListViewItemAdapter listViewItemAdapter = null;
//        if (view.getTag(R.id.bindRecycleView) == null) {
//            view.setTag(R.id.bindRecycleView, true);
//            listViewItemAdapter = new ListViewItemAdapter(view.getContext(), itemlist, itemlayoutid);
//            view.setAdapter(listViewItemAdapter);
//        } else {
//            //view.deferNotifyDataSetChanged();
//            ((ListViewItemAdapter) view.getAdapter()).notifyDataSetChanged();
//        }
//    }

    /**
     * 设置listview itemlayout
     *
     * @param view
     * @param itemlayoutid
     */
    @android.databinding.BindingAdapter({"app:itemList", "app:itemLayout", "app:emptyView", "app:viewModel"})
    public static void bindRecycleItemLayout(RecyclerView view, List itemlist,
                                             int itemlayoutid, int emptyviewId, BaseViewModel viewModel) {
        if (view.getTag(R.id.bindRecycleView) == null) {
            view.setTag(R.id.bindRecycleView, true);
            if (viewModel == null) {
                RecyclerItemAdapter listViewItemAdapter = new RecyclerItemAdapter(view.getContext(), itemlist, itemlayoutid);
                view.setAdapter(listViewItemAdapter);
            } else {
                RecyclerItemAdapter listViewItemAdapter = new RecyclerItemAdapter(view.getContext(), itemlist, itemlayoutid, viewModel);
                view.setAdapter(listViewItemAdapter);
            }
        } else {
            if (view instanceof LoadMoreRecyclerView) {
                if (LoadMoreRecyclerView.UPDATESTYLE == LoadMoreRecyclerView.UPDATEINSERT) {
                    view.getAdapter().notifyItemInserted(view.getAdapter().getItemCount());
                } else {
                    view.getAdapter().notifyDataSetChanged();
//                    LoadMoreRecyclerView.UPDATESTYLE = LoadMoreRecyclerView.UPDATEINSERT;
                }
            } else {
                view.getAdapter().notifyDataSetChanged();
            }
        }
        LogUtils.e("recycle_adapter");
        View rootView = view.getRootView();
        View emptyView = rootView.findViewById(emptyviewId);
        if (emptyView != null) {
            if (view.getAdapter() != null && itemlist.size() == 0) {
                if (view.getParent() instanceof SwipeRefreshLayout) {
                    ((View) view.getParent()).setVisibility(View.GONE);
                }
                view.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                if (view.getParent() instanceof SwipeRefreshLayout) {
                    ((View) view.getParent()).setVisibility(View.VISIBLE);
                }
                view.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    @android.databinding.BindingAdapter({"app:itemList", "app:itemLayout", "app:emptyView"})
    public static void bindRecycleItemLayout(RecyclerView view, List itemlist, int itemlayoutid, int emptyviewId) {
        bindRecycleItemLayout(view, itemlist, itemlayoutid, emptyviewId, null);
    }


    /**
     * 设置recyclerview 的点击事件
     *
     * @param adapterView
     */
    @android.databinding.BindingAdapter({"app:itemClick"})
    public static void setOnItemClick(RecyclerView adapterView, final RecyclerItemClickListener.OnItemClickListener clickListener) {
        if (null != clickListener) {
            adapterView.addOnItemTouchListener(new RecyclerItemClickListener(adapterView.getContext(), adapterView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemLongClick(View view, int position) {
                    clickListener.onItemLongClick(view, position);
                }

                @Override
                public boolean onItemClick(View view, int position) {
                    return clickListener.onItemClick(view, position);
                }
            }));
        }
    }


    /**
     * 设置controlHeaderImage itemlayout
     */
//    @android.databinding.BindingAdapter({"app:imageUrl", "app:userName"})
//    public static void bindControlHeadImage(ControlHeadImage view, String _imgurl, String _username) {
//        //针对排行榜新接口进行替换  并使用Picasso 加载图片
//        if (!StringUtils.isEmpty(_imgurl) && (_imgurl.contains("http://") || _imgurl.contains("https://")) && !(_imgurl.contains("localhost"))) {
//            view.setImage(_imgurl);
//        } else if (!StringUtils.isEmpty(_username)) {
//            view.setImageText(_username);
//        }
//    }

    /**
     * Glide 图片
     *
     * @param view
     * @param imageUrl
     * @param defPhoto
     */
    @BindingAdapter({"app:imageUrl", "app:defPhoto"})
    public static void bindImageViewwithdef(ImageView view, String imageUrl, Drawable defPhoto) {
        LogUtils.e("imageUrl" + imageUrl);
        if (!Tools.isEmpty(imageUrl)) {
            if (defPhoto != null)
                Glide.with(view.getContext()).load(imageUrl).dontAnimate().placeholder(defPhoto).into(view);
            else
                Glide.with(view.getContext()).load(imageUrl).dontAnimate().into(view);
        } else if (defPhoto != null) {
            view.setImageDrawable(defPhoto);
        }
    }

    /**
     * 滚动数字控件
     *
     * @param view
     * @param
     * @param
     */
    @BindingAdapter({"app:num"})
    public static void bindTextViewChange(TextView view, float num) {
        NumAnim.startAnim(view, num, 500);
    }


    /**
     * Glide 本地图片
     *
     * @param view
     * @param
     * @param
     */
    @BindingAdapter({"app:imageSource"})
    public static void bindlocalImageViewwithdef(ImageView view, Integer imageSource) {
//        LogUtils.e("imageUrl"+imageUrl);
//        if (!Tools.isEmpty(imageUrl)) {
//            if (defPhoto != null)
//                Glide.with(view.getContext()).load(imageUrl).dontAnimate().placeholder(defPhoto).into(view);
//            else
//                Glide.with(view.getContext()).load(imageUrl).dontAnimate().into(view);
//        } else if (defPhoto != null) {
//            view.setImageDrawable(defPhoto);
//        }
        Glide.with(view.getContext()).load(imageSource).into(view);
    }

    /**
     * RatingBar Binding   //实现ratingBar 双休绑定
     *
     * @param ratingBar
     * @param value
     */
    @BindingAdapter({"app:ratingBar"})
    public static void bindImageViewwithdef(RatingBar ratingBar, final BindableString value) {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                value.set((int) rating + "");
            }
        });
        if (!value.get().equals("")) {
            ratingBar.setRating(Float.parseFloat(value.get()));
        }
    }

    /**
     * 设置GridView itemlayout
     *
     * @param view
     * @param itemlayoutid
     */
//    @android.databinding.BindingAdapter({"app:itemList", "app:itemLayout"})
//    public static void bindGridViewItemLayout(GridView view, List itemlist, int itemlayoutid) {
//        GridViewItemAdapter listViewItemAdapter = new GridViewItemAdapter(view.getContext(), itemlist, itemlayoutid);
//        view.setAdapter(listViewItemAdapter);
//    }
}
