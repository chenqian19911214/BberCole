package com.bber.company.android.tools;

import android.content.Context;

import com.bber.company.android.bean.Balance;
import com.bber.company.android.bean.BusinessBean;
import com.bber.company.android.bean.BusinessRatingBean;
import com.bber.company.android.bean.BuyerUserEntity;
import com.bber.company.android.bean.ChatInfo;
import com.bber.company.android.bean.ChzfBean;
import com.bber.company.android.bean.DiscussBean;
import com.bber.company.android.bean.DpPayBean;
import com.bber.company.android.bean.GameBean;
import com.bber.company.android.bean.GameBeenSoldBean;
import com.bber.company.android.bean.GameGirlsBean;
import com.bber.company.android.bean.GameHistroyBean;
import com.bber.company.android.bean.GameHistroyUserBean;
import com.bber.company.android.bean.GameLoginBean;
import com.bber.company.android.bean.GameMessageEnterBean;
import com.bber.company.android.bean.GameSendBean;
import com.bber.company.android.bean.GameWinBean;
import com.bber.company.android.bean.GlodBean;
import com.bber.company.android.bean.HttpHead;
import com.bber.company.android.bean.InviteFriend;
import com.bber.company.android.bean.Level;
import com.bber.company.android.bean.ManuaUrlBean;
import com.bber.company.android.bean.MarBean;
import com.bber.company.android.bean.MessageBean;
import com.bber.company.android.bean.MoneySuer;
import com.bber.company.android.bean.Order;
import com.bber.company.android.bean.RandomGrilBean;
import com.bber.company.android.bean.SellerUserVo;
import com.bber.company.android.bean.Tag;
import com.bber.company.android.bean.VideoBean;
import com.bber.company.android.bean.VipInforBean;
import com.bber.company.android.bean.VipInfor_Bean;
import com.bber.company.android.bean.VipServiceBean;
import com.bber.company.android.bean.VoucherBean;
import com.bber.company.android.bean.adsBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by Administrator on 2015-06-23.
 */
public class JsonUtil {
    public Gson gson;

    public JsonUtil(Context context) {
        if (gson == null) {
//            gson = new Gson();
            gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().setLenient().create();
        }
    }

    /**
     * json字符串转为实体对象(BuyerUserEntity)
     * *
     */
    public BuyerUserEntity jsonToBuyerUserEntity(String jsonStr) {
        return gson.fromJson(jsonStr, BuyerUserEntity.class);
    }


    public String httpHeadToJson(Context context) {
        return gson.toJson(new HttpHead(context));
    }

    public String chatInfoToString(ChatInfo chatInfo) {
        return gson.toJson(chatInfo);
    }

    public SellerUserVo jsonToSellerUserVo(String jsonStr) {
        return gson.fromJson(jsonStr, SellerUserVo.class);
    }

    public MessageBean jsonToMessage(String jsonStr) {
        return gson.fromJson(jsonStr, MessageBean.class);
    }

    public List<VoucherBean> jsonToVoucherBean(String jsonStr) {
        List<VoucherBean> voucherBeans = gson.fromJson(jsonStr, new TypeToken<List<VoucherBean>>() {
        }.getType());
        return voucherBeans;
    }

    /**
     * 解析头部跑马灯的数据
     *
     * @param jsonStr
     * @return
     */
    public List<RandomGrilBean> jsonToHomeGrilBean(String jsonStr) {
        List<RandomGrilBean> randomGrilBeanList = gson.fromJson(jsonStr, new TypeToken<List<RandomGrilBean>>() {
        }.getType());
        return randomGrilBeanList;
    }


    public List<VipServiceBean> jsonToVipServiceBean(String jsonStr) {
        List<VipServiceBean> vipServiceBean = gson.fromJson(jsonStr, new TypeToken<List<VipServiceBean>>() {
        }.getType());
        return vipServiceBean;
    }


    public List<VipInforBean> jsonToVipinfoBean(String jsonStr) {
        List<VipInforBean> vipServiceBean = gson.fromJson(jsonStr, new TypeToken<List<VipInforBean>>() {
        }.getType());
        return vipServiceBean;
    }

    public List<VipInfor_Bean> jsonToVipinforBean(String jsonStr) {
        List<VipInfor_Bean> vipServiceBean = gson.fromJson(jsonStr, new TypeToken<List<VipInfor_Bean>>() {
        }.getType());
        return vipServiceBean;
    }


    public List<Order> jsonToOrderBean(String jsonStr) {
        List<Order> orders = gson.fromJson(jsonStr, new TypeToken<List<Order>>() {
        }.getType());
        return orders;
    }

    public List<DiscussBean> jsonToDiscussBean(String jsonStr) {
        List<DiscussBean> discussBean = gson.fromJson(jsonStr, new TypeToken<List<DiscussBean>>() {
        }.getType());
        return discussBean;
    }

    public List<VideoBean> jsonToVideoBean(String jsonStr) {
        List<VideoBean> discussBean = gson.fromJson(jsonStr, new TypeToken<List<VideoBean>>() {
        }.getType());
        return discussBean;
    }

    public List<String> jsonToDStringBean(String str) {
        List<String> discussBean = gson.fromJson(str, new TypeToken<List<String>>() {
        }.getType());
        return discussBean;
    }

    public List<DpPayBean> jsonToBalanceBean(String jsonStr) {
        List<DpPayBean> balances = gson.fromJson(jsonStr, new TypeToken<List<DpPayBean>>() {
        }.getType());
        return balances;
    }

    public List<InviteFriend> jsonToinviteFriend(String jsonStr) {
        List<InviteFriend> inviteFriends = gson.fromJson(jsonStr, new TypeToken<List<InviteFriend>>() {
        }.getType());
        return inviteFriends;
    }

    public List<BusinessBean> jsonToBusinessBean(String jsonStr) {
        List<BusinessBean> businessBeen = gson.fromJson(jsonStr, new TypeToken<List<BusinessBean>>() {
        }.getType());
        return businessBeen;
    }

    public List<BusinessRatingBean> jsonToBusinessRatingBean(String jsonStr) {
        List<BusinessRatingBean> businessBeen = gson.fromJson(jsonStr, new TypeToken<List<BusinessRatingBean>>() {
        }.getType());
        return businessBeen;
    }

    public List<adsBean> jsonToadsBean(String jsonStr) {
        List<adsBean> adsBean = gson.fromJson(jsonStr, new TypeToken<List<adsBean>>() {
        }.getType());
        return adsBean;
    }

    public ChatInfo jsonToChatInfo(String jsonStr) {
        return gson.fromJson(jsonStr, ChatInfo.class);
    }

    public ChzfBean jsonTochzf(String jsonStr) {
        return gson.fromJson(jsonStr, ChzfBean.class);
    }

    public ManuaUrlBean jsonToManuaUrlBean(String jsonStr) {
        return gson.fromJson(jsonStr, ManuaUrlBean.class);
    }

    public GlodBean jsonToGlodBeanBean(String jsonStr) {
        return gson.fromJson(jsonStr, GlodBean.class);
    }

    public String MessageBeanToString(MessageBean chatInfo) {
        return gson.toJson(chatInfo);
    }

    public GameLoginBean GameLoginBeanToString(String jsonStr) {
        return gson.fromJson(jsonStr, GameLoginBean.class);
    }

    public GameWinBean GameWinToString(String jsonStr) {
        return gson.fromJson(jsonStr, GameWinBean.class);
    }

    public List<GameMessageEnterBean> GameMessageEnterBeanToString(String jsonStr) {
        List<GameMessageEnterBean> list = gson.fromJson(jsonStr, new TypeToken<List<GameMessageEnterBean>>() {
        }.getType());
        return list;
    }

    public List<GameBeenSoldBean> GameSoldToString(String jsonStr) {
        List<GameBeenSoldBean> list = gson.fromJson(jsonStr, new TypeToken<List<GameBeenSoldBean>>() {
        }.getType());
        return list;
    }

    public List<GameHistroyUserBean> GameHistroyToString(String jsonStr) {
        List<GameHistroyUserBean> list = gson.fromJson(jsonStr, new TypeToken<List<GameHistroyUserBean>>() {
        }.getType());
        return list;
    }

    public List<GameGirlsBean> GameGilrsToString(String jsonStr) {
        List<GameGirlsBean> list = gson.fromJson(jsonStr, new TypeToken<List<GameGirlsBean>>() {
        }.getType());
        return list;
    }

    public String GameBeanToString(GameBean chatInfo) {
        return gson.toJson(chatInfo);
    }

    public String GameSendString(GameSendBean chatInfo) {
        return gson.toJson(chatInfo);
    }

    public String GameMarString(MarBean chatInfo) {
        return gson.toJson(chatInfo);
    }

    public String GameHistroyString(GameHistroyBean chatInfo) {
        return gson.toJson(chatInfo);
    }

    public Level jsonToLevel(String jsonStr) {
        return gson.fromJson(jsonStr, Level.class);
    }

    public DiscussBean jsonTodiscussBean(String jsonStr) {
        return gson.fromJson(jsonStr, DiscussBean.class);
    }

    public String orderToString(Order order) {
        return gson.toJson(order);
    }

    public Order jsonToOrder(String jsonStr) {
        return gson.fromJson(jsonStr, Order.class);
    }

    public List<Level> jsonToLevels(String jsonStr) {
        List<Level> levels = gson.fromJson(jsonStr, new TypeToken<List<Level>>() {
        }.getType());
        return levels;
    }

    public List<Balance> jsonToBalances(String jsonStr) {
        List<Balance> balances = gson.fromJson(jsonStr, new TypeToken<List<Balance>>() {
        }.getType());
        return balances;
    }

    /**
     * json字符串转为实体对象集合(Tag)
     * *
     */
    public List<Tag> jsonToTags(String jsonStr) {
        List<Tag> tags = gson.fromJson(jsonStr, new TypeToken<List<Tag>>() {
        }.getType());
        return tags;
    }

    public MoneySuer jsonMoneySuer(String jsonser) {

        return gson.fromJson(jsonser, MoneySuer.class);
    }

}
