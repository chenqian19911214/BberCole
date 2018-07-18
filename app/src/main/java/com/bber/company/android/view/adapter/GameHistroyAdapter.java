package com.bber.company.android.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.GameHistroyUserBean;
import com.bber.company.android.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 类描述：recycle的定制的adapter
 * 创建人：Vencent
 * 创建时间：2016/10/8 12:17
 *
 * @version v1.0
 */
public class GameHistroyAdapter extends RecyclerView.Adapter<GameHistroyAdapter.MyViewHolder> {

    private Context mContext;
    private List<GameHistroyUserBean> gameHistroyList; // 图片集合
    private OnItemClickLitener mOnItemClickLitener;

    public GameHistroyAdapter(Context _context, List<GameHistroyUserBean> gameHistroyList) {
        mContext = _context;
        this.gameHistroyList = gameHistroyList;
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public void setdata(GameHistroyUserBean messageBean) {
        gameHistroyList.add(messageBean);
        notifyItemInserted(gameHistroyList.size() - 1);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_game_history, viewGroup,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int position) {
        final GameHistroyUserBean gameHistroyUserBean = gameHistroyList.get(position);

        String data = mContext.getResources().getString(R.string.winner_money_);
//        DecimalFormat df = new DecimalFormat(".##");
//        String st = df.format(gameHistroyUserBean.jackpotAmount+gameHistroyUserBean.winAmount);

        String str = String.format(data, gameHistroyUserBean.betNumber, gameHistroyUserBean.userName, StringUtils.doubleChangeIne(gameHistroyUserBean.jackpotAmount + gameHistroyUserBean.winAmount));
        viewHolder.winner_text.setText(str);
        viewHolder.game_number.setText("【游戏局号:" + gameHistroyUserBean.gameNumber.substring(0, 6) + "】");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String strBeginDate = format.format(new Date());
//        viewHolder.dataTime.setText(strBeginDate);
        try {
            Date date = format.parse(gameHistroyUserBean.drawTime);
            String strBeginDate = format.format(date);
//            viewHolder.dataTime.setText(strBeginDate);
            // 获取日期实例
            Calendar calendar = Calendar.getInstance();
            // 将日历设置为指定的时间
            calendar.setTime(date);
            // 获取年
            int year = calendar.get(Calendar.YEAR);

            int month = calendar.get(Calendar.MONTH) + 1;

            int day = calendar.get(Calendar.DAY_OF_MONTH);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            // 这里要注意，月份是从0开始。
            int minute = calendar.get(Calendar.MINUTE);
            // 获取天
            viewHolder.dataTime.setText(month + "-" + day + " " + hour + ":" + minute);
        } catch (ParseException e) {
            e.printStackTrace();
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
        return gameHistroyList.size();
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

        private TextView winner_text;
        private TextView dataTime;
        private TextView game_number;

        public MyViewHolder(View itemView) {
            super(itemView);
            winner_text = itemView.findViewById(R.id.winner_text);
            dataTime = itemView.findViewById(R.id.dataTime);
            game_number = itemView.findViewById(R.id.game_number);
        }
    }


}
