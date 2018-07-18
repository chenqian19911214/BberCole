package com.bber.company.android.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bber.company.android.R;
import com.bber.company.android.listener.GameItemListener;
import com.bber.company.android.listener.IactionListener;
import com.bber.company.android.util.OtherUtils;
import com.bber.customview.utils.LogUtils;

/**
 * Created by jeanboy on 2017/4/20.
 */

public class LuckyMonkeyPanelView extends FrameLayout {


    private static final int DEFAULT_SPEED = 200;
    private static final int MIN_SPEED = 50;
    public IactionListener iactionListener;
    private ImageView bg_1;
    private ImageView bg_2;
    private PanelItemView itemView1, itemView2, itemView3,
            itemView4, itemView6,
            itemView7, itemView8, itemView5;
    private ItemView[] itemViewArr = new ItemView[8];
    private PanelItemView[] PaneViewArr = new PanelItemView[8];
    private int currentIndex = 0;
    private int currentTotal = 0;
    private int stayIndex = 0;
    private boolean isMarqueeRunning = false;
    private boolean isGameRunning = false;
    private boolean isTryToStop = false;
    private int currentSpeed = 350;
    private View fab;
    private GameItemListener gameListener;
    private Context mContext;
    private AnimatorPath path;//声明动画集合
    private int time = 0;
    private Handler MyHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    isGameRunning = false;
//                    itemViewArr[currentIndex].setFocus(false);
//                    itemViewArr[preIndex].setFocus(true);
//                    itemViewArr[stayIndex].setFocus(true);
                    break;
                case 2:
                    iactionListener.SuccessCallback("");
                    break;
                default:
                    break;
            }
        }
    };

    public LuckyMonkeyPanelView(@NonNull Context context) {
        this(context, null);
    }

    public LuckyMonkeyPanelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyMonkeyPanelView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        inflate(context, R.layout.view_lucky_mokey_panel, this);
        setupView();
    }

    public void setGameItemListener(GameItemListener gameListener) {
        this.gameListener = gameListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        startMarquee();
    }

    @Override
    protected void onDetachedFromWindow() {
//        stopMarquee();
        super.onDetachedFromWindow();
    }

    private void setupView() {
//        bg_1 = (ImageView) findViewById(R.id.bg_1);
//        bg_2 = (ImageView) findViewById(R.id.bg_2);
        itemView1 = findViewById(R.id.item1);
        itemView2 = findViewById(R.id.item2);
        itemView3 = findViewById(R.id.item3);
        itemView4 = findViewById(R.id.item4);
        itemView5 = findViewById(R.id.item5);
        itemView6 = findViewById(R.id.item6);
        itemView7 = findViewById(R.id.item7);
        itemView8 = findViewById(R.id.item8);
        itemView1.setModelNumber(getResources().getDrawable(R.drawable.icon_model_1));
        itemView2.setModelNumber(getResources().getDrawable(R.drawable.icon_model_2));
        itemView3.setModelNumber(getResources().getDrawable(R.drawable.icon_model_3));
        itemView4.setModelNumber(getResources().getDrawable(R.drawable.icon_model_4));
        itemView5.setModelNumber(getResources().getDrawable(R.drawable.icon_model_5));
        itemView6.setModelNumber(getResources().getDrawable(R.drawable.icon_model_6));
        itemView7.setModelNumber(getResources().getDrawable(R.drawable.icon_model_7));
        itemView8.setModelNumber(getResources().getDrawable(R.drawable.icon_model_8));
        fab = findViewById(R.id.overlay);

        itemView1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("1111111111");
                gameListener.onItemView1("");
            }
        });
        itemView2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("222222222");
                gameListener.onItemView2("");
            }
        });
        itemView3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("333333333");
                gameListener.onItemView3("");
            }
        });
        itemView4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("44444444444");
                gameListener.onItemView4("");
            }
        });
        itemView5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("6666666666");
                gameListener.onItemView5("");
            }
        });
        itemView6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("7777777777");
                gameListener.onItemView6("");
            }
        });
        itemView7.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("8888888888");
                gameListener.onItemView7("");
            }
        });
        itemView8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e("9999999999");
                gameListener.onItemView8("");
            }
        });
        itemViewArr[0] = itemView1;
        itemViewArr[1] = itemView2;
        itemViewArr[2] = itemView3;
        itemViewArr[3] = itemView4;
        itemViewArr[4] = itemView5;
        itemViewArr[5] = itemView6;
        itemViewArr[6] = itemView7;
        itemViewArr[7] = itemView8;

        PaneViewArr[0] = itemView1;
        PaneViewArr[1] = itemView2;
        PaneViewArr[2] = itemView3;
        PaneViewArr[3] = itemView4;
        PaneViewArr[4] = itemView5;
        PaneViewArr[5] = itemView6;
        PaneViewArr[6] = itemView7;
        PaneViewArr[7] = itemView8;


    }

    //设置背景图
    public void setbackgirl(int position, String url) {
        PaneViewArr[position].setModelBack(url);

    }

    public void setVisFlase(int position) {
        if (PaneViewArr[position].getVisibility() != View.VISIBLE) {
            PaneViewArr[position].setFocus(false);
        }
    }

    //设置黄色的字体
    public void setYellowWord(int position) {
        PaneViewArr[position].setYellow();
    }

    //设置紫色的字体
    public void setALLWord() {
        for (int i = 0; i < PaneViewArr.length; i++) {
            PaneViewArr[i].setPurper();
        }
    }

    public void setAlpha_(int position, float aph) {
        PaneViewArr[position].setAlpha_(aph);
    }

    public void setVisTrue(int position) {
        PaneViewArr[position].setFocus(true);
    }

    public void setUserName(int position, String name) {
        PaneViewArr[position].setUseName(name);
    }

    public void setUserNameText(String name) {
        for (int i = 0; i < PaneViewArr.length; i++) {
            PaneViewArr[i].setUseName(name);
        }
    }

    public void setChooseTrue(int position) {
        PaneViewArr[position].setChooseTrue();
    }

    public void setChooseFlase(int position) {
        PaneViewArr[position].setChooseFlase();
    }

    public void setAllChooseFlase() {
        for (int i = 0; i < PaneViewArr.length; i++) {
            PaneViewArr[i].setOrgialModel(R.drawable.unknow_model);
            PaneViewArr[i].setFocus(true);
        }
    }

    public boolean isSeclect(int position) {
        return PaneViewArr[position].isChoose;
    }

    public boolean checkALLChoose() {
        int y = 0;
        for (int i = 0; i < PaneViewArr.length; i++) {
            if (PaneViewArr[i].isChoose) {
                y++;
                if (y == 8) {
                    return true;
                }
            }
        }
        return false;
    }

    private void stopMarquee() {
        isMarqueeRunning = false;
        isGameRunning = false;
        isTryToStop = false;
    }

    private void startMarquee() {
        isMarqueeRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isMarqueeRunning) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (bg_1 != null && bg_2 != null) {
                                if (VISIBLE == bg_1.getVisibility()) {
                                    bg_1.setVisibility(GONE);
                                    bg_2.setVisibility(VISIBLE);
                                } else {
                                    bg_1.setVisibility(VISIBLE);
                                    bg_2.setVisibility(GONE);
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }

    //    private long getInterruptTime() {
//        currentTotal++;
//        if (isTryToStop) {
//            currentSpeed += 10;
//            if (currentSpeed > DEFAULT_SPEED) {
//                currentSpeed = DEFAULT_SPEED;
//            }
//        } else {
//            if (currentTotal / itemViewArr.length > 0) {
//                currentSpeed -= 10;
//            }
//            if (currentSpeed < MIN_SPEED) {
//                currentSpeed = MIN_SPEED;
//            }
//        }
//        return currentSpeed;
//    }
    private long getInterruptTime() {
        currentTotal++;
        if (isTryToStop) {
            currentSpeed += 10;
            if (currentSpeed > DEFAULT_SPEED) {
                currentSpeed = DEFAULT_SPEED;
            }
        } else {
            if (currentTotal > 4) {
                currentSpeed -= 2;
            }
            if (currentSpeed < MIN_SPEED) {
                currentSpeed = MIN_SPEED;
            }
        }
        return currentSpeed;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void startGame() {
        isGameRunning = true;
        isTryToStop = false;
        currentSpeed = DEFAULT_SPEED;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isGameRunning) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (isGameRunning) {
                                int preIndex = currentIndex;
                                currentIndex++;
                                if (currentIndex >= itemViewArr.length) {
                                    currentIndex = 0;
                                }

                                itemViewArr[currentIndex].setFocus(false);
                                itemViewArr[preIndex].setFocus(true);
//                            itemViewArr[currentIndex].setFocus(true);

                                if (isTryToStop && currentSpeed == DEFAULT_SPEED && stayIndex == currentIndex) {
                                    Message message = new Message();
                                    message.what = 1;
                                    MyHandler.sendMessage(message);
                                    LogUtils.e("stayIndex停下来的数字：" + stayIndex);
                                }
                            }

                        }
                    });
                }
            }
        }).start();


    }

    public void startAm(int wn_) {
        path = new AnimatorPath();
        path.moveTo(0, 0);
        setCycle(6);
        setFinallyStop(wn_);
        ObjectAnimator anim = ObjectAnimator.ofObject(this, "fab", new PathEvaluator(), path.getPoints().toArray());
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.setDuration(4000);
        anim.start();
    }

    public void setFabVisable() {
        fab.setVisibility(View.VISIBLE);
    }

    public void setFabGone() {
        fab.setVisibility(View.GONE);
    }

    public void setCycle(int cycle) {
        LogUtils.e("实际上的DP = " + OtherUtils.dip2px(mContext, 240));

        for (int i = 0; i < cycle; i++) {
            path.lineTo(OtherUtils.dip2px(mContext, 240), 0);
            path.lineTo(OtherUtils.dip2px(mContext, 240), OtherUtils.dip2px(mContext, 240));
            path.lineTo(0, OtherUtils.dip2px(mContext, 240));
            path.lineTo(0, 0);
        }
    }

    public void setFinallyStop(int position) {
        //前三位
        time = 0;
        if (position >= 0 && position < 3) {
            path.lineTo(position * OtherUtils.dip2px(mContext, 120), 0);
            time = 200;
            return;
        } else {
            path.lineTo(OtherUtils.dip2px(mContext, 240), 0);
            time = 200;
        }
        //右边3、4、位置
        if (position > 2 && position < 5) {
            time = time + 300;
            path.lineTo(OtherUtils.dip2px(mContext, 240), (position - 2) * OtherUtils.dip2px(mContext, 120));
            return;
        } else {
            time = time + 300;
            path.lineTo(OtherUtils.dip2px(mContext, 240), OtherUtils.dip2px(mContext, 240));
        }
        if (position > 4 && position < 7) {
            path.lineTo((Math.abs(position - 6)) * OtherUtils.dip2px(mContext, 120), OtherUtils.dip2px(mContext, 240));
            time = time + 300;
            return;
        } else {
            path.lineTo(0, OtherUtils.dip2px(mContext, 240));
            time = time + 300;
        }

        if (position > 6 && position < 8) {
            path.lineTo(0, OtherUtils.dip2px(mContext, 120));
            time = time + 300;
            return;
        } else {
            path.lineTo(0, 0);
            time = time + 300;
        }


    }

    /**
     * 设置View的属性通过ObjectAnimator.ofObject()的反射机制来调用
     *
     * @param newLoc
     */
    public void setFab(PathPoint newLoc) {
        fab.setTranslationX(newLoc.mX);
        fab.setTranslationY(newLoc.mY);
    }

    public void startGame(final int wn_) {
        isGameRunning = true;
        isTryToStop = false;
        currentSpeed = DEFAULT_SPEED;
        final Object lock = new Object();
        currentIndex = 0;//每次开始让currentIndex为第一个
        new Thread(new Runnable() {
            @Override
            public void run() {

                Integer around_ = 0;
                Integer speed_ = 240;
                while (true) { //固定转5圈
                    for (int run_ = 0; run_ < itemViewArr.length; run_++) {
                        if (around_ < 1) {
                            //speed_ = speed_ - (4*(around_+1));
                            if (run_ < 3) {

                            } else {
                                speed_ = 50;
                            }
                        } else if (around_ >= 1 && around_ <= 5) {
                            //speed_ = 200 - (2*(around_+1));
                            speed_ = 50;
                            if (wn_ < 2 && around_ == 5) {
                                Integer slow = wn_ + 6;
                                if (run_ >= slow) {
                                    speed_ = 240;
                                }
                            }
                        }

                        LogUtils.e("当前第[" + around_ + "]圈,第[" + run_ + "],速度[" + speed_ + "]");
                        synchronized (lock) {
                            try {
                                Thread.sleep(speed_);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isGameRunning) {
                                        int preIndex = currentIndex;
                                        currentIndex++;
                                        if (currentIndex >= itemViewArr.length) {
                                            currentIndex = 0;
                                        }
                                        //0.1 0.2 0.3 0.4 0.5
                                        setAlpha_(currentIndex, 0.1f);
                                        itemViewArr[currentIndex].setFocus(false);
                                        setAlpha_(currentIndex, 0.1f);
                                        setAlpha_(currentIndex, 0.2f);
                                        setAlpha_(currentIndex, 0.3f);
                                        setAlpha_(currentIndex, 0.4f);
                                        setAlpha_(currentIndex, 0.5f);
                                        setAlpha_(currentIndex, 0.6f);
                                        setAlpha_(currentIndex, 0.7f);
                                        setAlpha_(currentIndex, 0.8f);
                                        setAlpha_(currentIndex, 0.9f);
                                        setAlpha_(currentIndex, 1f);
                                        //0.6 0.7 0.8 0.9 1
                                        itemViewArr[preIndex].setFocus(true);
                                    }
                                }
                            });
                        }
                    }
                    around_++;
                    if (around_ > 5) {
                        break;
                    }
                }
                //转到开奖号码
                for (int run_ = 0; run_ < wn_; run_++) {
                    //speed_ = speed_ + ((10-wn_) * 2);
                    /*if(run_ > wn_-3)
                        speed_ = speed_+30;*/
                    if (wn_ >= 2) {
                        if (run_ == wn_ - 2) {
                            speed_ = 240;
                        }
                        if (run_ == wn_) {
                            speed_ = 450;
                        }
                    } else if (wn_ < 2) {
                        if (run_ == wn_) {
                            speed_ = 450;
                        }
                    }
                    LogUtils.e("当前第最后一圈,第[" + run_ + "],速度[" + speed_ + "]");
                    try {
                        Thread.sleep(speed_);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (lock) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                if (isGameRunning) {
                                    int preIndex = currentIndex;
                                    currentIndex++;
                                    if (currentIndex >= itemViewArr.length) {
                                        currentIndex = 0;
                                    }
                                    itemViewArr[currentIndex].setFocus(false);
                                    itemViewArr[preIndex].setFocus(true);
                                }
                            }
                        });
                    }
                }
                Message message = new Message();
                message.what = 2;
                MyHandler.sendMessage(message);
            }
        }).start();

//        while (true) {
//            if (OtherUtils.getNowStop()) {
//                break;
//            }
//        }
    }

    public void tryToStop(int position) {
        stayIndex = position;
        isTryToStop = true;

        int preIndex = currentIndex;
        currentIndex++;
        LogUtils.e(Thread.currentThread());
        Integer m_ = 0;
        while (m_ < 3) {
            for (int ivl_ = 0; ivl_ < PaneViewArr.length; ivl_++) {
                PaneViewArr[ivl_].setFocus(false);
                if (ivl_ != 0) {
                    PaneViewArr[ivl_ - 1].setFocus(true);
                }
                if (ivl_ == 0) {
                    PaneViewArr[PaneViewArr.length - 1].setFocus(true);
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
            m_++;
        }

        for (int st_ = 0; st_ <= stayIndex; st_++) {
            PaneViewArr[st_].setFocus(false);
            if (st_ != 0) {
                PaneViewArr[st_ - 1].setFocus(true);
            }
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(200);


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }

    public void setIactionListener(IactionListener iactionListener) {
        this.iactionListener = iactionListener;
    }
}
