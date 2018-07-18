package com.bber.company.android.listener;

import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

/**
 * 滚动至列表底部，读取下一页数据
 * Created by carlo.c on 2018/4/11.
 *
 * @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
 * Bundle savedInstanceState) {
 * View view = inflater.inflate(R.layout.fragment_tab, container, false);
 * mListView = (GridView) view
 * .findViewById(R.id.id_stickynavlayout_innerscrollview);
 * // mTextView = (TextView) view.findViewById(R.id.id_info);
 * // mTextView.setText(mTitle);
 * AutoLoadListener autoLoadListener = new AutoLoadListener(callBack);
 * mListView.setOnScrollListener(autoLoadListener);
 * initPage();
 * return view;
 * <p>
 * }
 * <p>
 * AutoLoadListener.AutoLoadCallBack callBack = new AutoLoadListener.AutoLoadCallBack() {
 * <p>
 * public void execute() {
 * //            Utils.showToast("已经拖动至底部");
 * loadSpareItems(currentPage + 1);//这段代码是用来请求下一页数据的
 * }
 * <p>
 * };
 */

public class AutoLoadListener implements AbsListView.OnScrollListener {
    private int getLastVisiblePosition = 0, lastVisiblePositionY = 0;
    private AutoLoadCallBack mCallback;

    public AutoLoadListener(AutoLoadCallBack callback) {
        this.mCallback = callback;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            //滚动到底部
            if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                View v = view.getChildAt(view.getChildCount() - 1);
                int[] location = new int[2];
                v.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
                int y = location[1];

                if (view.getLastVisiblePosition() != getLastVisiblePosition && lastVisiblePositionY != y)//第一次拖至底部
                {
                    Toast.makeText(view.getContext(), "已经拖动至底部，再次拖动即可翻页", Toast.LENGTH_SHORT).show();
                    getLastVisiblePosition = view.getLastVisiblePosition();
                    lastVisiblePositionY = y;
                    return;
                } else if (view.getLastVisiblePosition() == getLastVisiblePosition && lastVisiblePositionY == y)//第二次拖至底部
                {
                    mCallback.execute();
                }
            }

            //未滚动到底部，第二次拖至底部都初始化
            getLastVisiblePosition = 0;
            lastVisiblePositionY = 0;
        }
    }

    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

    }

    public interface AutoLoadCallBack {
        void execute();
    }
}
