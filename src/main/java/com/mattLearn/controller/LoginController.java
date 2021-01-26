package com.mattLearn.controller;

import com.mattLearn.async.EventModel;
import com.mattLearn.async.EventProducer;
import com.mattLearn.async.EventType;
import com.mattLearn.model.HostHolder;
import com.mattLearn.model.Question;
import com.mattLearn.model.ViewObject;
import com.mattLearn.service.QuestionService;
import com.mattLearn.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * modified by Matt @21/1/07
 *
 * Jan 15th 网站注册功能
 *
 * Jan 16th: 用户登录功能
 */

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    // 用户注册时 需要使用 post 请求
    @RequestMapping(path = {"/reg"}, method = {RequestMethod.POST})
    public String registration(Model model,
                            @RequestParam("username") String username,      // 注册的时候获取username 和 password 两个字段
                            @RequestParam("password") String password,
                            @RequestParam("next") String nextPage,
                               HttpServletResponse response) {

        try {
            Map<String, String> map = userService.register(username, password);
            // 出问题的处理思路1：
//        if(map != null){
//            model.addAttribute("msg", map.get("msg"));
//        }
            // 出问题的处理思路2
            if (map.containsKey("msg")) {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
            Cookie cookie = new Cookie("tic", map.get("ticket"));
            cookie.setPath("/");
            response.addCookie(cookie);
            if(StringUtils.isNotBlank(nextPage)){
                return "redirect:" + nextPage;
            }
            return "redirect:/";

        } catch (Exception e) {
            logger.error("Registration error!" + e.getMessage());
            return "login";
        }
    }

    // 登录页面
    @RequestMapping(path = "/reglogin", method = {RequestMethod.GET})
    public String reg(Model model,
                      @RequestParam(value = "next", required = false) String nextPage) {
        model.addAttribute("next", nextPage);
        return "login";
    }

    // 登录功能
    @RequestMapping(path = "/login", method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("next") String nextPage,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response){
        try{
            Map<String,String> map = userService.login(username,password);
            if(map.containsKey("ticket")){
                // 将 ticket 放置到 cookies 中
                Cookie cookie = new Cookie("tic", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);

                eventProducer.fireEvent(new EventModel(EventType.LOGIN).setExt("username",username)
                        .setExt("email","receiver@gmail.com")
                        .setActorId(userService.getUser(username).getId()));

                if(StringUtils.isNotBlank(nextPage)){
                    return "redirect:" + nextPage;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        }catch(Exception e){
            logger.error("Login error!" + e.getMessage());
            return "login";
        }

    }

    // 登出功能，删除 ticket 即可
    // 直接从 cookie 中读取 ticket
    @RequestMapping(path = "/logout", method = {RequestMethod.GET})
    public String logout(@CookieValue("tic") String ticket){
        userService.logout(ticket);
        return "redirect:/";
        // Note: 如果这里写 “redirect:login” 会报错。研究一下为何。
    }
}