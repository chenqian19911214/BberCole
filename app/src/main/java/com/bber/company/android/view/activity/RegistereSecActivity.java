package com.bber.company.android.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bber.company.android.R;
import com.bber.company.android.databinding.ActivityRegisterSecBinding;
import com.bber.company.android.network.NetWork;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.viewmodel.UserViewModel;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.TimePickerView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Vecent on 2017/3/15.
 * 第一个注册界面
 */

public class RegistereSecActivity extends BaseActivity implements AMapLocationListener {

    public AMapLocationClient locationClient;
    public AMapLocationClientOption locationOption;
    String psd = "";
    private ActivityRegisterSecBinding binding;
    private TextView agreement_text, use_age;
    private EditText user_userName, password, password_again, recommed;
    //传递过来的城市和邀请码
    private String city, key, username;
    private Date mDate;
    private UserViewModel userViewModel;
    //选择器
    private TimePickerView pvOptions;
    private String payWayCode;
    private ImageView icon_back;
    private String recommendBuyerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_sec);
        binding.setHeaderBarViewModel(headerBarViewModel);
        userViewModel = new UserViewModel(this);
        binding.setViewModel(userViewModel);
        netWork = new NetWork(this);
        //渲染控件
        initview();
    }

    private void initview() {
        city = getIntent().getStringExtra("city");
        key = getIntent().getStringExtra("inviteCode");
        username = getIntent().getStringExtra("username");
        psd = getIntent().getStringExtra("password");
//        password = binding.userPassword;
//        password_again = binding.userPasAgain;
        use_age = binding.useAge;
        recommed = binding.recommed;
        icon_back = binding.iconBack;
        //选项选择器
        pvOptions = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        pvOptions.setTitle("出生日期选择");
        pvOptions.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String str = sdf.format(date);
                mDate = new Timestamp(date.getTime());
                use_age.setText(str);
            }
        });
        RadioGroup radioGroup = binding.radioGroup;
        RadioButton radioButton = binding.radioSexMan;
        radioButton.setChecked(true);
        RadioButton check = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        payWayCode = check.getTag().toString();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton tempButton = findViewById(checkedId);
                payWayCode = tempButton.getTag().toString();
            }
        });

        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /***
     * 弹出选择出生日期的选择框
     *
     * @param view
     */
    public void alartOnclick(View view) {
        pvOptions.show();
    }

    /***
     * 跳转下一步的按钮
     *
     * @param view
     */
    public synchronized void submit(View view) {
        if (!netWork.isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        recommendBuyerName = recommed.getText().toString();
        //注册新用户
        userViewModel.register(username, psd, city, key, mDate, Tools.StringToInt(payWayCode), recommendBuyerName);

    }

    /***
     * 定位系统
     */
    private void startLocation() {
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
//        locationOption.setGpsFirst(false);
        //设置是否只定位一次,默认为false
        locationOption.setOnceLocation(true);

        if (!new NetWork(this).isNetworkConnected()) {
            MyToast.makeTextAnim(this, R.string.no_network, 0, R.style.PopToast).show();
            return;
        }
        // 设置定位参数
        locationClient.setLocationOption(locationOption);

        // 启动定位
        locationClient.startLocation();
    }

    @Override
    public void setHeaderBar() {
        headerBarViewModel.setBarTitle("注册");
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (null != amapLocation) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                //获取位置信息
                city = amapLocation.getCity();
                // 停止定位
                locationClient.stopLocation();
                if (!Tools.isEmpty(city)) {
                    recommendBuyerName = recommed.getText().toString();
                    userViewModel.register(username, psd, city, key, mDate, Tools.StringToInt(payWayCode), recommendBuyerName);
                } else {
                    MyToast.makeTextAnim(this, R.string.lbs_fail, 0, R.style.PopToast).show();
                }
            } else {
                MyToast.makeTextAnim(this, R.string.lbs_fail, 0, R.style.PopToast).show();
            }
        }
    }
}
