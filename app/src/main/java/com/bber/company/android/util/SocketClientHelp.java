package com.bber.company.android.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bber.company.android.tools.Tools;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientAddress;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketClientReceivingDelegate;
import com.vilyever.socketclient.helper.SocketClientSendingDelegate;
import com.vilyever.socketclient.helper.SocketPacket;
import com.vilyever.socketclient.helper.SocketResponsePacket;

/**
 * 直播 socket 工具类
 * Created by carlo.c on 2018/4/24.
 */

public class SocketClientHelp {

    private static SocketClientHelp initService;
    private SocketClient socketClient;
    private SocketClientAddress socketClientAddress;
    private Context context;
    private String MD5key = "842abfcf808c32877ac9186f240e3b41";
    private SocketClientDelegateData socketClientDelegateData;

    public static SocketClientHelp initService(Context context) {
        if (initService == null) {
            initService = new SocketClientHelp(context);
        } else {
             initService=initService;
        }
        return initService;
    }

    /**
     * 建立socket 链接
     */
    public SocketClientHelp(Context context) {
        this.context = context;
        socketClient = new SocketClient();
        socketClientAddress = new SocketClientAddress();
     //   socketClient.setCharsetName(CharsetUtil.UTF_8);// 设置编码格式
        socketClientAddress.setRemoteIP("10.60.170.83"); //ip  10.60.2.6
        socketClientAddress.setRemotePort("30000"); // 端口 8822
        socketClientAddress.setConnectionTimeout(1000 * 60*2); // 设置链接超时时间
        socketClient.setAddress(socketClientAddress);
        // socketClient.getHeartBeatHelper().setDefaultSendData()
        //常用状态回调
        socketClient.connect();
        socketClient.registerSocketClientDelegate(new SocketClientDelegate() {
            // 连接上远程端时的回调
            @Override
            public void onConnected(SocketClient client) {
                ChenQianLog.i("socket 链接成功 onConnected:" + client.getCharsetName());
                //  socketClient.cancelSend(packet);// 取消发送，若在等待发送队列中则从队列中移除，若正在发送则无法取
            }

            //与远程端断开连接时的回调
            @Override
            public void onDisconnected(SocketClient client) {
                // 可在此实现自动重连
                // socketClient.connect();
                ChenQianLog.i("socket onDisconnected:" + client.getCharsetName());
            }

            //接收到数据包时的回调
            @Override
            public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {

                byte[] data = responsePacket.getData(); // 获取接收的byte数组，不为null
                String message = responsePacket.getMessage(); // 获取按默认设置的编码转化的String，可能为null

                ChenQianLog.i("socket 收到数据onResponse:" + message);

                if (socketClientDelegateData != null) {
                    socketClientDelegateData.sendSuccess(message);
                }

            }
        });

        //发送状态回调配置
        socketClient.registerSocketClientSendingDelegate(new SocketClientSendingDelegate() {
            /**
             * 数据包开始发送时的回调
             */
            @Override
            public void onSendPacketBegin(SocketClient client, SocketPacket packet) {
                ChenQianLog.i("socket 数据包开始发送:" + client.getCharsetName());

            }

            /**
             * 数据包完成发送时的回调
             */
            @Override
            public void onSendPacketEnd(SocketClient client, SocketPacket packet) {
                ChenQianLog.i("socket 数据包完成发送:" + client.getCharsetName());

            }

            /**
             * 数据包取消发送时的回调
             * 取消发送回调有以下情况：
             * 1. 手动cancel仍在排队，还未发送过的packet
             * 2. 断开连接时，正在发送的packet和所有在排队的packet都会被取消
             */
            @Override
            public void onSendPacketCancel(SocketClient client, SocketPacket packet) {
                ChenQianLog.i("socket 数据包取消发送:" + client.getCharsetName());

            }

            /**
             * 数据包发送的进度回调
             * progress值为[0.0f, 1.0f]
             * 通常配合分段发送使用
             * 可用于显示文件等大数据的发送进度
             */
            @Override
            public void onSendingPacketInProgress(SocketClient client, SocketPacket packet, float progress, int sendedLength) {
                ChenQianLog.i("socket 数据包发送的进度:" +sendedLength);

            }

        });

        //接受状态回调
        // 对应removeSocketClientReceiveDelegate
        socketClient.registerSocketClientReceiveDelegate(new SocketClientReceivingDelegate() {
            /**
             * 开始接受一个新的数据包时的回调
             */
            @Override
            public void onReceivePacketBegin(SocketClient client, SocketResponsePacket packet) {
                ChenQianLog.i("socket 开始接受一个新的数据包:" + client.getCharsetName());

            }

            /**
             * 完成接受一个新的数据包时的回调
             */
            @Override
            public void onReceivePacketEnd(SocketClient client, SocketResponsePacket packet) {
                ChenQianLog.i("socket 完成接受一个新的数据包:" + client.getCharsetName());

            }

            /**
             * 取消接受一个新的数据包时的回调
             * 在断开连接时会触发
             */
            @Override
            public void onReceivePacketCancel(SocketClient client, SocketResponsePacket packet) {
                ChenQianLog.i("socket 取消接受一个新的数据包:" + client.getCharsetName());

            }

            /**
             * 接受一个新的数据包的进度回调
             * progress值为[0.0f, 1.0f]
             * 仅作用于ReadStrategy为AutoReadByLength的自动读取
             * 因AutoReadByLength可以首先接受到剩下的数据包长度
             */
            @Override
            public void onReceivingPacketInProgress(SocketClient client, SocketResponsePacket packet, float progress, int receivedLength) {

                ChenQianLog.i("socket 接受一个新的数据包的进度:" + receivedLength);

            }
        });

    }

    /**
     * 获取socket 对象
     */
    public SocketClient getSocket() {

        if (socketClient != null) {
            return socketClient;
        } else {
            return null;
        }
    }

    public void connectSocket() {
        if (socketClient != null) {
            socketClient.connect();
        }
    }

    /***
     * 拼接sign的加密字符
     */
    private String spellSign(String token, String type) {
        return Tools.md5("token=" + token + "#type=" + type + MD5key);
    }

    /**
     * 发送消息
     */
    public void sendMessage(String message, SocketClientDelegateData socketClientDelegateData) {

        this.socketClientDelegateData = socketClientDelegateData;
        if (socketClient != null) {
           // socketClient.sendString(message);

            socketClient.sendData(message.getBytes());
        }
    }

    /**
     * 断开链接
     */
    public void closeSocket() {
        if (socketClient != null) {
            socketClient.disconnect();
        }
    }

    public interface SocketClientDelegateData {

        void sendSuccess(String message);

    }
}
