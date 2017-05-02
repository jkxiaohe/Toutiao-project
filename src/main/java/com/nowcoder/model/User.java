package com.nowcoder.model;

/**
 * Created by dell on 2017/5/2.
 */
public class User {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public User(String name) {
        this.name = name;
    }
}
