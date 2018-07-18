package com.bber.company.android.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bber.company.android.bean.cityBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CityDao {
    public static final String ADD_CITY = "arsfc_geo_city";
    public static final String ADD_PROVINCE = "arsfc_geo_province";
    public static final String ADD_DISTRICT = "arsfc_geo_district";
    private static final String CITY_DB_NAME = "city.db";
    private SQLiteDatabase db;
    private AssetsDatabaseManager mg;

    public CityDao(Context context) {
        AssetsDatabaseManager.initManager(context);
        mg = AssetsDatabaseManager.getManager();
        if (db != null) {
            db.close();
        }
        db = mg.getDatabase(CITY_DB_NAME);
    }

    /**
     * 关闭db
     */
    public void closeDB() {
        db.close();
        AssetsDatabaseManager.closeAllDatabase();
    }

    /**
     * 通过表名，获取相关的城市信息
     *
     * @return
     */
    public List<cityBean> getAllCity() {
        List<cityBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * from " + ADD_CITY, null);
        while (cursor.moveToNext()) {
            cityBean item = new cityBean();
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setAdcode(cursor.getInt(cursor.getColumnIndex("adcode")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));
            item.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
            item.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
            item.setParentAdcode(cursor.getInt(cursor.getColumnIndex("parentAdcode")));
            item.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            item.setPinyin(cursor.getString(cursor.getColumnIndex("pinyin")));
            list.add(item);
        }
        cursor.close();
        Collections.sort(list, new CityComparator());
        return list;
    }

    /**
     * 通过表名，获取相关的城市信息
     *
     * @param tableName
     * @return
     */
    public List<cityBean> getAddName(String tableName) {
        List<cityBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * from " + tableName, null);
        while (cursor.moveToNext()) {
            cityBean item = new cityBean();
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setAdcode(cursor.getInt(cursor.getColumnIndex("adcode")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));
            item.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
            item.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
            item.setParentAdcode(cursor.getInt(cursor.getColumnIndex("parentAdcode")));
            item.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            item.setPinyin(cursor.getString(cursor.getColumnIndex("pinyin")));
            list.add(item);
        }
        cursor.close();
        return list;
    }


    /**
     * 通过表名，获取相关的城市信息
     *
     * @param parentAdcode
     * @return
     */
    public List<cityBean> getPrivinceCode(String parentAdcode) {
        List<cityBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * from " + ADD_PROVINCE + " where adcode =?", new String[]{parentAdcode});
        while (cursor.moveToNext()) {
            cityBean item = new cityBean();
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setAdcode(cursor.getInt(cursor.getColumnIndex("adcode")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));
            item.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
            item.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
            item.setParentAdcode(cursor.getInt(cursor.getColumnIndex("parentAdcode")));
            item.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            item.setPinyin(cursor.getString(cursor.getColumnIndex("pinyin")));
            list.add(item);
        }
        cursor.close();
        return list;
    }

    /**
     * 通过名字或者拼音搜索
     *
     * @param keyword
     * @return
     */
    public List<cityBean> searchCity(final String keyword) {
        Cursor cursor = db.rawQuery("select * from " + ADD_CITY + " where name like \"%" + keyword
                + "%\"", null);
        List<cityBean> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            cityBean item = new cityBean();
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setAdcode(cursor.getInt(cursor.getColumnIndex("adcode")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));
            item.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
            item.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
            item.setParentAdcode(cursor.getInt(cursor.getColumnIndex("parentAdcode")));
            item.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            item.setPinyin(cursor.getString(cursor.getColumnIndex("pinyin")));
            result.add(item);
        }
        cursor.close();
        return result;
    }

    /**
     * 通过名字或者拼音搜索
     *
     * @param keyword
     * @return
     */
    public List<cityBean> searchDistrict(final String keyword) {
        Cursor cursor = db.rawQuery("select * from " + ADD_DISTRICT + " where name like \"%" + keyword
                + "%\"", null);
        List<cityBean> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            cityBean item = new cityBean();
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setAdcode(cursor.getInt(cursor.getColumnIndex("adcode")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));
            item.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
            item.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
            item.setParentAdcode(cursor.getInt(cursor.getColumnIndex("parentAdcode")));
            item.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            item.setPinyin(cursor.getString(cursor.getColumnIndex("pinyin")));
            result.add(item);
        }
        cursor.close();
        return result;
    }


    /**
     * 获取城市下面所有区的名字
     *
     * @param cityCode
     * @return
     */
    public List<cityBean> getCityDistrict(String cityCode) {
        List<cityBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * from " + ADD_DISTRICT + " where citycode=?", new String[]{cityCode});
        while (cursor.moveToNext()) {
            cityBean item = new cityBean();
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setAdcode(cursor.getInt(cursor.getColumnIndex("adcode")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));
            item.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
            item.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
            item.setParentAdcode(cursor.getInt(cursor.getColumnIndex("parentAdcode")));
            item.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            item.setPinyin(cursor.getString(cursor.getColumnIndex("pinyin")));
            list.add(item);
        }
        cursor.close();
        return list;
    }

    /**
     * 获取省下面所有城市的名字
     *
     * @param provinceCode
     * @return
     */
    public List<cityBean> getProvinceCity(String provinceCode) {
        List<cityBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * from " + ADD_PROVINCE + " where citycode = ?", new String[]{provinceCode});
        while (cursor.moveToNext()) {
            cityBean item = new cityBean();
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setAdcode(cursor.getInt(cursor.getColumnIndex("adcode")));
            item.setName(cursor.getString(cursor.getColumnIndex("name")));
            item.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
            item.setLng(cursor.getDouble(cursor.getColumnIndex("lng")));
            item.setParentAdcode(cursor.getInt(cursor.getColumnIndex("parentAdcode")));
            item.setCitycode(cursor.getString(cursor.getColumnIndex("citycode")));
            item.setPinyin(cursor.getString(cursor.getColumnIndex("pinyin")));
            list.add(item);
        }
        cursor.close();
        return list;
    }

    /**
     * a-z排序
     */
    private class CityComparator implements Comparator<cityBean> {
        @Override
        public int compare(cityBean lhs, cityBean rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            return a.compareTo(b);
        }
    }
}
