package com.bber.company.android.util.country;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashSet;
import java.util.Set;

import io.netty.util.internal.StringUtil;

/**
 * Created by carlo.c on 2018/4/17.
 */

class SharePreferenceUtil {
    // 数据信息文件名
    private static String DATA_INFO_SHAREDPREFERENCE = "data";
    // sington
    private static SharePreferenceUtil builder;
    //SharedPreferences Editor对象
    private static SharedPreferences.Editor editor;
    //SharedPreferences对象
    private static SharedPreferences mPre;
    // 请求返回缓存list
    private static String SP_LOCAL_CACHE = "sp_local_cache";

    /**
     * 构造函数
     * @author leibing
     * @createTime 2016/08/26
     * @lastModify 2016/08/26
     * @param
     * @return
     */
    private SharePreferenceUtil(){
    }

    /**
     * sington 获取数据工具对象
     * @author leibing
     * @createTime 2016/08/26
     * @lastModify 2016/08/26
     * @param context 上下文
     * @return
     */
    public static SharePreferenceUtil getInstance(Context context) {
        return getInstance(context, DATA_INFO_SHAREDPREFERENCE);
    }

    /**
     * sington 获取数据工具对象
     * @author leibing
     * @createTime 2016/08/26
     * @lastModify 2016/08/26
     * @param context 上下文
     * @param fileName 保存到指定的文件的名称
     * @return
     */
    public static SharePreferenceUtil getInstance(Context context, String fileName) {
        if (builder == null) {
            builder = new SharePreferenceUtil();
        }
        // 如果传入的文件名称为空，这使用默认文件名
        if (fileName.equals("")||fileName==null) {
            fileName = DATA_INFO_SHAREDPREFERENCE;
        }
        mPre = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        editor = mPre.edit();

        return builder;
    }

    /**
     * 清除文件名下所有的数据
     * @author leibing
     * @createTime 2016/08/26
     * @lastModify 2016/08/26
     * @param mContext 上下文
     * @return
     */
    public void clearData(Context mContext) {
        try {
            SpLocalCache.clear(mContext, getSpLocalCache());
            if (editor != null)
                editor.clear().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 存储一个SpLocalCache(名称)，以便于用户缓存的统一管理
     * @author leibing
     * @createTime 2016/08/26
     * @lastModify 2016/08/26
     * @param cacheName 缓存名
     * @return
     */
    public void setSpLocalCache(String cacheName){
        try {
            Set<String> cacheSet = getSpLocalCache();
            cacheSet.add(cacheName);
            editor.putStringSet(SP_LOCAL_CACHE, cacheSet);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取所有的SpLocalCache(名称)
     * @author leibing
     * @createTime 2016/08/26
     * @lastModify 2016/08/26
     * @return
     */
    public Set<String> getSpLocalCache(){
        Set<String> result;
        result = mPre.getStringSet(SP_LOCAL_CACHE, new LinkedHashSet<String>());
        return result;
    }
}
