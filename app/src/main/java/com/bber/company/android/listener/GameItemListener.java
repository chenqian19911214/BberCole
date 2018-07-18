package com.bber.company.android.listener;

/**
 * Created by Vencent on 2017/2/20.
 * Description: 与业务系统对接回调泛型接口
 */
public interface GameItemListener<T> {
    /***
     * Item1的回调
     * @param t
     */
    void onItemView1(T t);

    /***
     * Item2的回调
     * @param t
     */
    void onItemView2(T t);

    void onItemView3(T t);

    void onItemView4(T t);

    void onItemView5(T t);

    void onItemView6(T t);

    void onItemView7(T t);

    void onItemView8(T t);
}
