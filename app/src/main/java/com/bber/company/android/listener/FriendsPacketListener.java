package com.bber.company.android.listener;

import android.util.Log;

import com.bber.company.android.service.MsfService;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;

public class FriendsPacketListener implements StanzaListener {

    MsfService context;

    public FriendsPacketListener(MsfService context) {
        this.context = context;
    }


    //public void processStanza(Stanza packet) throws NotConnectedException, InterruptedException;
    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if (packet.getFrom().equals(packet.getTo())) {
            return;
        }
        if (packet instanceof Presence) {
            Presence presence = (Presence) packet;
            //String from = presence.getFrom();//发送方
            //String to = presence.getTo();//接收方
            if (presence.getType().equals(Presence.Type.subscribe)) {//好友申请
                Log.e("jj", "好友申请");
            } else if (presence.getType().equals(Presence.Type.subscribed)) {//同意添加好友
                Log.e("jj", "同意添加好友");
            } else if (presence.getType().equals(Presence.Type.unsubscribe)) {//拒绝添加好友  和  删除好友
                Log.e("jj", "拒绝添加好友");
            } else if (presence.getType().equals(Presence.Type.unsubscribed)) {

            } else if (presence.getType().equals(Presence.Type.unavailable)) {//好友下线   要更新好友列表，可以在这收到包后，发广播到指定页面   更新列表
                Log.e("jj", "好友下线");
            } else if (presence.getType().equals(Presence.Type.available)) {//好友上线
                Log.e("jj", "好友上线");
            } else {
                Log.e("jj", "error");
            }
        }
    }

	/*@Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

	}*/
}

