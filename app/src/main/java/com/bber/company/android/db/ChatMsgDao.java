package com.bber.company.android.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bber.company.android.bean.ChatInfo;
import com.bber.company.android.tools.JsonUtil;

import java.util.ArrayList;

public class ChatMsgDao {
    private DBHelper helper;
    private JsonUtil jsonUtil;

    public ChatMsgDao(Context context) {
        helper = new DBHelper(context);
        jsonUtil = new JsonUtil(context);
    }

    public ChatMsgDao(Context context, int version) {
        helper = new DBHelper(context, version);
    }

    /**
     * 添加新信息
     *
     * @param chatInfo
     */
    public void insert(ChatInfo chatInfo) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBcolumns.FROM_ID, chatInfo.getFromUser());
        values.put(DBcolumns.SELLER_ID, chatInfo.getSellerId());
        values.put(DBcolumns.TO_ID, chatInfo.getToUser());
        values.put(DBcolumns.MSG, jsonUtil.chatInfoToString(chatInfo));
        values.put(DBcolumns.ORDER_ID, chatInfo.getOrderID());
        db.insert(DBcolumns.TABLE_MSG, null, values);
        db.close();
    }


    /**
     * 清空所有聊天记录
     */
    public void deleteTableData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DBcolumns.TABLE_MSG, null, null);
        db.close();
    }


    /**
     * 根据msgid，删除对应聊天记录
     *
     * @return
     */
    public long deleteMsgById(String sellerId, String toUser) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long row = db.delete(DBcolumns.TABLE_MSG, DBcolumns.SELLER_ID + " = ? and " + DBcolumns.TO_ID + "=?", new String[]{sellerId, toUser});
        db.close();
        return row;
    }

    /**
     * 根据msgid，删除对应聊天记录
     *
     * @return
     */
    public long deleteMsgByMsgId(String sellerId, String toUser, String MsgId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long row = db.delete(DBcolumns.TABLE_MSG, DBcolumns.SELLER_ID + " = ? and "
                + DBcolumns.TO_ID + " = ? and " + DBcolumns.MSG_ID + "=?", new String[]{sellerId, toUser, MsgId});
        db.close();
        return row;
    }

    /**
     * 查询列表,每页返回15条,依据id逆序查询，将时间最早的记录添加进list的最前面
     *
     * @return
     */
    public ArrayList<ChatInfo> queryMsg(String sellerId, String to, int offset) {
        ArrayList<ChatInfo> list = new ArrayList<ChatInfo>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBcolumns.TABLE_MSG + " where " + DBcolumns.SELLER_ID + "=? and " + DBcolumns.TO_ID + "=? order by " + DBcolumns.MSG_ID + " desc limit ?,?";
        String[] args = new String[]{sellerId, to, String.valueOf(offset), "10"};
        Cursor cursor = db.rawQuery(sql, args);
        ChatInfo chatInfo = null;
        while (cursor.moveToNext()) {
            String msg = cursor.getString(cursor.getColumnIndex(DBcolumns.MSG));
            chatInfo = jsonUtil.jsonToChatInfo(msg);
            chatInfo.setMsgID(cursor.getString(cursor.getColumnIndex(DBcolumns.MSG_ID)));
            list.add(0, chatInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 查询最新一条记录
     *
     * @return
     */
    public ChatInfo queryTheLastMsg() {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + DBcolumns.TABLE_MSG + " order by " + DBcolumns.MSG_ID + " desc limit 1";
        String[] args = new String[]{};
        Cursor cursor = db.rawQuery(sql, args);

        ChatInfo chatInfo = null;
        while (cursor.moveToNext()) {
            String msg = cursor.getString(cursor.getColumnIndex(DBcolumns.MSG));
            chatInfo = jsonUtil.jsonToChatInfo(msg);
        }
        cursor.close();
        db.close();
        return chatInfo;
    }


    /**
     * 查询最新一条记录的id
     *
     * @return
     */
//    public int queryTheLastMsgId() {
//        SQLiteDatabase db = helper.getWritableDatabase();
//        String sql = "select " + DBcolumns.MSG_ID + " from " + DBcolumns.TABLE_MSG + " order by " + DBcolumns.MSG_ID + " desc limit 1";
//        String[] args = new String[]{};
//        Cursor cursor = db.rawQuery(sql, args);
//        int id = -1;
//        if (cursor.moveToNext()) {
//            id = cursor.getInt(cursor.getColumnIndex(DBcolumns.MSG_ID));
//        }
//        cursor.close();
//        db.close();
//        return id;
//    }

}
