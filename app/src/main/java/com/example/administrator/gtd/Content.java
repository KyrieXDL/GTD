package com.example.administrator.gtd;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/1/28 0028.
 */

public class Content extends DataSupport{
    private String msg;  //内容
    private String name;  //时间标题
    private String time;  //事件创立时间
    private boolean isShow; // 是否显示CheckBox
    private boolean isChecked; // 是否选中CheckBox
    private String alarmTime;  //事件的提醒时间
    private int num;

    private boolean isDone;  //判断用户是否已经执行了这件事

    public Content(String msg, boolean isShow, boolean isChecked) {
        this.msg = msg;
        this.isShow = isShow;
        this.isChecked = isChecked;
        time="";
        if(msg.length()>10){
            name=msg.substring(0,9);
        }else{
            name=msg;
        }
        isDone=false;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public boolean isShow() {
        return isShow;
    }
    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
