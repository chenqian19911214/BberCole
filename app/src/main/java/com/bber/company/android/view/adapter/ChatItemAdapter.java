package com.bber.company.android.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.ChatInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * 类描述：recycle的定制的adapter
 * 创建人：Vencent
 * 创建时间：2016/10/8 12:17
 *
 * @version v1.0
 */
public class ChatItemAdapter extends RecyclerView.Adapter<ChatItemAdapter.MyViewHolder> {

    private Context mContext;
    private List<ChatInfo> messageBeanList; // 图片集合
    private OnItemClickLitener mOnItemClickLitener;

    public ChatItemAdapter(Context _context, List<ChatInfo> messageBeanList) {
        mContext = _context;
        this.messageBeanList = messageBeanList;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void setdata(ChatInfo messageBean) {
        messageBeanList.add(messageBean);
        notifyItemInserted(messageBeanList.size() - 1);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.chat_item_msg_text_left, viewGroup,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        final ChatInfo chatInfo = messageBeanList.get(position);

        viewHolder.tv_sendtime.setText(chatInfo.getDate());
        viewHolder.nickname.setText(chatInfo.getSellerName());
        viewHolder.tv_chatcontent.setText(chatInfo.getContent());


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
        return messageBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout view_voice_source;
        private TextView tv_sendtime;
        private TextView tv_chatcontent;
        private TextView nickname;
        private SimpleDraweeView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_sendtime = itemView.findViewById(R.id.tv_sendtime);
            tv_chatcontent = itemView.findViewById(R.id.tv_chatcontent);
            nickname = itemView.findViewById(R.id.nickname);
            view_voice_source = itemView.findViewById(R.id.view_voice_source);
        }
    }


}
