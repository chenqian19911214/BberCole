package com.bber.company.android.util;

import android.content.Context;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 直播 socket 工具类
 * Created by carlo.c on 2018/4/24.
 */

public class SocketHelp {

    private static SocketHelp initService;

    private Context context;
    private String MD5key = "842abfcf808c32877ac9186f240e3b41";
    private Socket socket;
    private boolean isSocketConnect = false;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String servicedata;
    private SocketClientDelegateData disconnect;

    private ExecutorService executorService;

    private Thread initSocketThead, sendSocketThad, acceptSocketThad;
    private String host;
    private int port;

    android.os.Handler handler = new android.os.Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            servicedata = (String) msg.obj;
           //  ChenQianLog.i("socket handler：" + servicedata);
            if (disconnect != null) {
                disconnect.sendSuccess(servicedata);
            }
        }
    };

    public static SocketHelp initService(Context context) {
        if (initService == null) {
            initService = new SocketHelp(context);
        } else {
            initService = initService;
        }
        return initService;
    }

    private SocketHelp(Context context) {
        this.context = context;
        executorService = Executors.newFixedThreadPool(3);
    }


    /**
     * 接受socket 消息
     */
    private void acceptSocketMessage() {
        acceptSocketThad = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inputStream = socket.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader socketBr = new BufferedReader(inputStreamReader);
                    String line = null;
                    // 采用循环，不断读取将输入流中每行
                    while ((line = socketBr.readLine()) != null) {
                        //showArea.append(line + "\n");
                        Message message = handler.obtainMessage();
                        message.obj = line;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    isSocketConnect = false;
                }

            }
        });
        acceptSocketThad.start();
        executorService.execute(acceptSocketThad);
    }

    /**
     * 建立socket 链接
     */
    public void connenctSocket(final String host, final int port) {

        this.host = host;
        this.port = port;
        initSocketThead = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(host, port);
                    isSocketConnect = socket.isConnected();
                    if (isSocketConnect) {
                        if (socket != null) {
                            acceptSocketMessage();
                        }
                        ChenQianLog.i("Socket 已经连接到服务器");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ChenQianLog.i("socket连接异常："+e.getMessage());
                    isSocketConnect = false;
                }
            }
        });

        initSocketThead.start();
        executorService.execute(initSocketThead);
    }

    /**
     * 判断socket 是否已经连接上服务器
     */

    public boolean getSocketConnectState() {

        return isSocketConnect;
    }

    /**
     * 发送消息
     */
    public void sendMessage(final String message, SocketClientDelegateData socketClientDelegateData) {

        this.disconnect = socketClientDelegateData;

        if (!isSocketConnect) {

            connenctSocket(host, port);
        }
        if (isSocketConnect) {
            sendMassess(message);
         //   acceptSocketMessage();
        }
    }

    /**
     * 向服务器端发消息
     */
    private void sendMassess(final String message) {
        sendSocketThad = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    outputStream = socket.getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    printStream.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    isSocketConnect = false;
                }
            }
        });
        sendSocketThad.start();
        executorService.execute(sendSocketThad);
    }

    /**
     * 断开链接
     */
    public void closeSocketService() {

        try {
            if (socket != null) {
                socket.close();
            }
            if (executorService != null) {
                executorService.shutdown();
            }

            isSocketConnect = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface SocketClientDelegateData {

        void sendSuccess(String message);
    }
}


