package com.mattLearn.controller;

import com.mattLearn.model.Question;
import com.mattLearn.model.ViewObject;
import com.mattLearn.service.QuestionService;
import com.mattLearn.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * modified by Matt @21/1/07
 *
 * Jan 15th 网站注册功能
 */

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    // 用户注册时 需要使用 post 请求
    @RequestMapping(path = {"/reg"}, method = {RequestMethod.POST})
    public String registration(Model model,
                            @RequestParam("username") String username,      // 注册的时候获取username 和 password 两个字段
                            @RequestParam("password") String password) {

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
            return "redirect:/";

        } catch (Exception e) {
            logger.error("Registration error!" + e.getMessage());
            return "login";
        }
    }

    // 登录页面
    @RequestMapping(path = "/reglogin", method = {RequestMethod.GET})
    public String reg() {
        return "login";
    }

    // 登录功能
    @RequestMapping(path = "/login", method = {RequestMethod.POST})
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password){

        return "login";
    }
}