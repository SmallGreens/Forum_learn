package com.mattLearn.controller;

import com.mattLearn.model.*;
import com.mattLearn.service.CommentService;
import com.mattLearn.service.QuestionService;
import com.mattLearn.service.UserService;
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
            vo.set("comment", comment);
            vo.set("user", userService.getUser(comment.getUserId()));
            comments.add(vo);
        }
        // 每一个 vo 中包含一个 comment 以及 comment 相关的用户的数据
        model.addAttribute("comments", comments);

        return "detail";
    }
}
