package com.bber.company.android.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bber.company.android.R;
import com.bber.company.android.bean.ImageItemBean;
import com.bber.company.android.databinding.ActivityCreateChatImageBinding;
import com.bber.company.android.util.Bimp;
import com.bber.company.android.util.BitmapUtils;
import com.bber.company.android.view.adapter.ChatCreatAdapter;
import com.bber.company.android.viewmodel.ChatPicViewModel;

import java.util.ArrayList;


public class ChatImageCreateActivity extends BaseActivity {

    private static final int PICK_PHOTO = 1;
    private ActivityCreateChatImageBinding binding;
    private ChatPicViewModel viewModel;
    private RecyclerView recyclerView;
    private ChatCreatAdapter chatCreatAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //加载databinding的双向绑定
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_chat);
        viewModel = new ChatPicViewModel(this);
        binding.setViewModel(viewModel);
//        viewModel.setActionListener(this);
        binding.setHeaderBarViewModel(headerBarViewModel);
        //初始化渲染控件
        initViews();
        initData();

    }

    protected void onRestart() {
        chatCreatAdapter.loading();
        super.onRestart();
    }

    //头部标题的设置  包括左右部分的监听事件
    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle("创建聊图");

    }

    private void initViews() {
        recyclerView = binding.gridviewRecycle;
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));

        //添加默认的一个加号
        Bitmap bimap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_addpic_focused);
        viewModel.chatCreatnewslist.add(bimap);

        chatCreatAdapter = new ChatCreatAdapter(this);
        recyclerView.setAdapter(chatCreatAdapter);
        chatCreatAdapter.loading();
    }


    private void initData() {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                showResult(result);
            }
        }
    }

    //根据回来的图片进行压缩
    private void showResult(ArrayList<String> paths) {
        if (viewModel.chatCreatnewslist == null) {
            viewModel.chatCreatnewslist = new ObservableArrayList<Bitmap>();
        }
        if (paths.size() != 0) {
            viewModel.chatCreatnewslist.remove(viewModel.chatCreatnewslist.size() - 1);
        }
        for (int i = 0; i < paths.size(); i++) {
            // 压缩图片
            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFd(paths.get(i), 400, 500);
            // 针对小图也可以不压缩
//            Bitmap bitmap = BitmapFactory.decodeFile(paths.get(i));
            viewModel.chatCreatnewslist.add(bitmap);

            ImageItemBean takePhoto = new ImageItemBean();
            takePhoto.setBitmap(bitmap);
            Bimp.tempSelectBitmap.add(takePhoto);
        }
        viewModel.chatCreatnewslist.add(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_addpic_focused));
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
