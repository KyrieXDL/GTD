package com.example.administrator.gtd;

/**
 * Created by Administrator on 2018/7/21 0021.
 */

public class Contenttmp {
    private int id;
    private String msg;  //内容
    private String buildtime;  //事件创立时间
    private String alarmtime;  //事件的提醒时间
    private int level;   //事件的重要等级 ，3为重要，2为较重要，1为一般；初始化为1
    private int night_mode;  //当前的主题模式，0为日间模式，1为夜间模式
    private String nextcontent;
    private int isshow; // 是否显示CheckBox
    private int ischecked; // 是否选中CheckBox
    private int isdone;  //判断用户是否已经执行了这件事
    private int userid;


    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public boolean isShow() {
        if (isshow==1){
            return true;
        }else{
            return false;
        }
    }
    public void setShow(boolean isShow) {
        if(isShow){
            this.isshow=1;
        }else{
            this.isshow=0;
        }
    }
    public boolean isChecked() {
        if (ischecked==1){
            return true;
        }else{
            return false;
        }
    }
    public void setChecked(boolean isChecked) {
        if(isChecked){
            this.ischecked=1;
        }else{
            this.ischecked=0;
        }
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setTime(String time) {
        this.buildtime = time;
    }

    public String getTime() {
        return buildtime;
    }

    public String getAlarmTime() {
        return alarmtime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmtime = alarmTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDone() {
        if (isdone==1){
            return true;
        }else{
            return false;
        }
    }

    public void setDone(boolean done) {
        if(done){
            this.isdone=1;
        }else{
            this.isdone=0;
        }
    }

    public String getNextContent() {
        return nextcontent;
    }

    public void setNextContent(String nextContent) {
        this.nextcontent = nextContent;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level =level;
    }

    public int getNight_mode() {
        return night_mode;
    }

    public void setNight_mode(int night_mode) {
        this.night_mode = night_mode;
    }
}
