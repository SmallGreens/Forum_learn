package com.mattLearn.service;

import com.mattLearn.dao.LoginTicketDAO;
import com.mattLearn.dao.UserDAO;
import com.mattLearn.model.LoginTicket;
import com.mattLearn.model.User;
import com.mattLearn.util.ForumUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.krb5.internal.Ticket;

import java.util.*;

/**
 * Modified by Matt on Jan. 14th 2021
 *
 * @Date Jan.15th add register 函数，实现用户注册功能
 *
 * Jan-16th 添加登录功能相关代码。
 *
 *
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoginTicketDAO loginTicketDAO;

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

        // 注册完毕，自动登录
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    // 登录函数
    public Map<String, String> login(String username, String password){
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

        // Note: 检查用户是否存在
        User user = userDAO.selectByName(username);
        if(user == null){
            map.put("msg","User is not exist.");
            return map;
        }

        if(!Objects.equals(ForumUtil.MD5(password + user.getSalt()), user.getPassword())){
            map.put("msg", "Wrong password!");
            return map;
        }

        // 使用 ticket 进行登录状态表示
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }


    // 添加 T票 函数
    private String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        // 设置过期时间为 1个星期之后
        Date expireTime = new Date();
        // 注意这里要乘上一个 1000 ms
        expireTime.setTime(expireTime.getTime() + 1000*3600*24*7);
        loginTicket.setExpired(expireTime);
        // 设置为 0 表示 ticket 有效
        loginTicket.setStatus(0);
        // 随机生成一个 UUID 作为 ticket，并将 UUID 中 的 “-” 替换为 “”
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-", ""));

        loginTicketDAO.addTicket(loginTicket);

        return loginTicket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
}
