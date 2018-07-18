package com.bber.company.android.listener;

import android.content.Context;

import com.bber.company.android.app.MyApplication;
import com.bber.company.android.constants.Constants;
import com.bber.customview.utils.LogUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * @author
 * @version 2016/02/25 14:11
 */

public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    private Context context = MyApplication.getContext();
    private ChannelHandlerContext mctx;
    //这里是断线要进行的操作
    private NettyListener nettyListener;

    public void setNettyListener(NettyListener nettyListener) {
        this.nettyListener = nettyListener;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LogUtils.e("断线了。---------");
        Constants.ISLink = true;
        Constants.ISCONTACT = true;
        nettyListener.onDisConnected(ctx);
        mctx.close();
        this.mctx = null;
    }

    //这里是出现异常的话要进行的操作
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        LogUtils.e("出现异常了。。。。。。。。。。。。。" + cause.getMessage());
        Constants.ISLink = true;
        Constants.ISCONTACT = true;
//        mctx.close();
        nettyListener.onExceptionconnected(ctx);
        cause.printStackTrace();
    }

    //这里是接受服务端发送过来的消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String msg1 = msg.trim();
        nettyListener.onResponse(ctx, msg1);
        ReferenceCountUtil.release(msg1);
    }

    //连接成功
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client active ");
        LogUtils.e("Client active ");
        this.mctx = ctx;
        super.channelActive(ctx);
//        setHeartInfo(1);
//延迟2s后发送首次心跳数据或者登陆数据
//        Observable.timer(2, TimeUnit.SECONDS)
//                .subscribe(l->{
//                    sendHeartData();
//                },e->{
//                    MyLogger.e(TAG, "===发送心跳异常=== e = "+e.toString());
//                    ctx.channel().close();
//                });
    }

    public boolean isLink() {

        return mctx != null;
    }

    //发送数据
    public boolean sendMsg(String message) {
        if (context != null) {
            try {
                LogUtils.e("sendMsg = " + mctx.isRemoved());
                LogUtils.e("sendMsg = " + message);
                mctx.channel().writeAndFlush(message);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
