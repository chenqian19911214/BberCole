package com.bber.company.android.listener;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by bn on 2017/6/19.
 */

public interface NettyListener {
    //这里是断线要进行的操作
    void onDisConnected(ChannelHandlerContext ctx);

    //这里是异常的操作
    void onExceptionconnected(ChannelHandlerContext ctx);

    // //这里是接受服务端发送过来的消息
    void onResponse(ChannelHandlerContext ctx, String msg);
}
