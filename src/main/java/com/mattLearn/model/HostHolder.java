package com.mattLearn.model;

import org.springframework.stereotype.Component;

/**
 * @author Matt
 * @date 2021/1/16 17:52
 */

@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    // 每个线程之间的 user 相互独立！
    public User getUser(){
        return users.get();
    }

    public void setUser(User user){
        users.set(user);
    }

    public void clear(){
        users.remove();
    }
}
