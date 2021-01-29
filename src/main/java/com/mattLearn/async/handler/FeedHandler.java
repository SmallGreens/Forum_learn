package com.mattLearn.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.mattLearn.async.EventHandler;
import com.mattLearn.async.EventModel;
import com.mattLearn.async.EventType;
import com.mattLearn.model.EntityType;
import com.mattLearn.model.Feed;
import com.mattLearn.model.Question;
import com.mattLearn.model.User;
import com.mattLearn.service.FeedService;
import com.mattLearn.service.FollowService;
import com.mattLearn.service.QuestionService;
import com.mattLearn.service.UserService;
import com.mattLearn.util.ForumUtil;
import com.mattLearn.util.JedisAdapter;
import com.mattLearn.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * @author Matt
 * @date 2021/1/29 16:46
 */

@Component
public class FeedHandler implements EventHandler {

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    private String buildFeedData(EventModel model){
        Map<String, String> map = new HashMap<>();
        User actor = userService.getUser(model.getActorId());
        if(actor == null) return null;
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", String.valueOf(actor.getHeadUrl()));
        map.put("userName", String.valueOf(actor.getName()));

        if (model.getType() == EventType.COMMENT ||         // 回答或者关注了问题。
                (model.getType() == EventType.FOLLOW  && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.getQuestionById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }


    @Override
    public void doHandle(EventModel event) {
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setUserId(event.getActorId());
        feed.setType(event.getType().getValue());
        String data;
        if((data = buildFeedData(event)) == null) return;   // ref java 源码中的写法
        feed.setData(data);
        feedService.addFeed(feed);

        // 给事件的粉丝推 -- 这里简化地推给所有的粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER,
                event.getActorId(), Integer.MAX_VALUE);
        followers.add(0);   // 系统id
        for(Integer follower: followers){
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT, EventType.FOLLOW);
    }
}
