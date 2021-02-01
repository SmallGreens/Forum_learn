package com.mattLearn.controller;

import com.mattLearn.async.EventModel;
import com.mattLearn.async.EventProducer;
import com.mattLearn.async.EventType;
import com.mattLearn.model.*;
import com.mattLearn.service.CommentService;
import com.mattLearn.service.FollowService;
import com.mattLearn.service.QuestionService;
import com.mattLearn.service.UserService;
import com.mattLearn.util.ForumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    CommentService commentService;

    // 关注用户
    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String follow(@RequestParam("userId") int userId){
        if(hostHolder == null){
            return ForumUtil.getJSONString(999);    // 如果用户未登录，由前端进行跳转
        }
        boolean res = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setEntityType(EntityType.ENTITY_USER)
                .setActorId(hostHolder.getUser().getId())
                .setEntityOwnerId(userId).setEntityId(userId));
        // 返回我关注的人数
        return ForumUtil.getJSONString(res? 0: 1,
                String.valueOf(followService.getFollowingCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    // 取关用户
    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST})
    @ResponseBody
    public String unfollow(@RequestParam("userId") int userId){
        if(hostHolder == null){
            return ForumUtil.getJSONString(999);
        }
        boolean res = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        return ForumUtil.getJSONString(res? 0: 1,
                String.valueOf(followService.getFollowingCount(hostHolder.getUser().getId(),EntityType.ENTITY_USER)));
    }

    // 关注问题
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

        // 用于 feed 流的产生。处理部分参看 FeedHandler.java
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
                .setEntityType(EntityType.ENTITY_QUESTION)
                .setActorId(hostHolder.getUser().getId())
                .setEntityOwnerId(q.getUserId()).setEntityId(questionId));

        Map<String, Object> info = new HashMap<>();
        // 获取用户的相关信息，用于前端显示
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return ForumUtil.getJSONString(res? 0: 1, info);
    }

    // 取关问题
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

        Map<String, Object> info = new HashMap<>();
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
        return ForumUtil.getJSONString(res? 0: 1, info);
    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followings(@PathVariable("uid") int userId, Model model){       // 主体是用户，该用户关注的对象

        List<Integer> followingIds = followService.getFollowing(userId, EntityType.ENTITY_USER,0, 10);

        if(hostHolder.getUser() != null) {
            model.addAttribute("followees", getUserInfo(hostHolder.getUser().getId(),followingIds));
        }else{
            model.addAttribute("followees", getUserInfo(0,followingIds));   // 用户没有登录
        }
        model.addAttribute("followeeCount", followService.getFollowingCount(userId,EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }


    @RequestMapping(path = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(@PathVariable("uid") int userId, Model model){       // 粉丝列表，查看该用户的粉丝

        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);

        if(hostHolder.getUser() != null) {
            model.addAttribute("followers", getUserInfo(hostHolder.getUser().getId(),followerIds));
        }else{
            model.addAttribute("followers", getUserInfo(0,followerIds));   // 用户没有登录
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    /**
     *
     * @param localUserId- 为当前浏览网页的用户的 id
     * @param userIds- 为当前页面所展示的用户的粉丝 的 id 列表
     * @return 返回所有粉丝的相关信息。
     */
    private List<ViewObject> getUserInfo(int localUserId, List<Integer> userIds){
        List<ViewObject> userInfo = new ArrayList<>();
        for(Integer uid : userIds){
            User user = userService.getUser(uid);
            if(user == null) continue;
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowingCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFollowingCount(uid, EntityType.ENTITY_USER));
            if(localUserId != 0){
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            }else {
                vo.set("followed", false);
            }
            userInfo.add(vo);
        }
        return userInfo;
    }
}
