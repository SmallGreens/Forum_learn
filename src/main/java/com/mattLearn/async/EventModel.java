package com.mattLearn.async;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Matt
 * @date 2021/1/25 15:42
 */

public class EventModel {
    private EventType type;     // 以对评论点赞为例子
    private int actorId;        // actor 表示是谁点的赞
    private int entityType;     // 表示对什么类型的东西进行点赞，例如是对评论点赞
    private int entityId;       // 被点赞评论的 id
    private int entityOwner;    // 对应的 评论的拥有者。

    private Map<String, String> exts = new HashMap<>();     // 拓展字段用于保存额外的信息

    public EventModel() {
    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) { // 返回 set 对应的对象，便于链式的进行调用
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwner() {
        return entityOwner;
    }

    public EventModel setEntityOwner(int entityOwner) {
        this.entityOwner = entityOwner;
        return this;
    }

    public String getExts(String key) {
        return exts.get(key);
    }

    public EventModel setExts(String key, String value) {
        exts.put(key,value);
        return this;
    }
}
