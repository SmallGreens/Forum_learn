package com.mattLearn.controller;

import com.mattLearn.model.*;
import com.mattLearn.service.*;
import com.mattLearn.util.ForumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Matt
 * @date 2021/1/18 14:55
 *
 * 用于用户发布问题
 *
 */

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Autowired
    FollowService followService;

    @RequestMapping(path = "/question/add", method = {RequestMethod.POST})
    @ResponseBody       // json 形式返回
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setContent(content);
            question.setTitle(title);
            // 如果没有登录，给一个匿名的用户id
            question.setCreatedDate(new Date());
            if(hostHolder.getUser() == null){
                question.setUserId(ForumUtil.ANONYMOUS_ID);
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }

            if(questionService.addQuestion(question) > 0){
                return ForumUtil.getJSONString(0);
            }

        }catch (Exception e){
            logger.error("Adding question failed!" + e.getMessage());
        }

        return ForumUtil.getJSONString(1,"Error!");
    }

    @RequestMapping(path = "/question/{questionID}", method = {RequestMethod.GET})
    public String questionDetail(Model model,
                                 @PathVariable("questionID") int questionId){
        Question question = questionService.getQuestionById(questionId);
        User user = userService.getUser(question.getUserId());
        model.addAttribute("question", question);
        model.addAttribute("user", user);

        List<Comment> commentList = commentService.getCommentByEntity(questionId, EntityType.ENTITY_QUESTION);
        List<ViewObject> comments = new ArrayList<>();
        for(Comment comment :commentList){
            ViewObject vo  = new ViewObject();

            // 判断当前用户对该问题的喜欢状态，从而决定前端的显示状态
            if(hostHolder.getUser() == null){
                vo.set("liked", 0);
            }else{
                int status = likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId());
                vo.set("liked", status);
            }
            // 当前问题的赞的数量
            long count = likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId());
            vo.set("likeCount", count);

            vo.set("comment", comment);
            vo.set("user", userService.getUser(comment.getUserId()));
            comments.add(vo);
        }
        // 每一个 vo 中包含一个 comment 以及 comment 相关的用户的数据
        model.addAttribute("comments", comments);

        List<ViewObject> followUsers = new ArrayList<>();
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, questionId, 20);
        for(Integer userId : users){
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if(u == null) continue;
            vo.set("headUrl", u.getHeadUrl());
            vo.set("name", u.getName());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if(hostHolder.getUser() != null){
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(),
                    EntityType.ENTITY_QUESTION, questionId));
        }else{
            model.addAttribute("followed", false);
        }
        return "detail";
    }
}
