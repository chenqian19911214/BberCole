package com.bber.company.android.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bber.company.android.BR;
import com.bber.company.android.viewmodel.BaseViewModel;

import java.util.List;


/**
 * 项目名称：
 * 类描述：
 * 创建人：PHC
 * 创建时间：2016/1/18 12:17
 * 修改人：PHC
 * 修改时间：2016/1/18 12:17
 * 修改备注：
 *
 * @version v1.0
 */
public class RecyclerItemAdapter extends RecyclerView.Adapter<RecyclerItemAdapter.ViewHolder> {

    private Context mContext;
    private List dataSources;
    private int itemLayout;
    private BaseViewModel viewModel;

    public RecyclerItemAdapter(Context _context, List _data, int _itemlayout) {
        mContext = _context;
        dataSources = _data;
        itemLayout = _itemlayout;
    }

    public RecyclerItemAdapter(Context _context, List _data, int _itemlayout, BaseViewModel viewModel) {
        mContext = _context;
        dataSources = _data;
        itemLayout = _itemlayout;
        this.viewModel = viewModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(itemLayout, viewGroup, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        if (dataSources.size() > 0) {
            if (i >= dataSources.size()) {
                i = dataSources.size() - 1;
            }
            viewHolder.bind(dataSources.get(i), i);
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getItemCount() {
        if (dataSources == null) {
            return 0;
        }
        return dataSources.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return this.binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }

        public void bind(@NonNull Object data, int _index) {
            binding.setVariable(BR.item, data);
            try {
                binding.setVariable(BR.index, _index);
                if (viewModel != null) {
                    binding.setVariable(BR.viewModel, viewModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //binding.executePendingBindings();
        }
    }
}
