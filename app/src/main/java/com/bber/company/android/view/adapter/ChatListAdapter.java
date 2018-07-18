package com.bber.company.android.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.ChatInfo;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.view.activity.BrowseImageActivity;
import com.bber.company.android.view.activity.GirlProfileActivity;
import com.bber.company.android.view.activity.MaplocationActivity;
import com.bber.company.android.widget.RecordButton;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.List;


/**
 * Created by Administrator on 2015-05-22.
 */
public class ChatListAdapter extends BaseAdapter implements View.OnLongClickListener {
    //语音操作对象
    public MediaPlayer mPlayer = null;
    private Activity mContext;
    private LayoutInflater mInflater;
    private List<ChatInfo> chatInfos;
    private RecordButton recordButton;
    private ViewHolder holder = null;
    ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(
                String id,
                @Nullable ImageInfo imageInfo,
                @Nullable Animatable anim) {
            if (imageInfo == null) {
                return;
            }
            final int width = imageInfo.getWidth();
            final int height = imageInfo.getHeight();
            ViewGroup.LayoutParams params = holder.image.getLayoutParams();
            params.width = width;
            params.height = height;
            if (width > MyApplication.screenWidth / 2) {//图片宽度大于屏幕宽度则最多显示屏幕的一半
                params.width = MyApplication.screenWidth / 2;
                params.height = height / (width / params.width);
            } else if (height > MyApplication.screenWidth / 2) {
                params.height = MyApplication.screenWidth / 2;
                params.width = width / (height / params.height);
            }
            holder.image.setLayoutParams(params);
        }

        @Override
        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            Log.e("eeeeeeeeeeeeeeeeeee", "onIntermediateImageSet");
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            Log.e("eeeeeeeeeeeeeeeeeee", "onFailure");
        }
    };
    private OnRecyclerViewItemLongClickListener mOnItemLongClickListener = null;


    public ChatListAdapter(Activity context, List<ChatInfo> infos) {
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        chatInfos = infos;
    }

    public void updateListView(List<ChatInfo> infos) {
        chatInfos = infos;
        notifyDataSetChanged();
    }

    public void setRecoreButton(RecordButton recordButton) {
        this.recordButton = recordButton;
    }

    @Override
    public int getCount() {
        return chatInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ChatInfo chatInfo = chatInfos.get(position);
        if (convertView == null || (holder = (ViewHolder) convertView.getTag()).flag != chatInfo.getIsComing()) {
            holder = new ViewHolder();
            if (chatInfo.getIsComing() == ChatInfo.MESSAGE_FROM) {
                holder.flag = ChatInfo.MESSAGE_FROM;
                convertView = mInflater.inflate(
                        R.layout.chat_item_msg_text_left, null);
            } else {
                holder.flag = ChatInfo.MESSAGE_TO;
                convertView = mInflater.inflate(
                        R.layout.chat_item_msg_text_right, null);
            }
            holder.chet_item_msg = convertView.findViewById(R.id.chet_item_msg);
            holder.view_chat = convertView.findViewById(R.id.view_chat);
            holder.image = convertView.findViewById(R.id.image);
            holder.tv_sendtime = convertView.findViewById(R.id.tv_sendtime);
            holder.tv_chatcontent = convertView.findViewById(R.id.tv_chatcontent);
            holder.view_voice_source = convertView.findViewById(R.id.view_voice_source);
            holder.voice_time = convertView.findViewById(R.id.voice_time);
            holder.tv_address = convertView.findViewById(R.id.address);
            holder.mapView = convertView.findViewById(R.id.mapview);
            holder.view_card = convertView.findViewById(R.id.view_card);
            holder.view_map = convertView.findViewById(R.id.view_map);
            holder.user_icon = convertView.findViewById(R.id.user_icon);
            holder.nickname = convertView.findViewById(R.id.nickname);
            holder.tv_tip = convertView.findViewById(R.id.tv_tip);
            convertView.setTag(holder);
            holder.chet_item_msg.setOnLongClickListener(this);
        }

        holder.position = position;
        holder.tv_sendtime.setText(chatInfo.getDate());
        holder.tv_tip.setVisibility(View.GONE);

        reset();
        switch (chatInfo.getMsg_type()) {
            case Constants.MSG_TYPE_CARD:
                //加载名片
                holder.view_card.setVisibility(View.VISIBLE);
                holder.nickname.setText(chatInfo.getSellerName());
                holder.user_icon.setImageResource(R.mipmap.user_icon);
                if (!Tools.isEmpty(chatInfo.getSellerHead())) {
                    holder.user_icon.setTag(chatInfo.getSellerHead());
                    //通过tag来防止图片错位
                    if (holder.user_icon.getTag() != null
                            && holder.user_icon.getTag().equals(chatInfo.getSellerHead())) {
                        Uri uri = Uri.parse(chatInfo.getSellerHead());
                        holder.user_icon.setImageURI(uri);
                    }
                }
                holder.view_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, GirlProfileActivity.class);
                        intent.putExtra("from", 1);
                        intent.putExtra("sellerId", chatInfo.getSellerId());
                        mContext.startActivity(intent);
                    }
                });

                break;
            case Constants.MSG_TYPE_TEXT:
                holder.tv_chatcontent.setText(chatInfo.getContent() + "");
                holder.tv_chatcontent.setVisibility(View.VISIBLE);
                holder.view_map.setVisibility(View.GONE);
                break;
            case Constants.MSG_TYPE_IMG:
                holder.view_chat.setVisibility(View.GONE);
                //加载图片
                final String path = chatInfo.getContent();
                Log.e("eeeeeeeeeeee", "path:" + path);
                if (!Tools.isEmpty(path)) {
                    holder.image.setVisibility(View.VISIBLE);
                    holder.image.setTag(path);
                    //预设图片
                    //通过tag来防止图片错位
                    if (holder.image.getTag() != null
                            && holder.image.getTag().equals(path)) {
//                        holder.image.setAspectRatio(0.5F);
                        Uri uri = Uri.parse(path);
                        holder.image.setImageURI(uri);

                        final int width = chatInfo.getWidth();
                        final int height = chatInfo.getHeight();
                        Log.e("eeeeeeeeeeee", "width:" + width + "---height:" + height);
                        ViewGroup.LayoutParams params = holder.image.getLayoutParams();
                        params.width = width;
                        params.height = height;

                        if ((width > (MyApplication.screenWidth / 2) - 100) || (height > (MyApplication.screenWidth / 2) - 100)) {//图片宽度大于屏幕宽度则最多显示屏幕的一半
                            if (width > height) {
                                params.width = (MyApplication.screenWidth / 2) - 100;
                                params.height = (int) (height / (width / (float) params.width));
                            } else {
                                params.height = (MyApplication.screenWidth / 2) - 100;
                                params.width = (int) (width / (height / (float) params.height));
                            }
                        }
                        holder.image.setLayoutParams(params);
                        holder.image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String[] images = new String[]{path};
                                Intent intent = new Intent(mContext, BrowseImageActivity.class);
                                intent.putExtra("images", images);
                                intent.putExtra("currentIndex", 0);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                    holder.image.setTag(holder);
                    holder.image.setOnLongClickListener(this);
                }
                break;
            case Constants.MSG_TYPE_VOICE:
                if (chatInfo.getVoicePath() != null) {
                    holder.tv_chatcontent.setVisibility(View.GONE);
                    holder.view_chat.setClickable(true);
                    holder.view_voice_source.setVisibility(View.VISIBLE);
                    holder.voice_time.setText(chatInfo.getVoiceTime() + "''");
                    holder.view_chat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mPlayer != null && mPlayer.isPlaying()) {
                                mPlayer.stop();
                                mPlayer.release();
                                mPlayer = null;
                            } else {
                                mPlayer = new MediaPlayer();
                                recordButton.play(mPlayer, chatInfo.getVoicePath());
                            }
                        }
                    });
                }
                holder.view_chat.setTag(holder);
                holder.view_chat.setOnLongClickListener(this);
                break;
            case Constants.MSG_TYPE_ORDER:
                break;
            case Constants.SYSTEM_TIP:
                holder.view_chat.setVisibility(View.GONE);
                holder.tv_tip.setVisibility(View.VISIBLE);
                holder.tv_tip.setText(chatInfo.getContent());
                break;
            case Constants.MSG_TYPE_LOCATION:
                holder.view_map.setVisibility(View.VISIBLE);
                holder.tv_address.setText(chatInfo.getContent());
                holder.view_map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentMap = new Intent(mContext, MaplocationActivity.class);
                        intentMap.putExtra("showType", 0);
                        intentMap.putExtra("mapbean", chatInfo.getMapBean());
                        mContext.startActivity(intentMap);
                    }
                });
                holder.view_map.setTag(holder);
                holder.view_map.setOnLongClickListener(this);
                break;
        }

        return convertView;
    }

    private void reset() {
        holder.tv_chatcontent.setVisibility(View.GONE);
        holder.view_card.setVisibility(View.GONE);
        holder.view_chat.setVisibility(View.VISIBLE);
        holder.view_map.setVisibility(View.GONE);
        holder.image.setVisibility(View.GONE);
        holder.view_voice_source.setVisibility(View.GONE);
        holder.view_chat.setClickable(false);
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemLongClickListener != null) {
            ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.position;
            if (position >= 0) {
                mOnItemLongClickListener.onItemLongClick(view, position);
            }
        }
        return false;
    }

    public void setOnItemLongClickListener(OnRecyclerViewItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public interface OnRecyclerViewItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    private static class ViewHolder {
        public TextView nickname, tv_sendtime, tv_chatcontent, voice_time, tv_address, tv_tip;
        public LinearLayout view_voice_source;
        public RelativeLayout view_chat;
        public ImageView mapView;
        public LinearLayout chet_item_msg;
        public LinearLayout view_map;
        public int flag;
        public int position;
        private LinearLayout view_card;
        private SimpleDraweeView image, user_icon;

    }
}
