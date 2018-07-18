package com.bber.company.android.view.activity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bber.company.android.R;
import com.bber.company.android.bean.DiscussBean;
import com.bber.company.android.tools.FlurryTypes;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.widget.MyZoomImageView;

/**
 * 浏览原图
 * Created by Administrator on 15-5-7.
 */
public class BrowseImageActivity extends BaseAppCompatActivity {

    //单击退出页面
    View.OnTouchListener myOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:


                    break;

                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:

                    finish();
                    break;
                default:

                    break;
            }
            return false;
        }
    };
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
    private float startX = 0, startY = 0, endX = 0, endY = 0;
    private ProgressBar progressBar;
    private RelativeLayout view;
    private ViewPager viewPager;
    private LinearLayout ll_discrible;
    private TextView tv_tittle, tv_picnum, tv_decrible;
    private View[] imageViews = null;
    //    private AsyncImageLoader asyncImageLoader;
    private int currentIndex;
    private String[] projectImages;
    private Bitmap[] projectImagesbitmap;
    private DiscussBean discussBean;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    viewPager.setAdapter(new MyAdapter());
                    viewPager.setCurrentItem(currentIndex);
//                    setTitle((currentIndex + 1) + "/" + +projectImages.size());
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_browse;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        asyncImageLoader = new AsyncImageLoader(this);
        initViews();
        setListeners();
        initData();
    }

    /**
     * 初始化组件
     */
    private void initViews() {
        view = findViewById(R.id.view);
        viewPager = findViewById(R.id.viewPager);
        ll_discrible = findViewById(R.id.ll_discrible);
        tv_picnum = findViewById(R.id.tv_picnum);
        tv_tittle = findViewById(R.id.tv_tittle);
        tv_decrible = findViewById(R.id.tv_decrible);
        progressBar = findViewById(R.id.progressBar);
        toolbar.setVisibility(View.GONE);
    }

    /* 设置监听事件 */
    private void setListeners() {
        viewPager.setOnPageChangeListener(new MyListener());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 初始化数据
     * *
     */
    private void initData() {
        currentIndex = getIntent().getIntExtra("currentIndex", 0);
        discussBean = (DiscussBean) getIntent().getSerializableExtra("discussBean");
        LayoutInflater inflater = LayoutInflater.from(BrowseImageActivity.this);

        try {
            projectImages = getIntent().getStringArrayExtra("images");
        }catch (Exception e){


            projectImagesbitmap = new Bitmap[getIntent().getStringArrayExtra("images").length];
           // projectImagesbitmap = getIntent().getStringArrayExtra("images");
            e.getMessage();
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {

        imageViews = new View[projectImages.length];
        for (int i = 0; i < projectImages.length; i++) {
            View view = inflater.inflate(R.layout.browse_view_item, null);
//                    SimpleDraweeView imageView = (SimpleDraweeView) view.findViewById(R.id.image);
//                    imageView.setOnTouchListener(new TouchListener());
            imageViews[i] = view;
        }
        handler.obtainMessage(0).sendToTarget();
//            }
//        }).start();

    }

    @Override
    public void onDestroy() {
        Log.e("eeeeeeeeeeee", "image---onDestroy");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryTypes.onStartSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryTypes.onEndSession(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(imageViews[position % imageViews.length]);

        }

        @Override
        public Object instantiateItem(final View container, final int position) {
            final View view = imageViews[position];
            String url = projectImages[position];
            final MyZoomImageView imageView = view.findViewById(R.id.image);
            imageView.setActivity(BrowseImageActivity.this);
            imageView.setAspectRatio(0.5f);

            String imageurltou = (String) SharedPreferencesUtils.get(getApplicationContext(), "IMAGE_FILE", "");
            String uitt = imageurltou + url;
            Uri uri = Uri.parse(uitt);
            imageView.setImageURI(uri);

            ((ViewPager) container).removeView(imageViews[position % imageViews.length]);
            ((ViewPager) container).addView(imageViews[position % imageViews.length]);

            return imageViews[position % imageViews.length];
        }


    }

    private class MyListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
            if (discussBean != null) {
                tv_picnum.setText(arg0 + 1 + "/" + discussBean.getImg().size());
                ll_discrible.setVisibility(View.VISIBLE);
                tv_tittle.setText(discussBean.getTitle());
                if (discussBean.getImg().size() > arg0) {
                    tv_decrible.setText(discussBean.getImg().get(arg0).getDetail());
                }
            }

        }

        @Override
        public void onPageSelected(int position) {
//            setTitle((position + 1) + "/" + projectImages.size());
        }


    }

    private final class TouchListener implements View.OnTouchListener {

        static final float MAX_SCALE = 2.5f;// 最大缩放比例
        /**
         * 拖拉照片模式
         */
        private static final int MODE_DRAG = 1;
        /**
         * 放大缩小照片模式
         */
        private static final int MODE_ZOOM = 2;
        float minScaleR = 1f;// 最小缩放比例
        /**
         * 记录是拖拉照片模式还是放大缩小照片模式
         */
        private int mode = 0;// 初始状态
        /**
         * 用于记录开始时候的坐标位置
         */
        private PointF startPoint = new PointF();
        /**
         * 用于记录拖拉图片移动的坐标位置
         */
        private Matrix matrix = new Matrix();
        /**
         * 用于记录图片要进行拖拉时候的坐标位置
         */
        private Matrix currentMatrix = new Matrix();
        /**
         * 两个手指的开始距离
         */
        private float startDis;
        /**
         * 两个手指的中间点
         */
        private PointF midPoint;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
            ImageView imageView = imageViews[viewPager.getCurrentItem()].findViewById(R.id.image);
//            ImageView imageView = imageViews[viewPager.getCurrentItem()];

            Log.e("eeeeeeeeeeee", "onTouch:");
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                // 手指压下屏幕
                case MotionEvent.ACTION_DOWN:
                    mode = MODE_DRAG;
                    // 记录ImageView当前的移动位置
                    currentMatrix.set(imageView.getImageMatrix());
                    startPoint.set(event.getX(), event.getY());

                    startX = event.getX();
                    startY = event.getY();
                    break;
                // 手指在屏幕上移动，改事件会被不断触发
                case MotionEvent.ACTION_MOVE:
                    // 拖拉图片
                    if (mode == MODE_DRAG) {
                        float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                        float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
                        // 在没有移动之前的位置上进行移动
                        matrix.set(currentMatrix);
                        matrix.postTranslate(dx, dy);
                    } else


                        // 放大缩小图片
                        if (mode == MODE_ZOOM) {
                            imageView.setScaleType(ImageView.ScaleType.MATRIX);
                            float endDis = distance(event);// 结束距离
                            float scale = endDis / startDis;// 得到缩放倍数
                            if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10

//                                float p[] = new float[9];
//                                matrix.getValues(p);
//                                if (p[0] < minScaleR) {
//                                    matrix.setScale(minScaleR, minScaleR);
//                                }
//                                Log.e("eeeeeeeeeeee","scale: " + scale+ "--p[0]: " + p[0]
//                                );
//                                if (p[0] < MAX_SCALE) {
//
                                matrix.set(currentMatrix);
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
//                                }

                                break;
                            }
                        }

                    break;
                // 手指离开屏幕
                case MotionEvent.ACTION_UP:
                    // 当触点离开屏幕，但是屏幕上还有触点(手指)
                    endX = event.getX();
                    endY = event.getY();

                    Log.e("eeeeeeeeeeeeeeee", "Y:" + Math.abs(startY - endY) + "---X:" + Math.abs(startX - endX));
                    if (Math.abs(startX - endX) <= 5 && Math.abs(startY - endY) <= 5) {
                        finish();
                    }


                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = 0;
                    break;
                // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = MODE_ZOOM;
                    /** 计算两个手指间的距离 */
                    startDis =

                            distance(event);
                    /** 计算两个手指间的中间点 */
                    if (startDis > 10f)

                    { // 两个手指并拢在一起的时候像素大于10
                        midPoint = mid(event);
                        //记录当前ImageView的缩放倍数
                        currentMatrix.set(imageView.getImageMatrix());
                    }

                    break;
            }

            imageView.setImageMatrix(matrix);
            return true;
        }

        /**
         * 限制最大最小缩放比例，自动居中
         */
        private void CheckView() {
            float p[] = new float[9];
            matrix.getValues(p);
            Log.e("eeeeeeeeeeee", "--p[0]: " + p[0]);
            if (mode == MODE_ZOOM) {
//                if (p[0] < minScaleR) {
//                    matrix.setScale(minScaleR, minScaleR);
//                }
                if (p[0] > MAX_SCALE) {
                    matrix.set(currentMatrix);
                }
            }
        }

        /**
         * 计算两个手指间的距离
         */

        private float distance(MotionEvent event) {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            /** 使用勾股定理返回两点之间的距离 */
            return (float) Math.sqrt(dx * dx + dy * dy);
        }

        /**
         * 计算两个手指间的中间点
         */
        private PointF mid(MotionEvent event) {
            float midX = (event.getX(1) + event.getX(0)) / 2;
            float midY = (event.getY(1) + event.getY(0)) / 2;
            return new PointF(midX, midY);
        }


    }
}
