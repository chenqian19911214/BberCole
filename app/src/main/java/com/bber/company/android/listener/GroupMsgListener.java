package com.bber.company.android.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bber.company.android.bean.MessageBean;
import com.bber.company.android.constants.Constants;
import com.bber.company.android.tools.JsonUtil;
import com.bber.customview.utils.LogUtils;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;


/**
 * @author baiyuliang
 */

@SuppressWarnings("static-access")
public class GroupMsgListener implements MessageListener {
    private Context mContext;

    public GroupMsgListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void processMessage(Message message) {
        LogUtils.e("eeeeeeeeeeeeeeee" + "收到消息--解密前Body" + message.getBody());
        LogUtils.e("eeeeeeeeeeeeeeee" + "收到消息--解密前Body" + message.toXML().toString());

        JsonUtil jsonUtil = new JsonUtil(mContext);
        //解析成需要的message
        MessageBean messageBean = new MessageBean();

        messageBean = jsonUtil.jsonToMessage(message.getBody());
        Intent mIntent = new Intent(Constants.ACTION_CHAT_IMG);
        Bundle bundle = new Bundle();
        bundle.putSerializable("MESSAGEGROUP", messageBean);
        mIntent.putExtras(bundle);
        mContext.sendBroadcast(mIntent);
    }
}