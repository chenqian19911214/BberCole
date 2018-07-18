package com.bber.company.android.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.util.StringUtils;

import java.util.List;


/**
 * 类描述：recycle的定制的adapter
 * 创建人：Vencent
 * 创建时间：2016/10/8 12:17
 *
 * @version v1.0
 */
public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.MyViewHolder> {

    private Context mContext;
    private List<Integer> imageList; // 图片集合
    private List<String> titleList; //头像集合
    private OnItemClickLitener mOnItemClickLitener;
    private String data = "";
    private String money = "";


    public UserItemAdapter(Context _context, List<Integer> imageList, List<String> titleList) {
        mContext = _context;
        this.imageList = imageList;
        this.titleList = titleList;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    //改变某一行的数据
    public void setChangeData(int position, String data) {
        this.data = data;
        notifyItemChanged(position);
    }

    //改变某一行的数据
    public void setChangeMoney(int position, String data) {
        if (!data.equals("")) {
            this.money = StringUtils.doubleChangeIne(Double.parseDouble(data));
        }

        notifyItemChanged(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.adapter_user, viewGroup,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int i) {
        //将数据每个逐个载入
        viewHolder.imageView.setImageDrawable(MyApplication.getContext().getResources().getDrawable(imageList.get(i)));
        //载入标题资源
        viewHolder.textView.setText(titleList.get(i));
        if (!money.equals("")) {
            if (i == 1) {
                viewHolder.right_data.setText(money + "元");
                viewHolder.right_data.setVisibility(View.VISIBLE);
            }
        }

        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, pos);
                }
            });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(viewHolder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (titleList != null) {
            return titleList.size();
        }
        return 0;
    }

    //长按和短按的监听事件
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        private TextView right_data;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.icon_image);
            textView = itemView.findViewById(R.id.text_title);
            right_data = itemView.findViewById(R.id.right_data);

        }


    }
}
