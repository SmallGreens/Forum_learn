package com.mattLearn.service;

import com.mattLearn.util.JedisAdapter;
import com.mattLearn.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Matt
 * @date 2021/1/27 17:23
 */
@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 用户的关注行为 引发的系列操作。通用型的函数，适用于关注 人、问题 或其他实体。
     *
     * @param userId-执行关注动作的用户 ID
     * @param entityType- 关注的对象实体
     * @param entityId- 关注的对象的实体的 id
     * @return 是否关注成功。
     */
    public boolean follow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId); // 一个被关注对象的所有的粉丝的集合
        String followedKey = RedisKeyUtil.getFollowingKey(userId, entityType);   // 一个人关注的所有的对象
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis); // 将上述jedis 对象包装成 事务型操作的对象
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));       // 将该用户添加到关注这个对象的 人群集合中
        tx.zadd(followedKey, date.getTime(), String.valueOf(entityId));     // 将该对象放置到我的关注列表中
        List<Object> list = jedisAdapter.exec(tx, jedis);   // 事务的执行
        return list.size() == 2 && (long) list.get(0) > 0 && (long)list.get(1) > 0;    // 看是否成功执行了上述两个事件
    }

    public boolean unfollow(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId); // 一个被关注对象的所有的粉丝的集合
        String followedKey = RedisKeyUtil.getFollowingKey(userId, entityType);   // 一个人关注的所有的对象
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis); // 将上述jedis 对象包装成 事务型操作的对象
        tx.zrem(followerKey, String.valueOf(userId));       // 将该用户添加到关注这个对象的 人群集合中
        tx.zrem(followedKey, String.valueOf(entityId));     // 将该对象放置到我的关注列表中
        List<Object> list = jedisAdapter.exec(tx, jedis);   // 事务的执行
        return list.size() == 2 && (long) list.get(0) > 0 && (long)list.get(1) > 0;    // 看是否成功执行了上述两个事件
    }

    // util 方法
    private List<Integer> getIdsFromSet(Set<String> idSet){
        List<Integer> ids = new ArrayList<>();
        for(String s: idSet){
            ids.add(Integer.parseInt(s));
        }
        return ids;
    }
    public List<Integer> getFollowers(int entityType, int entityId, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrange(followerKey, 0 ,count));
    }

    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrange(followerKey, offset ,count));
    }

    public List<Integer> getFollowing(int userId, int entityType, int count){
        String followedKey = RedisKeyUtil.getFollowingKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrange(followedKey, 0 ,count));
    }

    public List<Integer> getFollowing(int userId, int entityType, int offset, int count){
        String followingKey = RedisKeyUtil.getFollowingKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrange(followingKey, offset ,count));
    }

    public long getFollowerCount(int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    public long getFollowingCount(int userId, int entityType){
        String followingKey = RedisKeyUtil.getFollowingKey(userId, entityType);
        return jedisAdapter.zcard(followingKey);
    }

    public boolean isFollower(int userId, int entityType, int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;    // 不等于 null 说明在集合中
    }
}
