package com.mattLearn.async;

import com.alibaba.fastjson.JSONObject;
import com.mattLearn.util.JedisAdapter;
import com.mattLearn.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Matt
 * @date 2021/1/25 15:55
 *
 * 使用 redis 中的 zset来实现。
 *
 */

@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
        try{
            String json = JSONObject.toJSONString(eventModel);
        //    if(eventModel.getExts("email")!= null) System.out.println(eventModel.getExts("email"));
        //    System.out.println(json);
            String key = RedisKeyUtil.getBizEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
