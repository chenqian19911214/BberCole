package com.bber.company.android.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bber.company.android.R;
import com.bber.company.android.constants.preferenceConstants;
import com.bber.company.android.databinding.ActivityResponseBinding;
import com.bber.company.android.tools.ToastUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.viewmodel.MessageViewModel;
import com.bber.company.android.widget.MyToast;

/**
 * Created by Vencent on 2017/3/24.
 */

public class ResponseActivity extends BaseActivity {

    private ActivityResponseBinding binding;
    private MessageViewModel viewModel;
    private EditText post_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_response);
        binding.setHeaderBarViewModel(headerBarViewModel);
        viewModel = new MessageViewModel(this);
        binding.setViewModel(viewModel);
        initView();
    }

    private void initView() {

        post_edittext = binding.postEdittext;
    }

    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle("问题反馈");
    }

    public void postdata(View view) {
        String chatMsg = post_edittext.getText() + "";
        if (Tools.IsContainKeyWord(chatMsg, preferenceConstants.KEY_WORD_CHAT)) {
            ToastUtils.showToast(R.string.illege_key_word_chat, 0);
            return;
        }
        if (Tools.isEmpty(chatMsg)) {
            MyToast.makeTextAnim(this, "输入不能为空", 0, R.style.PopToast).show();
            return;
        }

        viewModel.postContent(chatMsg);
    }
}
