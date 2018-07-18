package com.bber.company.android.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.bber.customview.utils.LogUtils;

/**
 * Created by bn on 2017/8/2.
 */

public class UnlimitViewPager extends ViewPager {
    public UnlimitViewPager(Context context) {
        super(context);
    }

    public UnlimitViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void startGmae(final int position) {
        final Object lock = new Object();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Integer around_ = 0;
                Integer speed_ = 40;
                while (true) {
                    for (int i = 1; i < 10; i++) {
                        if (around_ > 0 && around_ <= 9) {
                            speed_ = 40;
                            if (position < 2 && around_ == 9) {
                                Integer slow = position + 6;
                                if (i >= slow) {
                                    speed_ = 200;
                                }
                            }
                        }
                        LogUtils.e("当前第[" + around_ + "]圈,第[" + i + "],速度[" + speed_ + "]");
                        synchronized (lock) {
                            try {
                                Thread.sleep(speed_);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            final int finalI = i;
                            post(new Runnable() {
                                @Override
                                public void run() {
//                                         if (finalI == 7){
//                                             setCurrentItem(0,false);
//                                         }else {
                                    setCurrentItem(finalI);
//                                         }

                                }
                            });

                        }
                    }
                    around_++;
                    if (around_ > 9) {
                        break;
                    }
                }

                for (int i = 1; i <= position; i++) {
                    if (position >= 2) {
                        if (i == position - 2) {
                            speed_ = 200;
                        }
                        if (i == position) {
                            speed_ = 300;
                        }
                    } else if (position < 2) {
                        if (i <= position) {
                            speed_ = 350;
                        }
                    }
//                    LogUtils.e("当前第最后一圈,第["+i+"],速度["+ speed_ +"]");
                    synchronized (lock) {
                        try {
                            Thread.sleep(speed_ + 20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        final int finalI = i;
                        post(new Runnable() {
                            @Override
                            public void run() {
//                                if (finalI == 7){
//                                    setCurrentItem(0,false);
//                                }else {
                                setCurrentItem(finalI);
//                                }
                            }
                        });
                    }
                }
            }
        }).start();
    }
}
