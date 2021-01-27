package com.mattLearn.async;

/**
 * @author Matt
 * @date 2021/1/25 15:39
 *
 * EventType 描述事件的类型。使用枚举类来表示。
 *
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5);

    private int value;
    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
