package com.bber.company.android.view.activity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.Toast;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.util.LocalImageHelper;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

/**
 * 选择照片页面
 */
public class PhotoWallActivity extends BaseAppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    String folder;
    LocalImageHelper helper = LocalImageHelper.getInstance((MyApplication) getBaseContext());
    List<LocalImageHelper.LocalFile> checkedItems;
    List<LocalImageHelper.LocalFile> currentFolder = null;
    private GridView mPhotoWall;
    private int nowSize;

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_photo_wall;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_photo_wall);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        mPhotoWall = findViewById(R.id.photo_wall_grid);
    }

    private void setListeners() {

    }

    private void initData() {
        nowSize = getIntent().getIntExtra("nowSize", 0);
        folder = getIntent().getExtras().getString(Constants.LOCAL_FOLDER_NAME);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //防止停留在本界面时切换到桌面，导致应用被回收，图片数组被清空，在此处做一个初始化处理
                helper.initImage();
                //获取该文件夹下地所有文件
                final List<LocalImageHelper.LocalFile> folders = helper.getFolder(folder);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (folders != null) {
                            currentFolder = folders;
                            MyAdapter adapter = new MyAdapter(PhotoWallActivity.this, folders);
                            title.setText(folder);
                            mPhotoWall.setAdapter(adapter);
                            //设置当前选中数量
//                            if (checkedItems.size()+LocalImageHelper.getInstance().getCurrentSize() > 0) {
//                                finish.setText("完成(" + (checkedItems.size()+LocalImageHelper.getInstance().getCurrentSize()) + "/9)");
//                                finish.setEnabled(true);
//                                headerFinish.setText("完成(" + (checkedItems.size()+LocalImageHelper.getInstance().getCurrentSize()) + "/9)");
//                                headerFinish.setEnabled(true);
//                            } else {
//                                finish.setText("完成");
////                                finish.setEnabled(false);
//                                headerFinish.setText("完成");
////                                headerFinish.setEnabled(false);
//                            }
                        }
                    }
                });
            }
        }).start();
        checkedItems = helper.getCheckedItems();
        LocalImageHelper.getInstance((MyApplication) getApplicationContext()).setResultOk(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (!b) {
            if (checkedItems.contains(compoundButton.getTag())) {
                checkedItems.remove(compoundButton.getTag());
            }
        } else {
            if (!checkedItems.contains(compoundButton.getTag())) {

                if (checkedItems.size() + nowSize >= 7) {
                    Toast.makeText(this, "最多选择7张图片", Toast.LENGTH_SHORT).show();
                    compoundButton.setChecked(false);
                    return;
                }
                checkedItems.add((LocalImageHelper.LocalFile) compoundButton.getTag());
            }
        }
//        if (checkedItems.size()+ LocalImageHelper.getInstance().getCurrentSize()> 0) {
//            finish.setText("完成(" + (checkedItems.size()+LocalImageHelper.getInstance().getCurrentSize()) + "/9)");
//            finish.setEnabled(true);
//            headerFinish.setText("完成(" +(checkedItems.size()+LocalImageHelper.getInstance().getCurrentSize()) + "/9)");
//            headerFinish.setEnabled(true);
//        } else {
//            finish.setText("完成");
//            finish.setEnabled(false);
//            headerFinish.setText("完成");
//            headerFinish.setEnabled(false);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sure, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.sure:
                //选择图片完成,回到起始页面
//                Intent intent = new Intent(PhotoWallActivity.this, AlbumActivity.class);
//                startActivity(intent);
//                app.finishActivity(PhotoAlbumActivity.class);
                PhotoAlbumActivity.instance.finish();
                setResult(Activity.RESULT_OK);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyAdapter extends BaseAdapter {
        List<LocalImageHelper.LocalFile> paths;
        private Context m_context;
        private LayoutInflater miInflater;

        public MyAdapter(Context context, List<LocalImageHelper.LocalFile> paths) {
            m_context = context;
            this.paths = paths;
        }

        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public LocalImageHelper.LocalFile getItem(int i) {
            return paths.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.photo_wall_item, null);
                viewHolder.imageView = convertView.findViewById(R.id.photo_wall_item_photo);
                viewHolder.checkBox = convertView.findViewById(R.id.photo_wall_item_cb);
                viewHolder.checkBox.setOnCheckedChangeListener(PhotoWallActivity.this);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            LocalImageHelper.LocalFile localFile = paths.get(i);
            viewHolder.imageView.setTag(localFile.getThumbnailUri());
            if (viewHolder.imageView.getTag() != null
                    && viewHolder.imageView.getTag().equals(localFile.getThumbnailUri())) {
//                Uri uri = Uri.parse(localFile.getThumbnailUri());
//                viewHolder.imageView.setImageURI(uri);

                ImageRequest imageRequest =
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse(localFile.getThumbnailUri()))
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


            viewHolder.checkBox.setTag(localFile);
            viewHolder.checkBox.setChecked(checkedItems.contains(localFile));
//            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showViewPager(i);
//                }
//            });
            return convertView;
        }

        private class ViewHolder {
            SimpleDraweeView imageView;
            CheckBox checkBox;
        }
    }

}
