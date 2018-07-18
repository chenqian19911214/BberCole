package com.bber.company.android.listener;


import com.bber.company.android.R;
import com.bber.company.android.app.AppManager;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.SharedPreferencesUtils;
import com.bber.company.android.widget.MyToast;
import com.bber.customview.utils.LogUtils;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by bn on 2017/6/19.
 */

public class NettyClientBootstrap {
    private static NettyClientBootstrap instance;
    public SocketChannel socketChannel;
    //handler实现类
    public NettyClientHandler mNettyClientHandler;
    private int port = 8812;
    private String host = "10.14.2.6";
    // 是否停止
    private boolean isStop = false;
    private String _host;
    private String _port;
    private int current = 0;

    //初始化
    public static NettyClientBootstrap getInstance() {
        if (instance == null) {
            instance = new NettyClientBootstrap();
        }
        return instance;
    }

    public void startNetty() throws InterruptedException {
        if (start()) {
            LogUtils.e("长链接成功");
            Constants.ISLink = false;
            Constants.ISCONTACT = false;
            //将已购买
//            Intent intent = new Intent(Constants.ACTION_GAME_TYPE1);
//            AppManager.getAppManager().currentActivity().sendBroadcast(intent);
        }
    }

    private Boolean start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.group(eventLoopGroup);
        String data = (String) SharedPreferencesUtils.get(AppManager.getAppManager().currentActivity(), "bbergame", host);
        String[] _data = data.split(":");
        _host = _data[0];
        _port = _data[1];

        bootstrap.remoteAddress(_host, Integer.parseInt(_port));
        mNettyClientHandler = new NettyClientHandler();
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {

                ChannelPipeline pipeline = socketChannel.pipeline();
                // 以("\n")为结尾分割的 解码器
                pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
//                pipeline.addLast(new ReadTimeoutHandler(10));
                // 字符串解码 和 编码
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("encoder", new StringEncoder());

                // 加入自定义的Handler
                pipeline.addLast("handler", mNettyClientHandler);
            }
        });
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(new InetSocketAddress(_host, Integer.parseInt(_port))).sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel) future.channel();
                LogUtils.e("connect server  成功---------");
                return true;
            } else {
                LogUtils.e("connect server  失败---------");
                startNetty();
                return false;
            }
        } catch (Exception e) {
            LogUtils.e("无法连接----------------");
            //这里最好暂停一下。不然会基本属于毫秒时间内执行很多次。
            //造成重连失败
//            TimeUnit.SECONDS.sleep(5);

            if (current < 1) {
                current++;
                startNetty();
            } else {
                Constants.ISLink = false;
                Constants.ISCONTACT = false;
                MyToast.makeTextAnim(AppManager.getAppManager().currentActivity(), "游戏暂时无法连接...", 0, R.style.PopToast).show();
            }

            return false;
        }
    }

    //发送数据
    public void sendMsg(String msg) {
        if (mNettyClientHandler != null) {
            mNettyClientHandler.sendMsg(msg);
        }
    }

    /**判断是否链接*/
    public boolean isLink() {
        if (mNettyClientHandler == null) {
            return false;
        }
        return mNettyClientHandler.isLink();
    }


    //停止服务
    public void onStop() {
        isStop = true;
        if (socketChannel != null && socketChannel.isOpen()) {
            socketChannel.close();
        }
    }

    //设置返回数据监听
    public void setInfoListener(NettyListener infoListener) {
        if (mNettyClientHandler != null) {
            mNettyClientHandler.setNettyListener(infoListener);
        }
    }
}
