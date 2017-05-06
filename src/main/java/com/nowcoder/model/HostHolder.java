package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * Created by dell on 2017/5/6.
 * 作用：用于在各个模块之间共享当前登录的用户的相关变量及数据
 */
@Component
public class HostHolder {
    //线程池对象，存储当前请求对象的线程
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();   //返回当前线程的对象
    }

    public void setUser(User user){
        users.set(user);     //将当前请求的用户线程对象放入到线程池当中
    }
    public void clear(){
        users.remove();   //从线程池中移除当前用户
    }
}
