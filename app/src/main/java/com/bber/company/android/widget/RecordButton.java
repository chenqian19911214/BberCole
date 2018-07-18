package com.bber.company.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.tools.ToastUtils;
import com.bber.company.android.tools.Tools;
import com.bber.company.android.tools.imageload.AudioGetFromHttp;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class RecordButton extends TextView {

    private static final int MIN_INTERVAL_TIME = 1000;// 1s
    private static int[] res = {R.mipmap.mic_1, R.mipmap.mic_2,
            R.mipmap.mic_3};
    private static ImageView mic_img;
    public String mFileName = null;
    private AudioGetFromHttp aduio;
    private Context mContext;
    private AudioManager audioManager;
    private int recordTime = 0;

    //    private ScrollView scrollView;
    private File newFile = null;//重命名后的文件
    private OnFinishedRecordListener finishedListener;
    private long startTime;
    private Dialog recordIndicator;
    private TextView cancle_text;
    private MediaRecorder recorder;
    private ObtainDecibelThread thread;
    private Handler volumeHandler;
    private float startY = 0, endY = 0;
    private boolean isCancle = false;
    private OnDismissListener onDismiss = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    public RecordButton(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


//    public void setListView(ListView scrollView) {
//        this.scrollView = scrollView;
//    }

    public void setSavePath(String path) {
        mFileName = path;
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        finishedListener = listener;
    }

    private void init() {
        volumeHandler = new ShowVolumeHandler();
        aduio = new AudioGetFromHttp(mContext);
    }

    public void setNewFile(File file) {
        newFile = file;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (mFileName == null)
//            return false;

//        this.scrollView.requestDisallowInterceptTouchEvent(true);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                this.setSelected(true);
                initDialogAndStartRecord();
                break;
            case MotionEvent.ACTION_UP:
                setText("按住录音");
                this.setSelected(false);
                if (isCancle) {
                    cancelRecord();
                } else {
                    finishRecord();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                endY = event.getY();
                isCancle = false;
                if (endY - startY <= -200) {
                    cancle_text.setText("松手取消录音");
                    setText("松手取消录音");
                    cancle_text.setTextColor(getResources().getColor(R.color.red));
                    isCancle = true;
                } else {
                    cancle_text.setText("上滑取消录音");
                    setText("上滑取消录音");
                    cancle_text.setTextColor(getResources().getColor(R.color.item_bg));
                }
                break;
            case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel

                break;
        }
        return true;
    }

    private void initDialogAndStartRecord() {

        startTime = System.currentTimeMillis();
        recordIndicator = new Dialog(getContext(),
                R.style.voice_dialog_style);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.voice_dialog, null);
        mic_img = view.findViewById(R.id.mic_img);
        cancle_text = view.findViewById(R.id.cancle_text);
        recordIndicator.setContentView(view, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recordIndicator.setOnDismissListener(onDismiss);
        LayoutParams lp = recordIndicator.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;

        startRecording();
        recordIndicator.show();
    }

    private void finishRecord() {
        stopRecording();
        recordIndicator.dismiss();

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            ToastUtils.showToast(R.string.so_short_tip, 0);
            File file = new File(mFileName);
            file.delete();
            return;
        }

        if (finishedListener != null)
            finishedListener.onFinishedRecord(mFileName);
    }

    private void cancelRecord() {
        recordTime = 0;
        stopRecording();
        recordIndicator.dismiss();
        File file = new File(mFileName);
        if (file.exists()) {
            file.delete();
        } else if (newFile != null && newFile.exists()) {//删除重命名后的文件
            newFile.delete();
        }

        if (finishedListener != null)
            finishedListener.onFinishedRecord(mFileName);
    }

    private void startRecording() {
        recordTime = 0;

        String current = new Date().getTime() + "";//已当前系统时间戳作为录音名称
        Log.e("eeeeeeeeeeeeeeeeeee", "current:" + current);
        mFileName = new File(Tools.getvoiceSDPath() + "/" + current + ".amr").getPath();
        File file = new File(mFileName);
        Log.e("eeeeeeeeeeee", "file:" + file);
        if (file.exists()) {
            Log.e("eeeeeeeeeeee", "file存在");
            file.delete();
        }

        recorder = new MediaRecorder();
//        recorder.setMaxDuration(60000);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(mFileName);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        thread = new ObtainDecibelThread();
        thread.start();

    }

    private void stopRecording() {

        if (thread != null) {
            thread.exit();
            thread = null;
        }
        if (recorder != null) {
            recorder.setOnErrorListener(null);
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    public void play(MediaPlayer mPlayer, String fileName) {

        if (fileName != null && fileName.length() > 0) {
            try {
                File file = aduio.getFile(fileName);
                if (file != null) {
                    mPlayer.setDataSource(file.getPath());
                    mPlayer.prepare();
                } else {
                    aduio.asyncDownAudio(fileName);
                    mPlayer.setDataSource(fileName);
                    mPlayer.prepareAsync();
                }
                mPlayer.start();

                mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e("eeeeeeeeeeee", "播放失败");


//                        MyToast.makeTextAnim(mContext, R.string.play_erorr, 0, R.style.PopToast).show();
                        return false;
                    }
                });

            } catch (IOException e) {
                Log.e("eeeeeeeeeeee", "播放失败");
            }
        }
    }

    public void cacheAudio(String fileName) {
        if (fileName != null && fileName.length() > 0) {

        }
    }

    //返回录音时间
    public int getVoicetTime() {
        return recordTime / 5;
    }

    public interface OnFinishedRecordListener {
        void onFinishedRecord(String audioPath);
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                recordTime++;
                if (recordTime == 5 * 60) {//最长录音时间为60秒
                    Log.e("eeeeeeeeeeee", "录音到达60s");
                    volumeHandler.sendEmptyMessage(-1);
                    return;
                }
                Log.e("eeeeeeeeeeee", "录音时间:" + recordTime / 5);
                int x = recorder.getMaxAmplitude();
                if (x != 0) {
                    int f = (int) (10 * Math.log(x) / Math.log(10));
                    if (f < 26)
                        volumeHandler.sendEmptyMessage(0);
                    else if (f < 32)
                        volumeHandler.sendEmptyMessage(1);
                   /* else if (f < 38)
                        volumeHandler.sendEmptyMessage(2);*/
                    else
                        volumeHandler.sendEmptyMessage(2);

                }

            }
        }

    }

    class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == -1) {
                finishRecord();
            } else {
                mic_img.setImageResource(res[what]);
            }
        }
    }


}
