package com.bber.company.android.util.country;

import com.bber.company.android.R;
import com.bber.company.android.bean.livebroadcast.BoradcastMessageBean;
import com.bber.company.android.bean.livebroadcast.MessageListBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by carlo.c on 2018/5/4.
 */

public class Constant {

    public static int MAX_INPUT_NAME_LENGTH = 128;

    public static Random RANDOM = new Random();

    public static final int[] COLOR_ARRAY = new int[]{R.drawable.shape_circle_black, R.drawable.shape_circle_blue, R.drawable.shape_circle_pink,
            R.drawable.shape_circle_pink_dark, R.drawable.shape_circle_yellow, R.drawable.shape_circle_red};

    private static List<MessageListBean> messageListBeanList = new ArrayList<>();


    public static void addMessageListBeanList(MessageListBean messageListBean) {
        messageListBeanList.add(messageListBean);

    }

    //logout clean list
    public static void cleanMessageListBeanList() {
        messageListBeanList.clear();
    }

    public static MessageListBean getExistMesageListBean(String accountOther) {
        int ret = existMessageListBean(accountOther);
        if (ret > -1) {

            return messageListBeanList.remove(ret);
        }
        return null;
    }

    //return exist list position
    private static int existMessageListBean(String accountOther) {
        int size = messageListBeanList.size();

        for (int i = 0; i < size; i++) {
            if (messageListBeanList.get(i).getAccountOther().equals(accountOther)) {

                return i;
            }
        }
        return -1;
    }

    public static void addMessageBean(String account, String msg) {
        BoradcastMessageBean messageBean = new BoradcastMessageBean(account, msg, "false");

        int ret = existMessageListBean(account);

        if (ret == -1) {

            //account not exist new messagelistbean
            messageBean.setBackground(Constant.COLOR_ARRAY[RANDOM.nextInt(Constant.COLOR_ARRAY.length)]);
            List<BoradcastMessageBean> messageBeanList = new ArrayList<>();
            messageBeanList.add(messageBean);
            messageListBeanList.add(new MessageListBean(account, messageBeanList));
        } else {

            //account exist get messagelistbean
            MessageListBean bean = messageListBeanList.remove(ret);
            List<BoradcastMessageBean> messageBeanList = bean.getMessageBeanList();
            if (messageBeanList.size() > 0) {
                messageBean.setBackground(messageBeanList.get(0).getBackground());
            } else {
                messageBean.setBackground(Constant.COLOR_ARRAY[RANDOM.nextInt(Constant.COLOR_ARRAY.length)]);
            }
            messageBeanList.add(messageBean);
            bean.setMessageBeanList(messageBeanList);
            messageListBeanList.add(bean);
        }
    }
}
