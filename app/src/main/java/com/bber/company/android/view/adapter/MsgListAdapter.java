package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.Session;
import com.bber.company.android.tools.Tools;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.ViewHolder> {
    private Activity activity;
    private LayoutInflater mInflater;
    private List<Session> sessions;
    private String[] colors = new String[]{"#f3267e", "#fe7979", "#04edbe", "#6cdeff", "#ffd708", "#ffb108"};
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;

    public MsgListAdapter(Activity activity, List<Session> sessions) {
        this.activity = activity;
        this.sessions = sessions;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateListView(List<Session> list) {
        this.sessions = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_msg, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
//        view.setOnClickListener(this);
//        view.setOnLongClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Session session = sessions.get(position);

        viewHolder.content.setText(session.getContent());
        if (session.getNotReadCount() == 0) {
            viewHolder.msg_num.setVisibility(View.GONE);
        } else {
            viewHolder.msg_num.setVisibility(View.VISIBLE);
            int count = session.getNotReadCount();
            if (count >= 100) {
                viewHolder.msg_num.setText(99 + "");
            } else {
                viewHolder.msg_num.setText(count + "");
            }

        }
        viewHolder.chat_date.setText(session.getTime());

        int index = (int) (Math.random() * colors.length);
//        viewHolder.user_icon.setBorderColor(Color.parseColor(colors[index]));

        RoundingParams roundingParams =
                viewHolder.user_icon.getHierarchy().getRoundingParams();
        roundingParams.setBorder(Color.parseColor(colors[index]), 4);
        roundingParams.setRoundAsCircle(true);
        viewHolder.user_icon.getHierarchy().setRoundingParams(roundingParams);

        String URL = session.getHeadURL();

        if (position == 0) {
            viewHolder.user_icon.setImageResource(R.mipmap.icon_title);
        } else if (position == 1) {
            viewHolder.user_icon.setImageResource(R.drawable.icon_meimei);
        } else {
            viewHolder.user_icon.setImageResource(R.mipmap.user_icon);
            if (!Tools.isEmpty(URL)) {
                viewHolder.user_icon.setTag(URL);
                if (viewHolder.user_icon.getTag() != null
                        && viewHolder.user_icon.getTag().equals(URL)) {
                    Uri uri = Uri.parse(URL);
                    viewHolder.user_icon.setImageURI(uri);
                }
            }
        }

        String name = session.getName();
        if ("10000".equals(session.getFrom())) {
            name = "系统消息";
            GenericDraweeHierarchy hierarchy = viewHolder.user_icon.getHierarchy();
            hierarchy.setPlaceholderImage(R.mipmap.ic_launcher);
        }
        if ("啪啪客服小美".equals(name)) {
            name = "啪啪客服";
        }
        viewHolder.username.setText(name);
        viewHolder.itemView.setTag(session);

        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(viewHolder.itemView, (Session) v.getTag(), pos);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    if (pos != 0 && pos != 1) {
                        mOnItemLongClickListener.onItemLongClick(viewHolder.itemView, (Session) v.getTag(), pos);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Session data, int position);
    }

//    @Override
//    public void onClick(View v) {
//        if (mOnItemClickListener != null) {
//            mOnItemClickListener.onItemClick(v,(Session)v.getTag());
//        }
//    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, Session data, int position);
    }

//    @Override
//    public boolean onLongClick(View view) {
//        if (mOnItemLongClickListener != null) {
//            //注意这里使用getTag方法获取数据
//
////            mOnItemLongClickListener.onItemLongClick(view, (Session) view.getTag());
//        }
//        return false;
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView user_icon;
        public TextView content, msg_num, username, chat_date;

        public ViewHolder(View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            content = itemView.findViewById(R.id.content);
            username = itemView.findViewById(R.id.username);
            msg_num = itemView.findViewById(R.id.msg_num);
            chat_date = itemView.findViewById(R.id.date);
        }
    }
}
