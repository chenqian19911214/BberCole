package com.bber.company.android.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.util.LocalImageHelper;
import com.bber.company.android.view.customcontrolview.DialogView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 选择相册
 */
public class PhotoAlbumActivity extends BaseAppCompatActivity {

    public static PhotoAlbumActivity instance = null;
    List<String> folderNames;
    private ListView select_img_listView;
    private LocalImageHelper helper;
    Thread myTread = new Thread(new Runnable() {
        @Override
        public void run() {
            //开启线程初始化本地图片列表，该方法是synchronized的，因此当AppContent在初始化时，此处阻塞
            LocalImageHelper.getInstance((MyApplication) getApplicationContext()).initImage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //初始化完毕后，显示文件夹列表
                    initAdapter();
                    //  progressDialog.dismiss();
                    DialogView.dismiss(PhotoAlbumActivity.this);
                }
            });
        }
    });
    private int nowSize;
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(PhotoAlbumActivity.this, PhotoWallActivity.class);
            intent.putExtra("nowSize", nowSize);
            intent.putExtra(Constants.LOCAL_FOLDER_NAME, folderNames.get(i));
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_photo_album;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = LocalImageHelper.getInstance((MyApplication) getApplicationContext());
        initView();
        title.setText("选择相册");
        setListeners();
        initData();
    }

    private void initView() {
        select_img_listView = findViewById(R.id.select_img_listView);

        DialogView.show(PhotoAlbumActivity.this, true);

    }

    private void setListeners() {
        select_img_listView.setOnItemClickListener(itemClickListener);
    }

    private void initData() {
        instance = this;
        nowSize = getIntent().getIntExtra("nowSize", 0);
        LocalImageHelper.getInstance((MyApplication) getApplicationContext()).clear();
        myTread.start();
    }

    public synchronized void initAdapter() {
        select_img_listView.setAdapter(new FolderAdapter(this, helper.getFolderMap()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_cancle, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalImageHelper.getInstance((MyApplication) getApplicationContext()).clearPaths();
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.cancle:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    public class FolderAdapter extends BaseAdapter {
        Map<String, List<LocalImageHelper.LocalFile>> folders;
        Context context;

        FolderAdapter(Context context, Map<String, List<LocalImageHelper.LocalFile>> folders) {
            this.folders = folders;
            this.context = context;
            folderNames = new ArrayList<>();

            Iterator iter = folders.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                folderNames.add(key);
            }
            //根据文件夹内的图片数量降序显示
            Collections.sort(folderNames, new Comparator<String>() {
                public int compare(String arg0, String arg1) {
                    Integer num1 = helper.getFolder(arg0).size();
                    Integer num2 = helper.getFolder(arg1).size();
                    return num2.compareTo(num1);
                }
            });
        }

        @Override
        public int getCount() {
            return folders.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.photo_album_lv_item, null);
                viewHolder.imageView = convertView.findViewById(R.id.select_img_gridView_img);
                viewHolder.textView = convertView.findViewById(R.id.select_img_gridView_path);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String name = folderNames.get(i);
            List<LocalImageHelper.LocalFile> files = folders.get(name);
            viewHolder.textView.setText(name + "(" + files.size() + ")");
            if (files.size() > 0) {
//                Uri uri = Uri.parse(files.get(0).getThumbnailUri());
//                viewHolder.imageView.setImageURI(uri);

                ImageRequest imageRequest =
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse(files.get(0).getThumbnailUri()))
                                .setResizeOptions(
                                        new ResizeOptions(150, 150))
                                .build();
                DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setOldController(viewHolder.imageView.getController())
                        .setAutoPlayAnimations(true)
                        .build();
                viewHolder.imageView.setController(draweeController);
            }
            return convertView;
        }

        private class ViewHolder {
            SimpleDraweeView imageView;
            TextView textView;
        }
    }
}
