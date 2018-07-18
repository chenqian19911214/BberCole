package com.bber.company.android.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bber.company.android.bean.Session;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.ChenQianLog;

import java.util.ArrayList;
import java.util.List;


/**
 * 聊天回话列表的管理
 *
 * @author Administrator
 */
public class SessionDao {
    private SQLiteDatabase db;

    public SessionDao(Context context) {
        db = DBHelper.getInstance(context).getWritableDatabase();
    }

    public SessionDao(Context context, int version) {
        db = DBHelper.getInstance(context).getWritableDatabase();
    }

    // 判断是否包含
    public boolean isContent(String belong, String userid) {

        Cursor cursor = db.query(DBcolumns.TABLE_SESSION, new String[]{"*"}, DBcolumns.SELLER_ID + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{belong, userid}, null, null, null);
        boolean flag = false;
        while (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        return flag;
    }

    // 判断是否包含
    public boolean isContentBuyller(String userid, String from) {

        Cursor cursor = db.query(DBcolumns.TABLE_SESSION, new String[]{"*"}, DBcolumns.SESSION_FROM + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{from, userid}, null, null, null);
        boolean flag = false;
        while (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        return flag;
    }


    // 添加一个会话
    public long insertSession(Session session) {
        if (session.getTo().equals(session.getFrom())) {
            return 0;
        }
        ContentValues values = new ContentValues();
        values.put(DBcolumns.SELLER_ID, session.getSellerId());
        values.put(DBcolumns.SESSION_HEAD, session.getHeadURL());
        values.put(DBcolumns.SESSION_NAME, session.getName());
        values.put(DBcolumns.SESSION_FROM, session.getFrom());
        values.put(DBcolumns.SESSION_TIME, session.getTime());
        values.put(DBcolumns.SESSION_CONTENT, session.getContent());
        values.put(DBcolumns.SESSION_TO, session.getTo());
        values.put(DBcolumns.SESSION_TYPE, session.getType());
        values.put(DBcolumns.SESSION_NOREADCOUNT, session.getNotReadCount());
        values.put(DBcolumns.SESSION_ISDISPOSE, session.getIsdispose());
        long row = db.insert(DBcolumns.TABLE_SESSION, null, values);
        return row;
    }

    // 返回全部列表
    public List<Session> queryAllSessions(String user_id) {
        List<Session> list = new ArrayList<Session>();
          Cursor cursor = db.query(
                  DBcolumns.TABLE_SESSION, //表名
                  new String[]{"*"}, //列名
                  DBcolumns.SESSION_TO + " = ? ",//DBcolumns.SESSION_TO + " = ? order by session_time desc
                  new String[]{user_id},
                  null,
                  null,
                  null);
       // Cursor cursor = db.rawQuery("Select * from " + DBcolumns.TABLE_SESSION, null);
        Session session = null;
        int size = cursor.getCount();
        ChenQianLog.i(size + "");
        while (cursor.moveToNext()) {
            session = new Session();
            String id = "" + cursor.getInt(cursor.getColumnIndex(DBcolumns.SESSION_ID));
            String from = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_FROM));
            String time = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_TIME));
            String content = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_CONTENT));
            String type = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_TYPE));
            String to = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_TO));
            int seller_id = cursor.getInt(cursor.getColumnIndex(DBcolumns.SELLER_ID));
            String head = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_HEAD));
            String name = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_NAME));
            int no_readcount = cursor.getInt(cursor.getColumnIndex(DBcolumns.SESSION_NOREADCOUNT));
            String isdispose = cursor.getString(cursor.getColumnIndex(DBcolumns.SESSION_ISDISPOSE));
            session.setId(id);
            session.setName(name);
            session.setFrom(from);
            session.setTime(time);
            session.setContent(content);
            session.setTo(to);
            session.setSellerId(seller_id);
            session.setHeadURL(head);
            session.setType(type);
            session.setNotReadCount(no_readcount);
            session.setIsdispose(isdispose);
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

        long row = db.update(DBcolumns.TABLE_SESSION, values, DBcolumns.SELLER_ID + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{session.getSellerId().toString(), session.getTo()});
        return row;
    }

    public void updateNoReadCount(String from, String to) {
        ContentValues values = new ContentValues();
        values.put(DBcolumns.SESSION_NOREADCOUNT, 0);
        db.update(DBcolumns.TABLE_SESSION, values, DBcolumns.SELLER_ID + " = ? and " + DBcolumns.SESSION_TO + " = ?", new String[]{from, to});
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
        long row = db.delete(DBcolumns.TABLE_SESSION, DBcolumns.SELLER_ID + "=? and " + DBcolumns.SESSION_TO + "=?", new String[]{fromUser, toUser});
        return row;
    }

}
