package com.bber.company.android.listener;

/**
 * Created by Vencent on 2017/2/20.
 * Description: 与业务系统对接回调泛型接口
 */
public interface IactionListener<T> {
    /***
     * 成功回调
     * @param t
     */
    void SuccessCallback(T t);

    /**
     * 失败回调
     *
     * @param result
     */
    void FailCallback(String result);
}
