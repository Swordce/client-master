package com.lepao.ydcgkf.content;

/**
 * created by zwj on 2018/9/8 0008
 */
public class EventMsg {
    private int type;
    private String value;

    public EventMsg(int type) {
        this.type = type;
    }

    public EventMsg(String value) {
        this.value = value;
    }

    public EventMsg(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
