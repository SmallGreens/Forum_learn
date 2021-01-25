package com.mattLearn.async;

import com.alibaba.fastjson.JSON;
import com.mattLearn.util.JedisAdapter;
import com.mattLearn.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matt
 * @date 2021/1/25 16:19
 *
 * 需要负责分发 event
 */

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;  // 应用上下文

    // 添加 bean 的初始化函数
    @Override
    public void afterPropertiesSet() throws Exception {
        // 系统启动的时候，找出所有实现了 EventHandler 的类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        /*
        * 这里实际上主要实现了一个映射的转换。将 eventHandler 的从 handler --》 EventType 的映射
        * 变成了 从 eventType --》 handler list 的映射
        *
        * */
        if(beans != null){
            for(Map.Entry<String, EventHandler> entry: beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();   // 获取 handler 支持的 eventType
                for(EventType type : eventTypes){
                    if(!config.containsKey(type)){
                        config.put(type, new ArrayList<>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key = RedisKeyUtil.getBizEventQueueKey();    // 获得对应的 queue 消息队列
                    List<String> events = jedisAdapter.brpop(0, key);
                    for(String message : events){
                        if(message.equals(key)){
                            continue;
                        }
                        // 解析出 eventModel
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("Can't recognize this event.");
                            continue;
                        }

                        for(EventHandler handler : config.get(eventModel.getType())){
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
