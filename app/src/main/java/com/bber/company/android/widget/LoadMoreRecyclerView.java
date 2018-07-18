package com.bber.company.android.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.customview.utils.LogUtils;
import com.bumptech.glide.Glide;


/**
 * 李立强 可上拉加载的RecyclerView 2016.1.18
 * 随着问题的不断增加本类也将进行升级与bug的修复
 */
public class LoadMoreRecyclerView extends RecyclerView {
    /**
     * item 类型
     */
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_HEADER = 1;//头部--支持头部增加一个headerView
    public final static int TYPE_FOOTER = 2;//底部--往往是loading_more
    public final static int TYPE_LIST = 3;//代表item展示的模式是list模式
    public final static int TYPE_STAGGER = 4;//代码item展示模式是网格模式

    public final static int UPDATEINSERT = 1;  //插入更新
    public final static int UPDATEFULL = 2;     //全部刷新更新

    public static int UPDATESTYLE = 2;
    /**
     * 自定义实现了头部和底部加载更多的adapter
     */
    private AutoLoadAdapter mAutoLoadAdapter;
    /**
     * 标记是否正在加载更多，防止再次调用加载更多接口
     */
    private boolean mIsLoadingMore;
    /**
     * 标记加载更多的position
     */
    private int mLoadMorePosition;
    /**
     * 加载更多的监听-业务需要实现加载数据
     */
    private LoadMoreListener mListener;
    private boolean mIsFooterEnable = false;//是否允许加载更多
    private boolean isShowLoadmore = false;  //更多加载完成后，是否显示底部文字
    private boolean isLoadingCache = true;  //设置是否滚动过程中加载loading图片
    /**
     * 头部的View
     */
    private View headView;

    /***
     * @param text
     */
    private String loadMoreText = "";

    public LoadMoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化-添加滚动监听
     * <p/>
     * 回调加载更多方法，前提是
     * <pre>
     *    1、有监听并且支持加载更多：null != mListener && mIsFooterEnable
     *    2、目前没有在加载，正在上拉（dy>0），当前最后一条可见的view是否是当前数据列表的最好一条--及加载更多
     * </pre>
     */
    private void init() {
        loadMoreText = getResources().getString(R.string.drop_down_list_header_loading_text);

        super.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE  //如果没有此处判断，则快速下拉时，onLoadMore（）为加载数据完成
                        //导致mLinearLayoutManager.getItemCount()的值还未改变，故易出现bug
                        //此法缺点：要载入更多则必须有停顿一下
                        && null != mListener && mIsFooterEnable && !mIsLoadingMore) {
                    int lastVisiblePosition = getLastVisiblePosition();
                    if (lastVisiblePosition + 1 == mAutoLoadAdapter.getItemCount()) {
                        setLoadingMore(true);
                        mLoadMorePosition = lastVisiblePosition;
                        mListener.onLoadMore();
                    }
                }

                try {
                    if (isLoadingCache) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            //滑动的时候不进行图片的加载 Glide 的模式
                            Glide.with(getContext()).resumeRequests();            //Glide 会发生不定时崩溃
                        } else {
                            Glide.with(getContext()).pauseRequests();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LogUtils.e(ex);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * 设置加载更多的监听
     *
     * @param listener
     */
    public void setLoadMoreListener(LoadMoreListener listener) {
        mListener = listener;
    }

    /**
     * 设置正在加载更多
     *
     * @param loadingMore
     */
    private void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
    }

    /**
     * 设置滚动不加载cache loading图片
     *
     * @param loadingCache
     */
    public void setLoadingCache(boolean loadingCache) {
        isLoadingCache = loadingCache;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            mAutoLoadAdapter = new AutoLoadAdapter(adapter);
        }
        super.swapAdapter(mAutoLoadAdapter, true);
    }

    /**
     * 获得当前展示最小的position
     *
     * @param positions
     * @return
     */
    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    public int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    /**
     * 添加头部view
     *
     * @param view
     */
    public void addHeaderView(View view) {
        if (mAutoLoadAdapter != null) {
            mAutoLoadAdapter.addHeaderView(view);
        } else {
            headView = view;
        }
    }

    public void showLoadMoreVisible(boolean _show) {
        isShowLoadmore = _show;
    }

    /**
     * 加载完成处理该函数，去除加载效果
     *
     * @param [参数名] [参数说明]
     * @return [返回类型] [返回类型说明]
     * @author PHC
     * @date 2016/2/5 15:18
     */
    public void refreshCompleted() {
        setLoadingMore(false);  //标志非loading状态
        getAdapter().notifyItemChanged(getAdapter().getItemCount());
    }

    /**
     * 添加底部view，如无则使用默认
     *
     * @param resId
     */
    public void addFooterView(int resId) {
        if (mAutoLoadAdapter != null) {
            mAutoLoadAdapter.addFooterView(resId);
        }
    }

    //设置底部文字
    public void setFooter(String str) {
        loadMoreText = str;
        showLoadMoreVisible(true);
        if (mIsLoadingMore) {
            setLoadingMore(false); //非loading
        }
        if (getAdapter() != null) {
            getAdapter().notifyItemChanged(getAdapter().getItemCount() - 1);
        }
    }

    //设置底部文字,用于变换没有更多切换到加载中的情况，第一次调用adapter为空的情况
    public void setFooterText(String str) {
        loadMoreText = str;
    }

    /**
     * 设置是否支持自动加载更多
     *
     * @param autoLoadMore
     */
    public void setLoadMoreEnable(boolean autoLoadMore) {
        mIsFooterEnable = autoLoadMore;
    }

    /**
     * 加载更多监听
     */
    public interface LoadMoreListener {
        /**
         * 加载更多
         */
        void onLoadMore();
    }

    public class AutoLoadAdapter extends Adapter<ViewHolder> {

        private Adapter mInternalAdapter;
        //头部的View
        private View mHeaderVeiw = null;
        //底部图片
        private int mFooterResId;
        //设置的footview
        private View view;
        //传递进来底部的文字
        private String footText;

        public AutoLoadAdapter(Adapter adapter) {
            mInternalAdapter = adapter;
        }

        @Override
        public int getItemViewType(int position) {
            int headerPosition = 0;
            int footerPosition = getItemCount() - 1;

            if (headerPosition == position && (mHeaderVeiw != null || headView != null)) {
                return TYPE_HEADER;
            }
            if (footerPosition == position) {
                return TYPE_FOOTER;
            }
            return TYPE_NORMAL;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                return new HeaderViewHolder(mHeaderVeiw != null ? mHeaderVeiw : headView);
            }
            if (viewType == TYPE_FOOTER) {
                view = LayoutInflater.from(parent.getContext()).inflate(
                        mFooterResId > 0 ? mFooterResId : R.layout.loadmorerecycle_foot_loading, parent, false);
                return new FooterViewHolder(view);//如果自定义上拉view 则显示上拉view
            } else { // type normal
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type != TYPE_FOOTER && type != TYPE_HEADER) {
                if (mHeaderVeiw != null || headView != null) {
                    mInternalAdapter.onBindViewHolder(holder, --position);
                } else {
                    mInternalAdapter.onBindViewHolder(holder, position);
                }
            }
            if (type == TYPE_FOOTER) {
                //处理底部
                ((FooterViewHolder) holder).textview.setText(loadMoreText);
                if (mIsFooterEnable) {
                    ((FooterViewHolder) holder).textview.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).progressbar.setVisibility(View.VISIBLE);
                } else if (isShowLoadmore == true) {
                    ((FooterViewHolder) holder).progressbar.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).textview.setVisibility(View.VISIBLE);
                } else {
                    ((FooterViewHolder) holder).textview.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).progressbar.setVisibility(View.GONE);
                }
            }
        }

        /**
         * 需要计算上加载更多和添加的头部俩个
         *
         * @return
         */
        @Override
        public int getItemCount() {
            int count = mInternalAdapter.getItemCount();
            if (mIsLoadingMore || isShowLoadmore) count++;
            if (mHeaderVeiw != null || headView != null) count++;
            return count;
        }

        /**
         * 添加头部和未部View正常显示
         *
         * @param holder
         */
        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && holder.getLayoutPosition() == 0 && mHeaderVeiw != null && headView != null) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && holder.getLayoutPosition() == getAdapter().getItemCount() - 1 && mIsFooterEnable == true) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        public void addHeaderView(View view) {
            mHeaderVeiw = view;
        }

        public void addFooterView(int resId) {
            mFooterResId = resId;
        }

        public class FooterViewHolder extends ViewHolder {
            TextView textview;
            ProgressBar progressbar;

            public FooterViewHolder(View itemView) {
                super(itemView);
                textview = view.findViewById(R.id.footbar);
                progressbar = view.findViewById(R.id.recycler_progress);
            }
        }

        public class HeaderViewHolder extends ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


}
