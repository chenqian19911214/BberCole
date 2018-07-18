package com.bber.company.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.tools.JsonUtil;
import com.bber.company.android.tools.Tools;

import java.util.ArrayList;

/**
 * Author: Bruce
 * Date: 2016/5/6
 * Version:
 * Describe:
 */
public class SellerUserDao {

    private DBHelper helper;
    private JsonUtil jsonUtil;

    public SellerUserDao(Context context) {
        helper = new DBHelper(context);
    }

    public SellerUserDao(Context context, int version) {
        helper = new DBHelper(context, version);
    }

    /**
     * 插入一个人
     *
     * @param sellerUser
     */
    public void insert(SellerUserVo sellerUser, String tableName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBcolumns.SELLERUSER_USELLER, sellerUser.getuSeller());
        values.put(DBcolumns.SELLERUSER_USNAME, sellerUser.getUsName());
        values.put(DBcolumns.SELLERUSER_USHEIGHT, sellerUser.getUsHeight());
        values.put(DBcolumns.SELLERUSER_USBRASSIERE, sellerUser.getUsBrassiere());
        values.put(DBcolumns.SELLERUSER_USDESCRIBE, sellerUser.getUsDescribe());
        values.put(DBcolumns.SELLERUSER_USPHONE, sellerUser.getUsPhone());
        values.put(DBcolumns.SELLERUSER_USHEADM, sellerUser.getUsHeadm());
        values.put(DBcolumns.SELLERUSER_USHEADBIG, sellerUser.getUsHeadbig());
        values.put(DBcolumns.SELLERUSER_USSEX, sellerUser.getUsSex());
        values.put(DBcolumns.SELLERUSER_SSSEX, sellerUser.getSsSex());
        values.put(DBcolumns.SELLERUSER_USSTATE, sellerUser.getUsState());
        values.put(DBcolumns.SELLERUSER_USMONEY, sellerUser.getUsMoney());
        values.put(DBcolumns.SELLERUSER_USGRADE, sellerUser.getUsGrade());
        values.put(DBcolumns.SELLERUSER_LEVEL, sellerUser.getLevel());
        values.put(DBcolumns.SELLERUSER_ISACCEPTORDER, sellerUser.getIsAcceptOrder());
        values.put(DBcolumns.SELLERUSER_TIME, sellerUser.getNowTime());
        db.insert(tableName, null, values);
        db.close();
    }

    /**
     * 清空列表中的项目
     */
    public void deleteTableData(String tableName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(tableName, null, null);
        db.close();
    }

    public boolean isExist(String tableName, String useller) {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(tableName, new String[]{"*"}, DBcolumns.SELLERUSER_USELLER + " = ? ", new String[]{useller}, null, null, null);
        boolean flag = false;
        while (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        db.close();
        return flag;
    }

    public int tableItemCount(String tableName) {
        int returnValue = 0;
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + tableName;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            returnValue = cursor.getCount();
        }
        cursor.close();
        db.close();
        return returnValue;
    }

    /**
     * 删除某一条信息
     *
     * @return
     */
    public long deleteTableItemById(String tableName, String useller) {
        SQLiteDatabase db = helper.getWritableDatabase();
        long row = db.delete(tableName, DBcolumns.SELLERUSER_USELLER + "=?", new String[]{useller});
        db.close();
        return row;
    }

    /**
     * 查询列表,每页返回15条,依据id逆序查询，将时间最早的记录添加进list的最前面
     *
     * @return
     */
    public ArrayList<SellerUserVo> queryTableItem(String tableName, int offset, int showItem) {
        ArrayList<SellerUserVo> list = new ArrayList<SellerUserVo>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select * from " + tableName + " order by " + DBcolumns.SELLERUSER_ID + " desc limit ?,?";
        String[] args = new String[]{String.valueOf(offset), "15"};
        Cursor cursor = db.rawQuery(sql, args);
        while (cursor.moveToNext()) {
            SellerUserVo sellerUser = new SellerUserVo();
            sellerUser.setuSeller(cursor.getInt(cursor.getColumnIndex(DBcolumns.SELLERUSER_USELLER)));
            sellerUser.setUsName(cursor.getString(cursor.getColumnIndex(DBcolumns.SELLERUSER_USNAME)));
            sellerUser.setUsHeight(cursor.getInt(cursor.getColumnIndex(DBcolumns.SELLERUSER_USHEIGHT)));
            sellerUser.setUsBrassiere(cursor.getString(cursor.getColumnIndex(DBcolumns.SELLERUSER_USBRASSIERE)));
            sellerUser.setUsDescribe(cursor.getString(cursor.getColumnIndex(DBcolumns.SELLERUSER_USDESCRIBE)));
            sellerUser.setUsPhone(cursor.getString(cursor.getColumnIndex(DBcolumns.SELLERUSER_USPHONE)));
            sellerUser.setUsHeadm(cursor.getString(cursor.getColumnIndex(DBcolumns.SELLERUSER_USHEADM)));
            sellerUser.setUsHeadbig(cursor.getString(cursor.getColumnIndex(DBcolumns.SELLERUSER_USHEADBIG)));
            sellerUser.setUsSex(cursor.getInt(cursor.getColumnIndex(DBcolumns.SELLERUSER_USSEX)));
            sellerUser.setSsSex(cursor.getInt(cursor.getColumnIndex(DBcolumns.SELLERUSER_SSSEX)));
            sellerUser.setUsState(cursor.getInt(cursor.getColumnIndex(DBcolumns.SELLERUSER_USSTATE)));
            sellerUser.setUsMoney(Tools.StringToDouble(cursor.getString(cursor.getColumnIndex(DBcolumns.SELLERUSER_USMONEY))));
            sellerUser.setUsGrade(Tools.StringToFloat(cursor.getString(cursor.getColumnIndex(DBcolumns.SELLERUSER_USGRADE))));
            sellerUser.setLevel(cursor.getInt(cursor.getColumnIndex(DBcolumns.SELLERUSER_LEVEL)));
            sellerUser.setIsAcceptOrder(cursor.getInt(cursor.getColumnIndex(DBcolumns.SELLERUSER_ISACCEPTORDER)));
            sellerUser.setNowTime(cursor.getString(cursor.getColumnIndex(DBcolumns.SELLERUSER_TIME)));
            list.add(sellerUser);
            showItem--;
            if (showItem <= 0) break;
        }
        cursor.close();
        db.close();
        return list;
    }

}
