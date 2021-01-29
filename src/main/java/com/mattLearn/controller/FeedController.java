package com.mattLearn.controller;

import com.mattLearn.model.EntityType;
import com.mattLearn.model.Feed;
import com.mattLearn.model.HostHolder;
import com.mattLearn.model.User;
import com.mattLearn.service.FeedService;
import com.mattLearn.service.FollowService;
import com.mattLearn.util.JedisAdapter;
import com.mattLearn.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Matt
 * @date 2021/1/29 19:30
 *
 * 用于 新鲜事 页面的显示
 *
 */

@Controller
public class FeedController {
    @Autowired
    FeedService feedService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    // 拉 模式
    @RequestMapping(path = "/pullfeeds", method = {RequestMethod.GET})
    public String getPullFeeds(Model model){
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();  // 获得当前页的 host 者
        List<Integer> followings = new ArrayList<>();
        if(localUserId != 0){
            // 取出所有人
            followings = followService.getFollowing(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
           // System.out.println(followings.toString()); 看关注我的有哪些人
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followings, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    // 推模式
    @RequestMapping(path = "/pushfeeds", method = {RequestMethod.GET})
    public String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();  // 获得当前页的 host 者
        // string 类型
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
        List<Feed> feeds = new ArrayList<>();
        for(String feedId: feedIds){
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if(feed != null) feeds.add(feed);
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }
}
