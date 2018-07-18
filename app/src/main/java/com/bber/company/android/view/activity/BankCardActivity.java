package com.bber.company.android.view.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.BindBankBean;
import com.bber.company.android.bean.BindBankInfoBean;
import com.bber.company.android.databinding.ActivityBankcardBinding;
import com.bber.company.android.entity.EnumBankIconType;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.listener.NoDoubleClickListener;
import com.bber.company.android.tools.DialogTool;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.util.DividerItemDecoration;
import com.bber.company.android.view.adapter.BankCardInforAdapter;
import com.bber.company.android.viewmodel.HeaderBarViewModel;
import com.bber.company.android.viewmodel.WalletViewModel;
import com.bber.company.android.widget.MyToast;
import com.bber.company.android.widget.PromptDialog;
import com.bber.company.android.widget.RectangleView;
import com.bber.customview.utils.LogUtils;

import java.util.ArrayList;

import static com.bber.company.android.app.MyApplication.getContext;


public class BankCardActivity extends BaseActivity implements View.OnClickListener, IactionListener<Object> {

    Bundle bundle = new Bundle();
    private RecyclerView recyclerView;
    private RelativeLayout view_to_card;
    private RectangleView card_info;
    private TextView card_name, card_num;
    private LinearLayout view_add_card, view_present;
    private BankCardInforAdapter bandCardadapter;
    private ImageView bank_img;
    private TextView bank_name;
    private TextView bank_card;
    private EditText edit_text;
    private Button btn_over;
    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String cardStr = edit_card.getText() + "";
            if (charSequence.length() == 0 || cardStr.length() == 0) {
                btn_over.setEnabled(false);
            } else {
                btn_over.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private Button btn_payNext;//提现按钮
    private EditText edit_name, edit_card, present_money;
    TextWatcher cardWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String nameStr = edit_name.getText() + "";
            if (charSequence.length() == 0 || nameStr.length() == 0) {
                btn_over.setEnabled(false);
            } else {
                btn_over.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextView btn_change;
    private int code = 0;
    AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            //position的位置也就是ID的位置
            code = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private ArrayAdapter<String> adapter;
    private String item[];
    private String ownName, cardNum;
    private AppCompatSpinner spinner;
    private Boolean isBank;   //true是银行卡 false提现界面
    private ActivityBankcardBinding binding;
    //全局的viewmodel
    private WalletViewModel viewModel;
    private boolean isUpdate = true; //true是修改，flase是跳转提现

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //加载databinding的双向绑定
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bankcard);
        viewModel = new WalletViewModel(this);
        binding.setViewModel(viewModel);

        binding.setHeaderBarViewModel(headerBarViewModel);
        viewModel.setActionListener(this);

        initViews();
        setListeners();
        initData();

    }

    //头部标题的设置  包括左右部分的监听事件
    @Override
    public void setHeaderBar() {

        headerBarViewModel.setBarTitle("银行卡");
        headerBarViewModel.setOnClickListener(new HeaderBarViewModel.headerclickListener() {
            @Override
            public void leftClickListener(View v) {
                onBackPressed();
            }

            @Override
            public void rightClickListener(View v) {

            }
        });

    }

    private void initViews() {
        // 检验从前面传来的页面是银行卡还是提现，分别为后面跳转不同的页面
        bundle = getIntent().getExtras();
        isBank = bundle.getBoolean("isBank");

        card_name = findViewById(R.id.card_name);
        card_num = findViewById(R.id.card_num);
        //无银行卡的展示界面
        view_to_card = findViewById(R.id.view_to_card);
        //添加银行卡界面
        view_add_card = findViewById(R.id.view_add_card);
        //
        view_present = findViewById(R.id.view_present);
        btn_over = findViewById(R.id.btn_over);
        btn_payNext = findViewById(R.id.btn_payNext);
        card_info = findViewById(R.id.card_info);
        edit_name = findViewById(R.id.edit_name);
        edit_card = findViewById(R.id.edit_card);
        spinner = findViewById(R.id.spinner);
        //银行卡的图片  名字   卡号
        bank_img = binding.imgBank;
        bank_name = binding.bankName;
        bank_card = binding.bankCard;
        edit_text = binding.editCard;
        present_money = binding.presentMoney;
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    private void setListeners() {
        view_to_card.setOnClickListener(this);
        btn_over.setOnClickListener(this);
//        btn_change.setOnClickListener(this);
        edit_name.addTextChangedListener(nameWatcher);
        edit_card.addTextChangedListener(cardWatcher);
        spinner.setOnItemSelectedListener(itemSelectedListener);

        //提现的防止多次点击
        btn_payNext.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                presentOnclick();
            }
        });
    }

    private void initData() {
        //刚进来第一遍先查询一边是否有银行卡列表
        viewModel.getCard();

        viewModel.bankCardInfors = new ArrayList<>();
//        btn_change.setVisibility(View.VISIBLE);
//        btn_change.setText("");
//        btn_change.setBackgroundResource(R.mipmap.card_add);


//        recyclerView.setAdapter(bandCardadapter);

        setView();
    }

    /***
     * 将recycleView加载出来
     */
    private void setRecycleView() {
        code = Integer.parseInt(viewModel.bindBankBean.getBankId());
        bandCardadapter = new BankCardInforAdapter(this, viewModel.bankCardInfors, code);
        recyclerView.setAdapter(bandCardadapter);
        bandCardadapter.updateListView(viewModel.bankCardInfors);

        bandCardadapter.setOnItemClickListener(new BankCardInforAdapter.OnRecyclerViewItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(View view, BindBankBean data) {
                //是银行卡还是提现界面
                if (isBank) {
                    //判断是在列表界面进入哪一个界面
                    if (isUpdate) {
                        LogUtils.e("银行卡界面");
//                    btn_change.setVisibility(View.GONE);
                        view_present.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        view_add_card.setVisibility(View.VISIBLE);

                        adapter = new ArrayAdapter<>(BankCardActivity.this, R.layout.spinner_item);
                        adapter.clear();
                        item = getResources().getStringArray(R.array.card);
                        for (int i = 0; i < item.length; i++) {
                            adapter.add(item[i]);
                        }

                        edit_name.setText(data.getCardName());
                        edit_card.setText(data.getBankCard());
                        spinner.setAdapter(adapter);
                        //设置初始值

                        spinner.setSelection(Integer.parseInt(viewModel.bindBankBean.getBankId()));
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        view_present.setVisibility(View.VISIBLE);
                        code = Integer.parseInt(viewModel.bindBankBean.getBankId());
                        bank_img.setBackground(EnumBankIconType.getbankType(code));
                        bank_name.setText(getResources().getStringArray(R.array.card)[code]);
                        String bank_num = viewModel.bankCardInfors.get(0).getBankCard();
                        bank_card.setText("尾号" + bank_num.substring(bank_num.length() - 4, bank_num.length()) + " 储蓄卡");
                    }


                } else {
                    LogUtils.e("体现界面");
//                    btn_change.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    view_present.setVisibility(View.VISIBLE);
                }
            }
        });

        bandCardadapter.setOnItemLongClickListener(new BankCardInforAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final BindBankBean data) {
                LayoutInflater inflater = LayoutInflater.from(BankCardActivity.this);
                View layout = inflater.inflate(R.layout.custom_alertdialog_del, null);
                final AlertDialog dialog = DialogTool.createDel(BankCardActivity.this, layout);
                layout.findViewWithTag(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.bankCardInfors.remove(data);
                        dialog.dismiss();
                        bandCardadapter.updateListView(viewModel.bankCardInfors);
                        setView();
                    }
                });
            }
        });
    }

    private void setView() {
        if (isBank) {
            if (viewModel.bankCardInfors.size() == 0) {
                view_to_card.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                view_to_card.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            //如果是提现界面，出现没有数据的界面
        } else {
            view_present.setVisibility(View.VISIBLE);
        }

    }

    //选择银行的跳转
    public void chooseOnClick(View view) {
        isBank = true;
        isUpdate = false;
        initData();
        view_present.setVisibility(View.GONE);
    }

    //提现的按钮触发接口
    public synchronized void presentOnclick() {
        if (!Tools.isEmpty(present_money.getText().toString())) {
            Double cash = Double.parseDouble(present_money.getText().toString());
            if (cash < 20) {
                MyToast.makeTextAnim(this, "提现最低额度20元噢~", 0, R.style.PopToast).show();
                return;
            }
            //这是银行卡的提现接口
            viewModel.presentCash(cash);
        }
    }

    @Override
    public synchronized void onClick(View v) {

        switch (v.getId()) {
//            case R.id.btn_change:
            case R.id.view_to_card:
                view_to_card.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                view_add_card.setVisibility(View.VISIBLE);
                adapter = new ArrayAdapter<String>(this, R.layout.spinner_item);
                item = getResources().getStringArray(R.array.card);
                for (int i = 0; i < item.length; i++) {
                    adapter.add(item[i]);
                }
                spinner.setAdapter(adapter);
                break;
            case R.id.btn_over:
                ownName = edit_name.getText() + "";
                cardNum = edit_card.getText() + "";
                if (code == 0) {
                    MyToast.makeTextAnim(this, R.string.spinner_error, 0, R.style.PopToast).show();
                    return;
                }
                if (ownName.isEmpty()) {
                    MyToast.makeTextAnim(this, R.string.own_error, 0, R.style.PopToast).show();
                    return;
                }

                if (cardNum.isEmpty()) {
                    MyToast.makeTextAnim(this, R.string.card_error, 0, R.style.PopToast).show();
                    return;
                }
                viewModel.updateBank(String.valueOf(code), ownName, cardNum);
                break;
        }
    }

    /***
     * 所有调用接口 成功返回和失败返回   切换回主线程
     *
     * @param o
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void SuccessCallback(Object o) {
        LogUtils.e("成功回掉");
        if (o instanceof BindBankBean) {
            if (Tools.isEmpty(((BindBankBean) o).getBankCard())) {
//                view_to_card.setVisibility(View.VISIBLE);
//                    btn_change.setVisibility(View.GONE);
            } else {
//                    btn_change.setVisibility(View.VISIBLE);
                //加载银行卡列表
                view_to_card.setVisibility(View.GONE);
                view_add_card.setVisibility(View.GONE);
//                recyclerView.setAdapter(bandCardadapter);
                recyclerView.setVisibility(View.VISIBLE);
                //true是银行卡 false提现界面
                if (isBank) {
                    LogUtils.e("银行卡");
                } else {
                    //银行图片
                    LogUtils.e("提现界面");
                    recyclerView.setVisibility(View.GONE);
                    view_present.setVisibility(View.VISIBLE);
                    code = Integer.parseInt(viewModel.bindBankBean.getBankId());
                    bank_img.setBackground(EnumBankIconType.getbankType(code));
                    bank_name.setText(getResources().getStringArray(R.array.card)[code]);
                    String bank_num = viewModel.bankCardInfors.get(0).getBankCard();
                    bank_card.setText("尾号" + bank_num.substring(bank_num.length() - 4, bank_num.length()) + " 储蓄卡");

                }

                setRecycleView();
            }
        }
        if (o instanceof BindBankInfoBean) {
            MyToast.makeTextAnim(this, "修改成功", 0, R.style.PopToast).show();
            view_to_card.setVisibility(View.GONE);
            view_add_card.setVisibility(View.GONE);

            //重新获取一次银行卡列表
            viewModel.getCard();
        }
        if (o instanceof String) {
            //提现成功
            LogUtils.e("提现回来dialog");
            PromptDialog promptDialog = new PromptDialog(BankCardActivity.this, (String) o);
            promptDialog.show();
        }
    }

    @Override
    public void FailCallback(String result) {

    }
}
