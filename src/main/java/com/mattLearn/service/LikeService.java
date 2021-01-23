package com.mattLearn.service;

import com.mattLearn.util.JedisAdapter;
import com.mattLearn.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Matt
 * @date 2021/1/23 14:50
 */
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    // 获得当前问题 like 的人数，用于页面上 like 状态的显示
    public long getLikeCount(int entityType, int entityId) {
        return jedisAdapter.scard(RedisKeyUtil.getLikeKey(entityType, entityId));
    }

    // 获得当前用户对对应问题的喜欢状态-》 从而来决定页面的显示效果。
    public int getLikeStatus(int userId, int entityType, int entityId) {
        if (jedisAdapter.sisMember(RedisKeyUtil.getLikeKey(entityType, entityId), String.valueOf(userId))) {
            return 1;   // 喜欢状态
        }
        if (jedisAdapter.sisMember(RedisKeyUtil.getDisLikeKey(entityType, entityId), String.valueOf(userId))) {
            return -1; // 不喜欢状态
        }
        return 0;   // 未定状态
    }

    // 用户点击 like 时对应的操作。将用户的 id 添加到 redis like set 中，并且返回 like set 中用户的总个数
    public long like(int userId, int entityType, int entityId){
        // key 的设计一定要小心。指定一套统一的规则，保证不同业务之间的 key 不会重复
        // method: 可以写一个专门生成 key 的util
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

    public long dislike(int userId, int entityType, int entityId){

        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));

        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }
}
