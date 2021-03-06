package com.example.administrator.gtd;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/1/28 0028.
 */

public class Content extends DataSupport{
    private String msg;  //内容
    //private String name;  //时间标题
    //private String time;  //事件创立时间
    private int isshow; // 是否显示CheckBox
    private int ischecked; // 是否选中CheckBox
    private String alarmtime;  //事件的提醒时间
    //private int num;
    private int id;
    private int contentid;
    private String nextcontent="nothing";
    private int isdone;  //判断用户是否已经执行了这件事
    private int level=1;   //事件的重要等级 ，3为重要，2为较重要，1为一般；初始化为1
    private int night_mode=0;  //当前的主题模式，0为日间模式，1为夜间模式
    private String buildtime;  //事件创立时间
    private int userid;

    public Content(String msg, boolean isShow, boolean isChecked) {
        this.msg = msg;
        setShow(isShow);
        setChecked(isChecked);
        buildtime="";
//        if(msg.length()>10){
//            name=msg.substring(0,9);
//        }else{
//            name=msg;
//        }
        isdone=0;
        nextcontent="nothing";
        level=1;
    }

    public int getContentid() {
        return contentid;
    }

    public void setContentid(int contentid) {
        this.contentid = contentid;
    }

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

    /*public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }*/

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

    /*public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }*/

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
        this.level = level;
    }

    public int getNight_mode() {
        return night_mode;
    }

    public void setNight_mode(int night_mode) {
        this.night_mode = night_mode;
    }
}
