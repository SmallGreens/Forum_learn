package com.mattLearn.service;

import com.mattLearn.dao.UserDAO;
import com.mattLearn.model.User;
import com.mattLearn.util.ForumUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Modified by Matt on Jan. 14th 2021
 *
 * @Date Jan.15th add register 函数，实现用户注册功能
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    // overload the get method, can also use name to get the user.
    public User getUser(String name){
        return userDAO.selectByName(name);
    }

    public Map<String, String> register(String username, String password){
        Map<String, String> map = new HashMap<>();
        // 有很多 StringUtil 的方法。这里用的是 apache.commons.lang 包里的
        // 在 apache commons 包中有很多帮助的类，可以简单看一下
        // isBlank 方法还能判断例如 "   " 这种为 空的情况。
        if(StringUtils.isBlank(username)){
            map.put("msg","Username can't be blank!!");
            // 中断执行，返回给前端，不要忘记返回！！
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","Password can't be blank!!");
            return map;
        }

        // Note:
        User user = userDAO.selectByName(username);
        if(user != null){
            map.put("msg","User is already exist!!");
            return map;
        }

        user = new User();
        user.setName(username);
        // 使用 java.com.mattLearn.util 的 uuid 的 randomUUID 方法获取一个随机的 salt， 用于对密码进行加密
        // 关于 UUID: A class that represents an immutable universally unique identifier (UUID). A UUID represents a 128-bit value.
        // randomUUID 方法返回一个 UUID 对象
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        // 保存的密码为 ”用户密码 + salt“ then md5 加密的结果。
        user.setPassword(ForumUtil.MD5(password+user.getSalt()));
        // 注意这里 string.format 的使用和 随机数的生成方式
        user.setHeadUrl(String.format("https://images.nowcoder.com/head/%dm.png", new Random().nextInt(1000)));
        userDAO.addUser(user);
        // map.put("msg", "register successfully!");
        return map;
    }
}
