package com.bber.company.android.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.MessageBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.widget.RecordButton;
import com.bber.customview.utils.LogUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * 类描述：recycle的定制的adapter
 * 创建人：Vencent
 * 创建时间：2016/10/8 12:17
 *
 * @version v1.0
 */
public class ChatImageItemAdapter extends RecyclerView.Adapter<ChatImageItemAdapter.MyViewHolder> {

    //长按和短按的监听事件
    //语音操作对象
    public MediaPlayer mPlayer = null;
    public String fontcolor = "";
    public String[] keywords;
    private Context mContext;
    private List<MessageBean> messageBeanList; // 图片集合
    private RecordButton recordButton;
    private AnimationDrawable animationDrawable;
    private OnItemClickLitener mOnItemClickLitener;

    public ChatImageItemAdapter(Context _context, List<MessageBean> messageBeanList, String fontcolor, String[] keywords) {
        this.fontcolor = fontcolor;
        this.keywords = keywords;
        mContext = _context;
        this.messageBeanList = messageBeanList;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void setdata(MessageBean messageBean) {
        messageBeanList.add(messageBean);
        notifyItemInserted(messageBeanList.size() - 1);
    }

    //录音
    public void setRecoreButton(RecordButton recordButton) {
        this.recordButton = recordButton;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.adapter_group_chat, viewGroup,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int i) {
        final MessageBean messageBean = messageBeanList.get(i);

//        if (!Tools.isEmpty(messageBean.fromUserHead)) {
//            viewHolder.imageView.setAspectRatio(0.0f);
//            Glide.with(mContext).load(messageBean.fromUserHead).into(viewHolder.imageView);
//        }

        //载入标题资源
        viewHolder.textView.setText(messageBean.fromUserName + ":");
        if (!fontcolor.equals("")) {
            viewHolder.right_data.setTextColor(Color.parseColor(fontcolor));
        } else {
            viewHolder.right_data.setTextColor(R.color.black);
        }

        viewHolder.imageView.setAspectRatio(0.0f);
        Uri uri = Uri.parse(messageBean.fromUserHead);
        viewHolder.imageView.setImageURI(uri);

        int nowVipId = (int) SharedPreferencesUtils.get(mContext, Constants.VIP_ID, 0);
        //VIP等级
        if (messageBean.viplevel != 0) {
            if (messageBean.viplevel == 1) {
                viewHolder.viplevel_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_base_vip));
                viewHolder.viplevel_image.setVisibility(View.VISIBLE);
            } else if (nowVipId >= 2 && nowVipId < 5) {
                viewHolder.viplevel_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_glod_vip));
                viewHolder.viplevel_image.setVisibility(View.VISIBLE);
            } else if (nowVipId == 5) {
                viewHolder.viplevel_image.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_long_glod));
                viewHolder.viplevel_image.setVisibility(View.VISIBLE);
            }
        }
        //载入标题资源
        viewHolder.textView.setText(messageBean.fromUserName + ":");
        if (messageBean.gender == 2) {
            viewHolder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.vip_normal));
        } else {
            viewHolder.textView.setTextColor(ContextCompat.getColor(mContext, R.color.blue_img));
        }

        //判断不同的消息来判别不同的类型
        switch (messageBean.msgType) {
            case Constants.MSG_TYPE_TEXT:
                String chatMsg = messageBean.msgContent;
                if (!keywords.equals("[]")) {
                    for (int y = 0; y < keywords.length; y++) {
                        if (messageBean.msgContent.contains(keywords[y])) {
                            chatMsg = messageBean.msgContent.replace(keywords[y].trim().toString(), replaceLength(keywords[y]));
                        }
                    }
                }
                viewHolder.right_data.setText(chatMsg);
                break;
            case Constants.MSG_TYPE_VOICE:
                viewHolder.view_voice_source.setVisibility(View.VISIBLE);
                viewHolder.right_data.setVisibility(View.GONE);
                viewHolder.voice_time.setText(messageBean.voiceTime + "''");

                viewHolder.view_voice_source.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer.release();
                            mPlayer = null;

                        } else {
                            mPlayer = new MediaPlayer();
                            recordButton.play(mPlayer, messageBean.sourcePath);
                            LogUtils.e("语音地址" + messageBean.sourcePath);
                            animationDrawable = (AnimationDrawable) viewHolder.animationIV.getDrawable();
                            animationDrawable.start();
                            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    animationDrawable.stop();
                                    animationDrawable.selectDrawable(0);
                                }
                            });
                        }
                    }
                });

                break;
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

    //替换符号是几个*号
    private String replaceLength(String str) {
        if (str.length() < 2) {
            return "*";
        } else {
            return "**";
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
        private TextView textView;
        private TextView right_data;
        private TextView voice_time;
        private SimpleDraweeView imageView;
        private ImageView viplevel_image, animationIV;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.user_image);
            viplevel_image = itemView.findViewById(R.id.vip_level);
            animationIV = itemView.findViewById(R.id.animationIV);
            textView = itemView.findViewById(R.id.text_name);
            right_data = itemView.findViewById(R.id.right_data);
            voice_time = itemView.findViewById(R.id.voice_time);
            view_voice_source = itemView.findViewById(R.id.view_voice_source);
        }
    }


}
