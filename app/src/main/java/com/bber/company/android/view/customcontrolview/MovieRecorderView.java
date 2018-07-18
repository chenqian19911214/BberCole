package com.bber.company.android.view.customcontrolview;

/**
 * Created by carlo.c on 2018/4/13.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.app.MyApplication;
import com.bber.company.android.tools.imageload.ImageFileCache;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/12/9 0009.
 */
public class MovieRecorderView extends LinearLayout implements MediaRecorder.OnErrorListener {

    private String videoPath = Environment.getExternalStorageDirectory().getPath() + "/bber/bber.mp4";
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    //    private ProgressBar mProgressBar;
    private TextView time_num;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口
    private int mWidth;// 视频分辨率宽度
    private int mHeight;// 视频分辨率高度
    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mVecordFile = null;// 文件

    public MovieRecorderView(Context context) {
        this(context, null);
    }

    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mWidth = 640;
        mHeight = 480;
//        mWidth = 320;
//        mHeight = 240;
        isOpenCamera = true;
        mRecordMaxTime = 9;

        LayoutInflater.from(context).inflate(R.layout.moive_recorder_view, this);
        time_num = (TextView) findViewById(R.id.time_num);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        mProgressBar.setMax(mRecordMaxTime);// 设置进度条最大量
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    /**
     * @author liuyinjun
     * @date 2015-2-5
     */
    private class CustomCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
            try {

                initCamera();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//                }
//            }).start();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            freeCameraResource();
        }

    }

    /**
     * 初始化摄像头
     *
     * @throws IOException
     * @author lip
     * @date 2015-3-16
     */
    private void initCamera() throws IOException {
        if (mCamera != null) {
            freeCameraResource();
        }
        try {
            mCamera = Camera.open(1);
            //mCamera.
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }
        if (mCamera == null)
            return;

        setCameraParams();
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
        mCamera.unlock();
//        handler.obtainMessage(1).sendToTarget();
    }

    /**
     * 设置摄像头为竖屏
     *
     * @author lip
     * @date 2015-3-16
     */
    private void setCameraParams() {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            params.set("orientation", "portrait");

            List<Camera.Size> supportedPreviewSizes
                    = params.getSupportedVideoSizes();
            Camera.Size previewSize = null;
            if (supportedPreviewSizes != null &&
                    supportedPreviewSizes.size() > 0) {
                previewSize = getOptimalPreviewSize(supportedPreviewSizes);
                params.setPreviewSize(previewSize.width, previewSize.height);
                mWidth = previewSize.width;
                mHeight = previewSize.height;
                mCamera.setParameters(params);

            }

        }
        intSuffaceSize(mHeight, mWidth);
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) MyApplication.screenWidth / MyApplication.screenHeigth;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = MyApplication.screenHeigth;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            if (size.width <= 700) {
                optimalSize = size;
                break;
            }
            if (size.width <= 600) {
                optimalSize = size;
                break;
            }
            if (size.width <= 500) {
                optimalSize = size;
                break;
            }
            if (size.width <= 400) {
                optimalSize = size;
                break;
            }
        }
        return optimalSize;
    }

    private void intSuffaceSize(float width, float height) {
        final int screenHeight = MyApplication.screenHeigth;
        final int screenWidth = MyApplication.screenWidth;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mSurfaceView
                .getLayoutParams();
        if (screenHeight - height > screenWidth - width) {
            // 高度适配
            params.width = (int) (width * (screenHeight / height));
            params.height = screenHeight;
        } else {
            // 宽度适配
            params.width = screenWidth;
            params.height = (int) (height * (screenWidth / width));
        }
        // 这里需要注意的一点是，如果surface的布局不是framelayout的话，需要同时改变它的父layout的width和height才有效果
        params.gravity = Gravity.CENTER;
        mSurfaceView.setLayoutParams(params);
        mSurfaceView.requestLayout();
    }

    /**
     * 释放摄像头资源
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void freeCameraResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;

            //  mCamera.takePicture();
        }
    }

    /**
     * 初始化
     *
     * @throws IOException
     * @author lip
     * @date 2015-3-16
     */

    private void initRecord() throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        try {
            if (mCamera != null) {
                mMediaRecorder.setCamera(mCamera);
                mMediaRecorder.setOnErrorListener(this);
                mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
                // mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);// 音频源
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 音频格式
                mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 1024);// 设置帧频率
                mMediaRecorder.setOrientationHint(270);// 输出旋转90度，保持竖屏录制
//              mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);// 视频录制格式

                mVecordFile = new ImageFileCache().initUploadVoideFile();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mMediaRecorder.setOutputFile(mVecordFile);
                }else {
                    mMediaRecorder.setOutputFile(videoPath);
                }
                mMediaRecorder.prepare();
            }
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录制视频
     *
     * @param onRecordFinishListener 达到指定时间之后回调接口
     * @author liuyinjun
     * @date 2015-2-5
     * //     * @param fileName
     * //     *            视频储存位置
     */
    public void record(final OnRecordFinishListener onRecordFinishListener) {
        time_num.setVisibility(VISIBLE);
        this.mOnRecordFinishListener = onRecordFinishListener;
        try {
            if (!isOpenCamera)// 如果未打开摄像头，则打开
                initCamera();
            initRecord();
//            mTimeCount = 0;// 时间计数器重新赋值
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mRecordMaxTime--;
                    if (mRecordMaxTime != -1) {
                        handler.obtainMessage(0).sendToTarget();
                    }
//                    mProgressBar.setProgress(mTimeCount);// 设置进度条
                    if (-1 == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    time_num.setText(mRecordMaxTime + "");
                    break;
                case 1:
                    intSuffaceSize(mHeight, mWidth);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 停止拍摄
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    /**
     * 停止录制
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    public void stopRecord() {
//        mProgressBar.setProgress(0);
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 释放资源
     *
     * @author liuyinjun
     * @date 2015-2-5
     */
    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public int getTimeCount() {
        return mTimeCount;
    }

    /**
     * @return the mVecordFile
     */
    public String getmVecordFile() {
        return videoPath;
    }

    /**
     * 录制完成回调接口
     *
     * @author lip
     * @date 2015-3-16
     */
    public interface OnRecordFinishListener {
        public void onRecordFinish();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
