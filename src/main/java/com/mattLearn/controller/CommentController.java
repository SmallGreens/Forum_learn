package com.mattLearn.controller;

import com.mattLearn.model.Comment;
import com.mattLearn.model.EntityType;
import com.mattLearn.model.HostHolder;
import com.mattLearn.service.CommentService;
import com.mattLearn.service.QuestionService;
import com.mattLearn.util.ForumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author Matt
 * @date 2021/1/20 17:44
 */

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = "/addComment", method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            if(hostHolder.getUser() != null){
                comment.setUserId(hostHolder.getUser().getId());
            }else{
                comment.setUserId(ForumUtil.ANONYMOUS_ID);
                // return "redirect:/reglogin";
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            commentService.addComment(comment);

            // 更新评论数量，从 comment 表中数出评论的数量，跟新到 question表中
            // for temp use.
            int count = commentService.getCommentCount(questionId,EntityType.ENTITY_QUESTION);
            questionService.updateCommentCount(count,questionId);

        } catch (Exception e) {
            logger.error("Error while adding the comment." + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
