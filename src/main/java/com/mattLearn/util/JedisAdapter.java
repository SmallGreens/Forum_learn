package com.mattLearn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author Matt
 * @date 2021/1/23 14:33
 */

@Service
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    // 构建一个连接池
    private JedisPool pool;

    @Override
    public void afterPropertiesSet() throws Exception {
        // bean 初始化的时候将 pool 初始化
        pool = new JedisPool("redis://localhost:6379/10");
    }

    // 封装 jedis 的接口函数
    public long sadd(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        }catch (Exception e){
            logger.error("Adding data to redis failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return 0;
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        }catch (Exception e){
            logger.error("Removing data from redis failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return 0;
    }


    public long scard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("Counting data of redis failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return 0;
    }

    public boolean sisMember(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        }catch (Exception e){
            logger.error("Query if element is member falied. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return false;
    }

}
