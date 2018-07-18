package com.bber.company.android.bean;

import android.databinding.BaseObservable;

import com.bber.company.android.util.TimeUtil;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class VideoBean extends BaseObservable implements Serializable {
    public String start_time;
    public List<VideoitemBean> vipZoneImglist;
    private int click;
    private int id;
    private String cover_path;
    private String detail;
    private String end_time;
    private String flag;
    private String keywords;
    private String path_end;
    private String path_start;
    private String priority;
    private String title;
    private int type;
    private String update_time;

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCover_path() {
        return cover_path;
    }

    public void setCover_path(String cover_path) {
        this.cover_path = cover_path;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getPath_end() {
        return path_end;
    }

    public void setPath_end(String path_end) {
        this.path_end = path_end;
    }

    public String getPath_start() {
        return path_start;
    }

    public void setPath_start(String path_start) {
        this.path_start = path_start;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public List<VideoitemBean> getVipZoneImglist() {
        return vipZoneImglist;
    }

    public void setVipZoneImglist(List<VideoitemBean> vipZoneImglist) {
        this.vipZoneImglist = vipZoneImglist;
    }

    public String getPic() {
        if (vipZoneImglist.size() != 0) {
            return vipZoneImglist.get(0).source;
        }
        return null;
    }

    public String getDataStr() {
        int betweendays = 0;
        String str = "";
        TimeUtil timeUtil = new TimeUtil();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date newData = sdf.parse(start_time);

            betweendays = timeUtil.daysBetween(newData, new Date(System.currentTimeMillis()));

            if (Math.abs(betweendays) < 1) {
                str = "今天" + TimeUtil.timeStampString(start_time, 1);
                return str;
            } else if (Math.abs(betweendays) < 2) {
                str = "昨天" + TimeUtil.timeStampString(start_time, 1);
                return str;
            } else if (Math.abs(betweendays) <= 7) {
                str = Math.abs(betweendays) + "天前" + TimeUtil.timeStampString(start_time, 1);
                return str;
            } else {
                return TimeUtil.timeStampString(start_time, 2);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return str;
    }
}



