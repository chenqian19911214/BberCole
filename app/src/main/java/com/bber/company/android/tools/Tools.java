package com.bber.company.android.tools;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.view.activity.LoginActivity;
import com.bber.company.android.widget.LoadingDialog;
import com.bber.company.android.widget.MyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具包
 * Created by Administrator on 15-4-21.
 */
public class Tools {

    /**
     * 手机振动
     *
     * @param activity
     * @return [0] - width,[1] - height
     */

    private static Vibrator vib;

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断字符串是否为空(包括隔空)
     */
    public static boolean
    isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 设置首页activity ActionBar
     * <p/>
     * *
     */
    public static void setHomeActionBarTitle(Activity activity) {
        activity.getActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getActionBar().setDisplayShowHomeEnabled(false);//设置icon隐藏
        activity.getActionBar().setDisplayShowTitleEnabled(false);//去掉标题
        activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getActionBar().setDisplayShowCustomEnabled(true);
    }

    /**
     * 自定义activity ActionBar
     * <p/>
     * *
     */
    public static void setCustomActionBar(Activity activity, int textID, boolean needBack) {
        activity.setTitle(textID);
        if (needBack) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            activity.getActionBar().setDisplayHomeAsUpEnabled(false);
        }
        activity.getActionBar().setDisplayShowHomeEnabled(false);//设置icon隐藏
        activity.getActionBar().setDisplayShowTitleEnabled(false);//去掉标题
        activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getActionBar().setDisplayShowCustomEnabled(true);
    }

    /**
     * 设置activity ActionBar
     * <p/>
     * *
     */
    public static void setActionBarTitle(Activity activity, int textID, boolean needBack) {
        activity.setTitle(textID);
        if (needBack) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            activity.getActionBar().setDisplayHomeAsUpEnabled(false);
        }
        activity.getActionBar().setDisplayShowCustomEnabled(false);
        activity.getActionBar().setDisplayShowTitleEnabled(true);//去掉标题
    }

    /**
     * 关闭软键盘
     * <p/>
     * *
     */
    public static void hideKb(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && activity.getCurrentFocus() != null) {
            if (activity.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 更具edittext弹出键盘
     * <p/>
     * *
     */
    public static void showKb(EditText edit) {
        InputMethodManager imm = (InputMethodManager) edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 获取图片被旋转的角度
     * 有些手机系统调用相册后的图片被旋转了
     * <p/>
     * *
     */
    public static int getImageDigree(String filepath) {
        //根据图片的filepath获取到一个ExifInterface的对象

        ExifInterface exif = null;

        try {

            exif = new ExifInterface(filepath);

        } catch (IOException e) {

            e.printStackTrace();

            exif = null;

        }

        int degree = 0;
        if (exif != null) {

            // 读取图片中相机方向信息

            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,

                    ExifInterface.ORIENTATION_UNDEFINED);

            // 计算旋转角度

            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:

                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;

            }

        }
        return degree;
    }

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSDCard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 取voice文件夹路径 *
     */
    public static String getvoiceSDPath() {
        File sdDir = null;
        if (hasSDCard()) {
//            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
            sdDir = MyApplication.getContext().getExternalCacheDir();
        }
        if (sdDir != null) {
            File dirFile = new File(sdDir.getAbsolutePath() + "/voice");
            if (!dirFile.exists())
                dirFile.mkdirs();
            return dirFile.toString();
        } else {
            return "";
        }
    }

    /**
     * 取voice文件夹路径 *
     */
    public static String getORTSDPath() {
        File sdDir = null;
        if (hasSDCard()) {
//            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
            sdDir = MyApplication.getContext().getExternalCacheDir();
        }
        if (sdDir != null) {
            File dirFile = new File(sdDir.getAbsolutePath() + "/secure");
            if (!dirFile.exists())
                dirFile.mkdirs();
            return dirFile.toString();
        } else {
            return "";
        }
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email))
            return false;
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 生成32位随机字符(包括数字、英文)
     * <p/>
     * *
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    /**
     * MD5加密字符串
     * <p/>
     * *
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    /**
     * 得到手机SIM卡联系人信息*
     */
//    private void getSIMContacts() {
//        ContentResolver resolver = getContentResolver();
//        // 获取Sim卡联系人
//        Uri uri = Uri.parse("content://icc/adn");
//        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
//                null);
//
//        if (phoneCursor != null) {
//            while (phoneCursor.moveToNext()) {
//
//                // 得到手机号码
//                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
//                // 当手机号码为空的或者为空字段 跳过当前循环
//                if (TextUtils.isEmpty(phoneNumber))
//                    continue;
//                // 得到联系人名称
//                String contactName = phoneCursor
//                        .getString(PHONES_DISPLAY_NAME_INDEX);
//
//                //Sim卡中没有联系人头像
//
//                mContactsName.add(contactName);
//                mContactsNumber.add(phoneNumber);
//            }
//
//            phoneCursor.close();
//        }
//    }

    /**
     * 得到手机通讯录联系人信息*
     */
    public static String getPhoneContacts(Context context, ProgressDialog updateDialog) {

        /**
         * 获取库Phone表字段*
         */
        final String[] PHONES_PROJECTION = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

        /**
         * 联系人显示名称*
         */
        final int PHONES_DISPLAY_NAME_INDEX = 0;

        /**
         * 电话号码*
         */
        final int PHONES_NUMBER_INDEX = 1;


        String jsonString = "";

        ContentResolver resolver = context.getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            Map<String, String> maps = new HashMap<String, String>();

            while (phoneCursor.moveToNext()) {

                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if (!TextUtils.isEmpty(phoneNumber)) {
                    phoneNumber = phoneNumber.replaceAll(" ", "");//去除号码中的空格
                    if (phoneNumber.contains("+86")) {
                        phoneNumber = phoneNumber.replace("+86", "");//去除号码开头的"+86"
                    }
                    if (phoneNumber.length() != 11) //不是11位跳过当前循环
                        continue;
                } else {
                    continue;
                }
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                maps.put(phoneNumber, contactName);
            }
            phoneCursor.close();
            jsonString = MapToJsonString(maps, updateDialog);
        } else {
            if (updateDialog != null && updateDialog.isShowing()) {
                updateDialog.dismiss();
            }
        }
        return jsonString;
    }

    /**
     * Map转为json字符串
     * <p/>
     * *
     */

    public static String MapToJsonString(Map<String, String> maps, ProgressDialog updateDialog) {

        // 初始化返回值
        String json = "";

        if (maps == null) {
            if (updateDialog != null && updateDialog.isShowing()) {
                updateDialog.dismiss();
            }
            return json;
        }
        StringBuilder buff = new StringBuilder();
        try {
            buff.append("[");
            buff.append("{");
            int i = 0;
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                if (i != 0) {
                    buff.append(",");
                }
                buff.append('"' + entry.getKey() + '"');
                buff.append(":");
                buff.append("\"");
                buff.append(entry.getValue());
                buff.append("\"");
                i++;
            }
            buff.append("}");
            buff.append("]");
            json = buff.toString();

        } catch (Exception e) {
            if (updateDialog != null && updateDialog.isShowing()) {
                updateDialog.dismiss();
            }
        }

        return json;
    }

    public static Boolean jsonResult(Context context, JSONObject jsonObject, Object object) {
        if (jsonObject == null) {
            ToastUtils.showToast(R.string.getData_fail, 0);
        } else {
            try {
                int resultCode = jsonObject.getInt("resultCode");
                String resultMessage = jsonObject.getString("resultMessage");
                if (resultCode != 1) {
                    if (resultCode == 2) { //用户未登录
                        AppManager.getAppManager().finishAllActivity();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("relogin", true);
                        context.startActivity(intent);
                    } else if (resultCode != 4) {
                        if (!Tools.isEmpty(resultMessage)) {
                            MyToast.makeTextAnim(context, resultMessage, 0, R.style.PopToast).show();
                        } else {
                            MyToast.makeTextAnim(context, "目前提现人数较多,请稍后在试", 0, R.style.PopToast).show();
                        }
                    }
                } else {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (object != null) {
            if (object instanceof LoadingDialog) {
                ((LoadingDialog) object).dismiss();
            } else if (object instanceof ProgressDialog) {
                ((ProgressDialog) object).dismiss();
            } else if (object instanceof ProgressBar) {
                ((ProgressBar) object).setVisibility(View.GONE);
            }
        }
        return true;
    }

    /**
     * 保存借款项目原图URL
     * *
     */
    public static void saveImageURLJson(Context context, String json) {
        SharedPreferences mSharedPreferences =
                context.getSharedPreferences("jiedianr", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove("imageURLJson");
        editor.putString("imageURLJson", json);
        editor.commit();
    }

    /**
     * 获取借款项目原图URL
     * *
     */
    public static String getImageURLJson(Context context) {
        SharedPreferences mSharedPreferences =
                context.getSharedPreferences("jiedianr", Context.MODE_PRIVATE);
        return mSharedPreferences.getString("imageURLJson", null);
    }

    public static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * 将mp3 url转成文件名 *
     */
    public static String convertMp3UrlToFileName(String url) {
        String fileName = Tools.md5(url) + ".mp3";
        return fileName;
    }

    /**
     * * 获取版本号
     * * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            Log.e("eeeeeeeeeeeeeeeeeee", "---version:" + version);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return [0] - width,[1] - height
     */
    public static int[] getScreenDispaly(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int[] result = {width, height};
        return result;
    }

    public static void Vibrate(final Activity activity) {
        boolean canVibrate = (boolean) SharedPreferencesUtils.get(activity, "vibrate", true);
        if (canVibrate == false) {
            return;
        }
        vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        long[] pattern = {1, 50, 1000, 50};   // 停止 开启 停止 开启
        vib.vibrate(pattern, 0);
    }

    public static void Vibrate(final Activity activity, long millTime) {
        boolean canVibrate = (boolean) SharedPreferencesUtils.get(activity, "vibrate", true);
        if (canVibrate == false) {
            return;
        }
        vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(millTime);
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static void CancleVibrate() {
        if (vib != null) {
            vib.cancel();
        }
    }

    public static Double StringToDouble(String value) {
        Double resultValue = 0.0;
        if (value != null && isEmpty(value) == false) {
            resultValue = Double.parseDouble(value);
        }
        return resultValue;
    }


    public static Float StringToFloat(String value) {
        Float resultValue = 0.0f;
        if (value != null && isEmpty(value) == false) {
            resultValue = Float.valueOf(value).floatValue();
        }
        return resultValue;
    }

    public static int StringToInt(String value) {
        int resultValue = 0;
        if (value != null && isEmpty(value) == false && isNumeric(value) == true) {
            resultValue = Integer.valueOf(value);
        }
        return resultValue;
    }

    /**
     * @date 2015.7.5
     * @String sourceString 源字符串
     * @String keywordS 关键字
     * @decribe 检查关键字
     */
    public static boolean IsContainKeyWord(String sourceString, String[] keywordS) {
        boolean resultValue = false;
        for (String keyword : keywordS) {
            if (Pattern.compile(keyword, Pattern.CASE_INSENSITIVE).matcher(sourceString).find()) {
                resultValue = true;
                break;
            }
        }
        return resultValue;
    }

    /**
     * @date 2015.7.5
     * @String sourceString 源字符串
     * @String keywordS 关键字
     * @decribe 检查关键字
     */
    public static String connectAddress(String sourceString) {
        String tmp;
        if (sourceString == null || isEmpty(sourceString)) {
            tmp = "";
        } else {
            tmp = sourceString + ",";
        }

        return tmp;
    }

    /**
     * 设置字体的大小和颜色
     *
     * @param content
     * @param color
     * @param size
     * @return spannableString
     */
    public static SpannableString getSpannableString(String content, int color, Float size) {
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, content.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(size), 0, content.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }


    /**
     * 解决小米手机上获取图片路径为null的情况
     *
     * @param uri
     * @param context
     * @return
     */
    public static Uri getImageuri(Context context, Uri uri) {

        String path = uri.getEncodedPath();
        if (path != null) {
            path = Uri.decode(path);
            ContentResolver cr = context.getContentResolver();
            StringBuffer buff = new StringBuffer();
            buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                    .append("'" + path + "'").append(")");
            Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.ImageColumns._ID},
                    buff.toString(), null, null);
            int index = 0;
            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                index = cur.getInt(index);
            }
            if (index == 0) {
            } else {
                Uri uri_temp = Uri
                        .parse("content://media/external/images/media/"
                                + index);
                if (uri_temp != null) {
                    uri = uri_temp;
                }
            }
        }
        return uri;
    }

    /**
     * 时间戳转换为date
     *
     * @param seconds
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));

    }


    /**
     * @param seconds
     * @param format
     * @return
     */
    public static String date2TimeStamp(String seconds, String format) {
        String res;
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }

        if (format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(seconds);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /**
     * 将时间转换成另外一种格式
     *
     * @param scrDate
     * @param scrFormat
     * @param desFormat
     */
    public static String dateToDate(String scrDate, String scrFormat, String desFormat) {
        String TimeStamp = date2TimeStamp(scrDate, scrFormat);
        String dateTime = timeStamp2Date(TimeStamp, desFormat);
        return dateTime;
    }

}
