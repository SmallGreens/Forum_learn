package com.mattLearn.controller;

import com.mattLearn.model.HostHolder;
import com.mattLearn.model.Question;
import com.mattLearn.service.QuestionService;
import com.mattLearn.util.ForumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

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
}
