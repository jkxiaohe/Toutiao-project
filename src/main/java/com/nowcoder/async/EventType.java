package com.nowcoder.async;

/**
 * Created by dell on 2017/5/14.
 * 发布对象的类型
 */
public enum EventType {
    LIKE(0),
    COMMENT(0),
    LOGIN(2),
    MAIL(3);

    private int value;
    EventType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
