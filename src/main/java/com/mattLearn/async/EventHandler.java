package com.mattLearn.async;

import java.util.List;

/**
 * @author Matt
 * @date 2021/1/25 16:14
 *
 * 用于处理 event 的 handler 的接口
 */
public interface EventHandler {
    void doHandle(EventModel event);

    List<EventType> getSupportEventTypes();  // 注册自己，声明自己支持处理的 event 类型
}
