package com.bber.company.android.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bber.company.android.bean.Session;
import com.bber.company.android.bean.livebroadcast.BoradcastMessageBean;
import com.bber.company.android.tools.Tools;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;


/**
 * 存储 主播的聊天记录
 *
 * @author Administrator
 */
public class BroadcastBrowseDao {
    private SQLiteDatabase db;

    public BroadcastBrowseDao(Context context) {
        db = DBHelper.getInstance(context).getWritableDatabase();
    }


    // 判断是否包含
    public boolean isContent(String belong, String userid) {

        Cursor cursor = db.query(DBcolumns.TABLE_BROADCASD, new String[]{"*"}, DBcolumns.SELLER_ID + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{belong, userid}, null, null, null);
        boolean flag = false;
        while (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        return flag;
    }

    // 判断是否包含
    public boolean isContentBuyller(String userid, String from) {

        Cursor cursor = db.query(DBcolumns.TABLE_BROADCASD, new String[]{"*"}, DBcolumns.SESSION_FROM + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{from, userid}, null, null, null);
        boolean flag = false;
        while (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        return flag;
    }


    // 添加一个会话
    public long insertSession(BoradcastMessageBean session) {
      /*  if (session.getTo().equals(session.getFrom())) {
            return 0;
        }*/
        ContentValues values = new ContentValues();

        values.put(DBcolumns.BROADCAST_ID,session.getUserid());
        values.put(DBcolumns.BROADCAST_HEADM,session.geticonuri());
        values.put(DBcolumns.BROADCAST_MAG,session.getMessage());
        values.put(DBcolumns.BROADCAST_SELECT,session.isBeSelf());
        values.put(DBcolumns.BROADCAST_TIME,session.getTime());

        long row = db.insert(DBcolumns.TABLE_BROADCASD, null, values);
        return row;
    }

    // 返回全部列表
    public List<BoradcastMessageBean> queryAllSessions(String user_id) {
        List<BoradcastMessageBean> list = new ArrayList<>(); //order by session_time desc
        Cursor cursor = db.query(DBcolumns.TABLE_BROADCASD, new String[]{"*"}, DBcolumns.BROADCAST_ID + " = ? ", new String[]{user_id}, null, null, null);
        //Cursor cursor = db.rawQuery("Select * from  "+DBcolumns.TABLE_BROADCASD ,null);
        BoradcastMessageBean session = null;
        while (cursor.moveToNext()) {

          /*  values.put(DBcolumns.BROADCAST_ID,session.getUserid());
            values.put(DBcolumns.BROADCAST_HEADM,session.geticonuri());
            values.put(DBcolumns.BROADCAST_MAG,session.getMessage());
            values.put(DBcolumns.BROADCAST_SELECT,session.isBeSelf());*/

            session = new BoradcastMessageBean();
            String userId = "" + cursor.getString(cursor.getColumnIndex(DBcolumns.BROADCAST_ID));
            String icon = cursor.getString(cursor.getColumnIndex(DBcolumns.BROADCAST_HEADM));
            String mag = cursor.getString(cursor.getColumnIndex(DBcolumns.BROADCAST_MAG));
            String time = cursor.getString(cursor.getColumnIndex(DBcolumns.BROADCAST_TIME));
            String select = cursor.getString(cursor.getColumnIndex(DBcolumns.BROADCAST_SELECT));

            session.setTime(time);
            session.setTime(userId);
            session.setTime(icon);
            session.setTime(mag);
            session.setTime(select);

            list.add(session);
        }
        cursor.close();
        return list;
    }

    // 修改一个回话
    public long updateSession(Session session) {
        ContentValues values = new ContentValues();
        if (session.getSellerId() != null && session.getSellerId() != -1 && session.getSellerId() != 0) {
            values.put(DBcolumns.SELLER_ID, session.getSellerId());
        }
        values.put(DBcolumns.SESSION_TYPE, session.getType());
        values.put(DBcolumns.SESSION_TIME, session.getTime());
        values.put(DBcolumns.SESSION_CONTENT, session.getContent());
        if (!Tools.isEmpty(session.getHeadURL())) {
            values.put(DBcolumns.SESSION_HEAD, session.getHeadURL());
        }
        if (!Tools.isEmpty(session.getName())) {
            values.put(DBcolumns.SESSION_NAME, session.getName());
        }


        Cursor countcursor = db.rawQuery("select " + DBcolumns.SESSION_NOREADCOUNT + " from " +
                DBcolumns.TABLE_SESSION + " where " + DBcolumns.SELLER_ID + " = ?   and " + DBcolumns.SESSION_TO + " = ?", new String[]{session.getSellerId().toString(), session.getTo()});
        int count = 0;
        if (countcursor.moveToFirst()) {
            count = countcursor.getInt(0);
        }
        countcursor.close();
        values.put(DBcolumns.SESSION_NOREADCOUNT, count + session.getNotReadCount());

        long row = db.update(DBcolumns.TABLE_BROADCASD, values, DBcolumns.SELLER_ID + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{session.getSellerId().toString(), session.getTo()});
        return row;
    }

    public void updateNoReadCount(String from, String to) {
        ContentValues values = new ContentValues();
        values.put(DBcolumns.SESSION_NOREADCOUNT, 0);
        db.update(DBcolumns.TABLE_BROADCASD, values, DBcolumns.SELLER_ID + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{from, to});
    }

    /**
     * 查询有无未读对话
     **/

    public boolean hasNoRead() {
        Cursor countcursor = db.rawQuery("select " + DBcolumns.SESSION_NOREADCOUNT + " from " +
                DBcolumns.TABLE_SESSION + " where " + DBcolumns.SESSION_NOREADCOUNT + ">0", null);
        if (countcursor.getCount() > 0) {
            countcursor.close();
            return true;
        }
        return false;
    }

    // 删除一个回话
    public long deleteSession(String fromUser, String toUser) {
        long row = db.delete(DBcolumns.TABLE_BROADCASD, DBcolumns.SELLER_ID + "=? and " + DBcolumns.SESSION_TO + "=?", new String[]{fromUser, toUser});
        return row;
    }

}
