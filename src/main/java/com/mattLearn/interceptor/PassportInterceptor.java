package com.mattLearn.interceptor;

import com.mattLearn.dao.LoginTicketDAO;
import com.mattLearn.dao.UserDAO;
import com.mattLearn.model.HostHolder;
import com.mattLearn.model.LoginTicket;
import com.mattLearn.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author Matt
 * @date 2021/1/16 16:28
 */

// 添加 component 标签，便于后面使用依赖注入功能
@Component
public class PassportInterceptor implements HandlerInterceptor {

    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    HostHolder hostHolder;

    // 请求调用之前，也就是 controller 函数执行之前，首先调用 preHandle 函数
    // 如果该函数 返回 false ，则后面的请求处理内容不会执行，就相当于 “拦截住了” 后面的请求
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies != null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals("tic")){
                    ticket = cookie.getValue();
                    // 找到 tick 后 记得 break 提前结束
                    break;
                }
            }
        }
        LoginTicket loginTicket;
        if(ticket != null){
            loginTicket = loginTicketDAO.selectByTicket(ticket);
            // 判断 ticket 是否有效
            if(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0){
                return true;
            }
            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }
        return true;
    }

    // controller 处理完毕，网页渲染之前
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        // 将 hostHolder 的内容通过 modelAndView 对象传递到前端。
        // 与 controller 函数中的参数  Model model 很像
        if(modelAndView != null){
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    // 全部执行完毕后，进行数据清理等工作
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        // 对当前线程中的 user 内容进行清理
        hostHolder.clear();
    }
}
