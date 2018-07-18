package com.bber.company.android.bean;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/10 0010.
 */
public class RandomGrilBean implements Serializable {
    public int id;
    public int isPush;
    public String marqueeCity;
    public String marqueeContent;
    public int marqueeType;
    public ArrayList<SellersBean> sellers;

    public class SellersBean {
        public int uSeller;
        public String usHeadm;
        public String usName;
    }
}
