package com.mattLearn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    // 封装 jedis 的接口函数
    public long lpush(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        }catch (Exception e){
            logger.error("Adding data to redis failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return 0;
    }

    // 封装 jedis 的接口函数
    public List<String> lrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, start, end);
        }catch (Exception e){
            logger.error("Adding data to redis failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return null;
    }

    // 封装 jedis 的接口函数
    public List<String> brpop(int timeOut, String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeOut, key);
        }catch (Exception e){
            logger.error("Adding data to redis failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return new ArrayList<>();
    }

    public long zadd(String key, double score, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score, value);
        }catch (Exception e){
            logger.error("Sorted set add value error for redis. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return 0;
    }

    public long zrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key, value);
        } catch (Exception e) {
            logger.error("Failed when remove the data from zset. " + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }

    // 封装 jedis 的接口函数 zrange
    public Set<String> zrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key, start, end);
        }catch (Exception e){
            logger.error("Fetching data from redis sorted set failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return null;
    }

    // 封装 jedis 的接口函数 zrange
    public Set<String> zrevrange(String key, int start, int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, start, end);
        }catch (Exception e){
            logger.error("Fetching data from redis sorted set reversed failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return null;
    }

    public long zcard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        }catch (Exception e){
            logger.error("Counting the number of the elements in sorted set failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return 0;
    }

    public Double zscore(String key, String member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key, member);
        }catch (Exception e){
            logger.error("Zscore failed. " + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();  // 最后关闭资源，将资源返回给 连接池
            }
        }
        return null;
    }

    public Jedis getJedis(){
        return pool.getResource();
    }

    // 事务的开始
    public Transaction multi(Jedis jedis){
        try {
            return jedis.multi();
        }catch (Exception e){
            logger.error("Error while get the transaction for redis. " + e.getMessage());
        }
        return null;
    }

    // 事务的执行
    public List<Object> exec(Transaction tx, Jedis jedis){
        try {
            return tx.exec();
        }catch (Exception e){
            logger.error("Error while exec the transation of the redis " + e.getMessage());
        } finally {
            if(tx != null){
                try {
                    tx.close();
                }catch (IOException ioException){
                    logger.error("Error while close the transaction of the redis. " + ioException.getMessage());
                }
            }
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }
}
