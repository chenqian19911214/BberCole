package com.bber.company.android.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.bean.Session;

public class AlarmActivity extends Activity implements View.OnClickListener {

    PowerManager.WakeLock wakeLock;
    private LinearLayout view_alarm;
    private TextView content, date;
    private Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        AppManager.getAppManager().addActivity(this);
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm);
        initViews();
        setListeners();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();

//        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
//        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        wakeLock.release();
    }

    private void initViews() {
        view_alarm = findViewById(R.id.view_alarm);
        content = findViewById(R.id.content);
        date = findViewById(R.id.date);
    }

    private void setListeners() {
        view_alarm.setOnClickListener(this);
    }


    private void initData() {
        session = (Session) getIntent().getSerializableExtra("session");
        content.setText(session.getContent());
        date.setText(session.getTime());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        Log.e("eeeeeeeeeeeeeeee","-----onNewIntent");
//        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        if (!pm.isScreenOn()) {
//            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
//                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
//            wl.acquire();
//            wl.release();
//        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.view_alarm:
                Intent intent;
                if (MyApplication.getContext().isAppRunning()) {
                    intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("from", session.getFrom());
                    intent.putExtra("sellerId", session.getSellerId());
                    intent.putExtra("sellerName", session.getName());
                    intent.putExtra("sellerHead", session.getHeadURL());
                } else {
                    intent = new Intent(this, WelcomeActivity.class);
                }
                startActivity(intent);
                finish();
                break;

        }
    }
}
