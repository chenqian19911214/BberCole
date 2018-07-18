package com.bber.company.android.tools.imageload;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.bber.company.android.app.MyApplication;
import com.bber.company.android.tools.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: Bruce
 * Date: 2016/5/23
 * Version:
 * Describe:
 */
public class AudioGetFromHttp {
    /**
     * 线程池线程数量
     */
    private static final int THREAD_NUM = 5;
    private static final String CACHDIR = "bber";
    /**
     * 内存缓存对象
     */
    private static final String WHOLESALE_CONV = ".cach";
    private static final String WHOLESALE_JPG = ".jpg";
    private static final int MB = 1024 * 1024;
    private static final int CACHE_SIZE = 100;
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
    /**
     * 线程池
     */
    private ExecutorService executorService;
    /***/
    private Context mContext;


    public AudioGetFromHttp(Context context) {
        this.mContext = context;
        this.executorService = Executors.newFixedThreadPool(THREAD_NUM);
    }

    /**
     * 异步加载应用图标
     *
     * @param URL
     * @author heweiyun
     */
    public void asyncDownAudio(String URL) {
        executorService.submit(new getAudioFromURI(URL));
    }

    /**
     * 异步加载应用图标
     *
     * @param URL
     * @author heweiyun
     */
    public void asyncDownImage(String URL) {
        executorService.submit(new getImageFromURI(URL));
    }

    public File getFile(final String url) {
        final String path = getDirectory() + "/" + convertUrlToFileName(url);
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public String getImageLoacalURL(final String url) {
        final String path = getDirectory() + "/" + convertUrlToImageName(url);
        return path;
    }

    public File getLoacalImage(final String url) {
        final String path = getDirectory() + "/" + convertUrlToImageName(url);
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public void saveFileToCache(File file, String url) {
        //判断sdcard上的空间
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            //SD空间不足
            removeCache(getDirectory());
            return;
        }

        final String path = getDirectory() + "/" + convertUrlToFileName(url);
        File newfile = new File(path);
        if (!file.exists()) {
            return;
        }
        try {
            FileInputStream fosfrom = new FileInputStream(file);
            FileOutputStream fosto = new FileOutputStream(newfile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception ex) {
        }
    }

    /**
     * 计算存储目录下的文件大小，
     * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
     * 那么删除40%最近没有被使用的文件
     */
    private boolean removeCache(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return true;
        }
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return false;
        }

        int dirSize = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().contains(WHOLESALE_CONV)) {
                dirSize += files[i].length();
            }
            Log.e("eeeeeeeeeeee", "removeCache:");
        }

        if (dirSize > CACHE_SIZE * MB || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            int removeFactor = (int) ((0.4 * files.length) + 1);
            Arrays.sort(files, new FileLastModifSort());
            for (int i = 0; i < removeFactor; i++) {
                if (files[i].getName().contains(WHOLESALE_CONV)) {
                    files[i].delete();
                }
            }
        }
        return freeSpaceOnSd() > CACHE_SIZE;
    }

    /**
     * 删除所有的缓存
     */

    public void removeCaches() {
        File dir = new File(getDirectory());
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        int removeFactor = (int) ((0.4 * files.length) + 1);
        Arrays.sort(files, new FileLastModifSort());
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    /**
     * 把网络上的文件保存到sd卡
     *
     * @param url 完整的可访问的url
     * @return boolean
     */
    public boolean saveUrlFile(String url) throws Exception {
        //判断sdcard上的空间
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            //SD空间不足
            removeCache(getDirectory());
            return false;
        }
        String filename = convertUrlToFileName(url);
        String dir = getDirectory();
        File dirFile = new File(dir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        File file = new File(dir + "/" + filename);

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        InputStream input = conn.getInputStream();
        if (null != input) {
            copyFile(input, file);
            conn.disconnect();
            return true;
        }
        return false;
    }

    /**
     * 把网络上的文件保存到sd卡
     *
     * @param url 完整的可访问的url
     * @return boolean
     */
    public boolean saveUrlImage(String url) throws Exception {
        //判断sdcard上的空间
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            //SD空间不足
            removeCache(getDirectory());
            return false;
        }
        String filename = convertUrlToImageName(url);
        String dir = getDirectory();
        File dirFile = new File(dir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        File file = new File(dir + "/" + filename);

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        InputStream input = conn.getInputStream();
        if (null != input) {
            copyFile(input, file);
            conn.disconnect();
            return true;
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param input   输入流
     * @param newFile 新文件
     * @throws Exception
     */
    public void copyFile(InputStream input, File newFile) throws Exception {
        OutputStream output = new FileOutputStream(newFile);
        byte[] buffer = new byte[1024];
        int i = 0;
        while ((i = input.read(buffer)) != -1) {
            output.write(buffer, 0, i);
        }
        output.flush();
        output.close();
        input.close();
    }

    /**
     * 修改文件的最后修改时间
     **/
    public void updateFileTime(String path) {
        File file = new File(path);
        long newModifiedTime = System.currentTimeMillis();
        file.setLastModified(newModifiedTime);
    }

    /**
     * 计算sdcard上的剩余空间
     **/
    private int freeSpaceOnSd() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        return (int) sdFreeMB;
    }

    /**
     * 将url转成文件名
     **/
    private String convertUrlToFileName(String url) {
        String fileName = Tools.md5(url);
        Log.e("eeeeeeeeeeeeeee", "url:" + url + "--fileName:" + fileName);

        return fileName + WHOLESALE_CONV;
    }

    /**
     * 将url转成文件名
     **/
    private String convertUrlToImageName(String url) {
        String fileName = Tools.md5(url);
        Log.e("eeeeeeeeeeeeeee", "url:" + url + "--fileName:" + fileName);

        return fileName + WHOLESALE_JPG;
    }

    /**
     * 获得缓存目录
     **/
    public String getDirectory() {
        String dir = getSDPath();
        return dir;
    }

    /**
     * 获得bber目录
     **/
    public String getMyDirectory() {
        String dir = getSDRootPath() + "/" + CACHDIR;
        return dir;
    }

    /**
     * 取SD卡 data路径
     **/
    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = MyApplication.getContext().getExternalCacheDir(); //获取android/data根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }

    /**
     * 取SD卡根路径
     **/
    private String getSDRootPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }

    public class getAudioFromURI extends Thread {
        private String URL;

        public getAudioFromURI(String url) {
            this.URL = url;
        }

        @Override
        public void run() {
            try {
                saveUrlFile(URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class getImageFromURI extends Thread {
        private String URL;

        public getImageFromURI(String url) {
            this.URL = url;
        }

        @Override
        public void run() {
            try {
                saveUrlImage(URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件的最后修改时间进行排序
     */
    private class FileLastModifSort implements Comparator<File> {
        public int compare(File arg0, File arg1) {
            if (arg0.lastModified() > arg1.lastModified()) {
                return 1;
            } else if (arg0.lastModified() == arg1.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

}
