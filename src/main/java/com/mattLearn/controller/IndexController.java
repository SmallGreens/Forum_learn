package com.mattLearn.controller;

import com.mattLearn.model.*;
import com.mattLearn.service.CommentService;
import com.mattLearn.service.FollowService;
import com.mattLearn.service.QuestionService;
import com.mattLearn.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    CommentService commentService;

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
    public String index(Model model){
        // 获取问题列表
        List<Question> questionList = questionService.getLatestQuestions(0,0,10);
        List<ViewObject> vos = new ArrayList<>();
        // 将每个问题的相关信息放置到一个 viewObject 中
        for(Question question: questionList){
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        // 通过 model 传递 viewObject 到前端
        model.addAttribute("vos", vos);
        return "index";
    }

    @RequestMapping(path = "/user/{userId}", method = {RequestMethod.GET, RequestMethod.POST})
    public String personPage(Model model,
                             @PathVariable(value = "userId") int userId){
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFollowingCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }
}
