package com.mattLearn.util;

/**
 * @author Matt
 * @date 2021/1/23 14:53
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static String BIZ_FOLLOWER = "FOLLOWER";    // 关注服务 - 粉丝, 获取一个对象的粉丝列表
    private static String BIZ_FOLLOWED = "FOLLOWED";    // 关注服务 - 关注对象。 获取一个人的关注内容

    public static String getLikeKey(int entityType, int entityId){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getBizEventQueueKey(){
        return BIZ_EVENTQUEUE;
    }

    public static String getFollowerKey(int entityType, int entityId){
        return BIZ_FOLLOWER + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getFollowedKey(int userId, int entityType){
        return BIZ_FOLLOWED + SPLIT + String.valueOf(userId) + SPLIT + String.valueOf(entityType);
    }



}
