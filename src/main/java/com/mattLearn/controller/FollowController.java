package com.mattLearn.controller;

import com.mattLearn.async.EventModel;
import com.mattLearn.async.EventProducer;
import com.mattLearn.async.EventType;
import com.mattLearn.model.*;
import com.mattLearn.service.FollowService;
import com.mattLearn.service.QuestionService;
import com.mattLearn.service.UserService;
import com.mattLearn.util.ForumUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matt
 * @date 2021/1/27 18:26
 *
 *  关注的相关请求处理
 *  页面：两个 关注页面，关注问题页面 ...
 *
 */

@Controller
public class FollowController {

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    QuestionService questionService;

    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String follow(@RequestParam("userId") int userId){
        if(hostHolder == null){
            return ForumUtil.getJSONString(999);
        }
        boolean res = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setEntityType(EntityType.ENTITY_USER)
                .setActorId(hostHolder.getUser().getId()).setEntityOwnerId(userId).setEntityId(userId));
        return ForumUtil.getJSONString(res? 0: 1,
                String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollow(@RequestParam("userId") int userId){
        if(hostHolder == null){
            return ForumUtil.getJSONString(999);
        }
        boolean res = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setEntityType(EntityType.ENTITY_USER)
                .setActorId(hostHolder.getUser().getId()).setEntityOwnerId(userId).setEntityId(userId));
        return ForumUtil.getJSONString(res? 0: 1,
                String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/followQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder == null){
            return ForumUtil.getJSONString(999);
        }
        Question q = questionService.getQuestionById(questionId);
        if(q == null){
            return ForumUtil.getJSONString(1, "Question is not exist!!");
        }

        boolean res = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setEntityType(EntityType.ENTITY_QUESTION)
                .setActorId(hostHolder.getUser().getId()).setEntityOwnerId(q.getUserId()).setEntityId(questionId));
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return ForumUtil.getJSONString(res? 0: 1, info);
    }

    @RequestMapping(path = {"/unfollowQuestion"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId){
        if(hostHolder == null){
            return ForumUtil.getJSONString(999);
        }
        Question q = questionService.getQuestionById(questionId);
        if(q == null){
            return ForumUtil.getJSONString(1, "Question is not exist!!");
        }

        boolean res = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setEntityType(EntityType.ENTITY_QUESTION)
                .setActorId(hostHolder.getUser().getId()).setEntityOwnerId(q.getUserId()).setEntityId(questionId));
        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return ForumUtil.getJSONString(res? 0: 1, info);
    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(@PathVariable("uid") int userId){       // 主体是用户，该用户关注的人
        if(hostHolder == null){
            return ForumUtil.getJSONString(999);
        }
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER,0, 10);


    }


    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followees(@PathVariable("uid") int userId){       // 粉丝列表，查看该用户的粉丝
        if(hostHolder == null){
            return ForumUtil.getJSONString(999);
        }
        List<Integer> followeeIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);



    }


    private List<ViewObject> getUserInfo(int localUserId, List<Integer> userIds){
        List<ViewObject> userInfos = new ArrayList<>();
        for(Integer uid : userIds){
            User user = userService.getUser(uid);
            if(user == null) continue;
        }
        ViewObject vo = new ViewObject();
        vo.set();
        return vo;
    }

}
