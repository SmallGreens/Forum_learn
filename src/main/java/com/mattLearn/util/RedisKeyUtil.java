package com.mattLearn.util;

/**
 * @author Matt
 * @date 2021/1/23 14:53
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String BIZ_LIKE = "LIKE";
    private static final String BIZ_DISLIKE = "DISLIKE";
    private static final String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static final String BIZ_FOLLOWER = "FOLLOWER";    // 关注服务 - 粉丝, 获取一个对象的粉丝列表
    private static final String BIZ_FOLLOWING = "FOLLOWING";    // 关注服务 - 关注对象。 获取一个人的关注内容
    private static final String BIZ_TIMELINE = "TIMELINE";

    public static String getLikeKey(int entityType, int entityId){
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getDisLikeKey(int entityType, int entityId){
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getBizEventQueueKey(){
        return BIZ_EVENTQUEUE;
    }

    public static String getFollowerKey(int entityType, int entityId){
        return BIZ_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getFollowingKey(int userId, int entityType){
        return BIZ_FOLLOWING + SPLIT + userId + SPLIT + entityType;
    }

    public static String getTimelineKey(int userId){
        return BIZ_TIMELINE + SPLIT + userId;
    }
}
