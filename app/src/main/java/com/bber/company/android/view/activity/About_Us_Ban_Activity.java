package com.bber.company.android.view.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.databinding.ActivityAboutUsBinding;

public class About_Us_Ban_Activity extends BaseAppCompatActivity {
    private ActivityAboutUsBinding binding;

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
            // LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_about__us__ban_;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_about__us__ban_);

        title.setText(R.string.about_usus);
        String bi = getLocalVersionName(this);
        TextView f = findViewById(R.id.textqianid);
        f.setText("版本：" + bi);
    }

}
