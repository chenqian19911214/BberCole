package com.bber.company.android.view.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bber.company.android.R;
import com.bber.company.android.util.Bimp;


/**
 * 类描述：recycle的定制的adapter
 * 创建人：Vencent
 * 创建时间：2016/10/8 12:17
 *
 * @version v1.0
 */
public class ChatCreatAdapter extends RecyclerView.Adapter<ChatCreatAdapter.MyViewHolder> {

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private Context mContext;

    public ChatCreatAdapter(Context _context) {
        mContext = _context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_recycle_gridview, viewGroup,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {

        if (position == Bimp.tempSelectBitmap.size()) {
            viewHolder.image.setImageBitmap(BitmapFactory.decodeResource(
                    mContext.getResources(), R.mipmap.icon_addpic_focused));
            if (position == 4) {
                viewHolder.image.setVisibility(View.GONE);
            }
        } else {
            viewHolder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
        }
    }

    @Override
    public int getItemCount() {
        if (Bimp.tempSelectBitmap.size() == 3) {
            return 3;
        }
        return (Bimp.tempSelectBitmap.size() + 1);
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public void loading() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        break;
                    } else {
                        Bimp.max += 1;
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView1);
        }
    }

}
