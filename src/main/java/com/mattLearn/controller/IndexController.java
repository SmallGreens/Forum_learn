package com.mattLearn.controller;

import com.mattLearn.model.Question;
import com.mattLearn.model.User;
import com.mattLearn.model.ViewObject;
import com.mattLearn.service.QuestionService;
import com.mattLearn.service.TestService;
import com.mattLearn.service.UserService;
import org.aspectj.weaver.ast.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * modified by Matt @21/1/07
 *
 * Jan 14th, 写网站首页。
 */

@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
    public String index(Model model){
        // 获取问题列表
        List<Question> questionList = questionService.getLatestQuestions(0,0,10);
        List<ViewObject> vos = new ArrayList<>();
        // 将每个问题的相关信息放置到一个 viewObject 中
        for(Question question: questionList){
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        // 通过 model 传递 viewObject 到前端
        model.addAttribute("vos", vos);
        return "index";
    }
}
