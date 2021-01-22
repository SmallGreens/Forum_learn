package com.mattLearn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mattLearn.model.User;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import javax.naming.Name;

/**
 * @author Matt
 * @date 2021/1/22 14:56
 */
public class JedisTests {
    public static void print(int index, Object obj){
        System.out.printf("%d, %s%n", index, obj.toString());
    }

    public static void main(String[] args) {
       // Jedis jedis = new Jedis();  // 什么都不填的话会默认连接本地的 6379 端口
        Jedis jedis = new Jedis("redis://localhost:6379/8");    // 也可以手动指定。最后的 8 是指reids 中第8个数据库
        jedis.flushDB();    // 清空数据空中的内容

        // key-value 数据库初体验
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newHello");
        print(2, jedis.get("newHello"));

        // 设置过期时间, 可以用于验证码业务
        jedis.setex("hello2", 15, "world"); // 设置过期时间为 15秒。好处是数据库自动帮助删除内容
        print(3, jedis.get("hello2"));

        // 操作数值 https://redis.io/commands#generic
        jedis.set("pv", "100");
        jedis.incr("pv");
        print(4, jedis.get("pv"));  // 101
        jedis.decrBy("pv", 50);
        print(5, jedis.get("pv"));  // 50

        print(5, jedis.keys("*"));  // 通配符，打印出所有 key

        // List 的使用 https://redis.io/commands#list
        String listName = "list";
        for(int i = 0; i < 10; ++i){
            jedis.lpush(listName, "a" + String.valueOf(i));  // 向 list 中添加元素。 这里实际上看作一个栈结构
        }
        print(6, jedis.lrange(listName, 0,3)); // 取出一个范围内的 list 的内容
        // 输出 “[a9, a8, a7, a6]”， 两边都是闭区间。
        print(6, jedis.llen(listName));
        print(6, jedis.lpop(listName));         // a9
        print(6, jedis.llen(listName));
        print(6, jedis.lindex(listName, 8));    // a0
        jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "hello");
        print(6, jedis.lrange(listName, 0, 10));    //[a8, a7, a6, a5, a4, hello, a3, a2, a1, a0]

        // hash 的使用 -- 类似 java 中的 Map  https://redis.io/commands#hash
        String userKey = "user1";
        jedis.hset(userKey, "name", "Tom");
        jedis.hset(userKey, "age", "18");
        jedis.hset(userKey, "phone", "888888");
        print(7, jedis.hget(userKey, "name"));
        print(7, jedis.hgetAll(userKey));   // {phone=888888, name=Tom, age=18}
        jedis.hdel(userKey,"phone");    //删除
        print(7, jedis.hgetAll(userKey));   // {name=Tom, age=18}

        // set 的使用 https://redis.io/commands#set
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for(int i = 0; i < 10; ++i){
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i * i));
        }
        jedis.srem(likeKey1, "5");    // 删除
        print(8, "共有： " + jedis.scard(likeKey1));
        print(8, jedis.smembers(likeKey1));
        print(8, jedis.smembers(likeKey2)); // 取出集合中的元素
        print(8, jedis.sunion(likeKey1,likeKey2));  // 求并
        print(8, jedis.sdiff(likeKey1, likeKey2));  // 求差别
        print(8, "交集 " + jedis.sinter(likeKey1,likeKey2));  // 求交集

        // 优先队列 - heap， priorityQueue。sortedSet: https://redis.io/commands#sorted_set
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim"); // 插入中的数字默认都是浮点存储
        jedis.zadd(rankKey, 50, "David");
        jedis.zadd(rankKey, 69, "Lip");
        jedis.zadd(rankKey, 90, "Matt");
        jedis.zadd(rankKey, 49, "Andy");
        jedis.zadd(rankKey, 23, "Rudi");
        print(9, jedis.zcard(rankKey));
        print(9, jedis.zcount(rankKey, 50,100));    // 数出范围内元素的个数
        print(9, jedis.zscore(rankKey, "Andy"));
        jedis.zincrby(rankKey, 2, "Andy");  // 增加 2
        print(9, "Andy " + jedis.zscore(rankKey, "Andy"));
        print(9, jedis.zrange(rankKey, 0, 3));  // 打印出第 1到第4名。默认从小达到排序！
        print(9, jedis.zrevrange(rankKey, 0, 3));   // 返回来取前4个
        // 依次取出范围内的元素。
        for(Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, 60, 100)){
            print(9, tuple.getElement() + ": " + tuple.getScore());
        }
        print(9, jedis.zrevrank(rankKey,"Matt"));   // 获取特定用户的排名
        // zlex... 按字典序排序。

        print(10, jedis.get("pv"));
        // jedisPool ...这里报空指针错误。。不知道为何。。
/*        JedisPool pool = new JedisPool();   // jedis 与 redis 连接
        for(int i = 0; i < 100; ++i){
            Jedis j = pool.getResource();
            print(10, j.get("pv"));
            j.close();  // 用完之后一定要 close 资源
        }*/

        User user = new User();
        user.setName("XiaoA");
        user.setPassword("123");
        user.setHeadUrl("a.png");
        user.setSalt("xx");
        user.setId(1);
        jedis.set("user1", JSONObject.toJSONString(user));  // 序列化存储在数据库中
        print(11, jedis.get("user1"));  // {"headUrl":"a.png","id":1,"name":"XiaoA","password":"123","salt":"xx"}
        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value, User.class);   // 取出对象

    }
}
