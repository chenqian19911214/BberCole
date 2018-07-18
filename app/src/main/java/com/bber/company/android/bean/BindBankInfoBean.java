package com.bber.company.android.bean;

/**
 * 银行卡号码
 * Created by Vencent on 2016/02/22 0024.
 * C端获取银行卡返回的bean类型
 */
public class BindBankInfoBean {
    private String resultCode;    //结果描述代码
    private String resultMessage;  //结果描述
    private String totalPage;      //页面总数
    private String currentPage;      //当前页面数
    private Object dataCollection;

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public Object getDataCollection() {

        return dataCollection;
    }

    public void setDataCollection(Object dataCollection) {
        this.dataCollection = dataCollection;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "BindBankInfoBean{" +
                "resultCode='" + resultCode + '\'' +
                ", resultMessage='" + resultMessage + '\'' +
                ", totalPage='" + totalPage + '\'' +
                ", currentPage='" + currentPage + '\'' +
                ", dataCollection=" + dataCollection +
                '}';
    }
}
