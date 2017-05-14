package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

/**
 * Created by dell on 2017/5/14.
 */
@Service
public class JedisAdapter  implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    public static void print(int index , Object obj){
        System.out.println(String.format("%d , %s" , index , obj.toString()));
    }

    public static void main(String[] args){
        //创建redis对象
        Jedis jedis = new Jedis();
        jedis.flushAll();
        jedis.set("hello"  , "world");
        print(1,jedis.get("hello"));
        jedis.rename("hello" , "newhello");
        print(2,jedis.get("newhello"));

        jedis.setex("aa" , 10 , "this is aa");
        print(2, jedis.get("aa"));
        print(2, jedis.ttl("aa"));

        jedis.set("pv" , "100");
        jedis.incr("pv");
        jedis.decrBy("pv" , 6);
        print(3, jedis.get("pv"));
        print(3 , jedis.keys("*"));

        //列表操作
        String listName = "list";
        jedis.del(listName);
        for(int i=0;i<=10;++i){
            jedis.lpush(listName , "a" + String.valueOf(i));
        }
        print(4 , jedis.lrange(listName , 0 , 12));
        print(5 , jedis.llen(listName));
        print(6 , jedis.lpop(listName));
        print(7 , jedis.llen(listName));
        print(8 , jedis.lrange(listName , 2, 6));
        print(9 , jedis.lindex(listName , 3));
        print(10 , jedis.linsert(listName , BinaryClient.LIST_POSITION.AFTER , "a5" , "after"));
        print(10 , jedis.linsert(listName , BinaryClient.LIST_POSITION.BEFORE , "a5" , "before"));
        print(11 , jedis.lrange(listName , 0 , 12));


        //hash,可变字段
        String userkey="alice";
        jedis.hset(userkey , "name" , "jack");
        jedis.hset(userkey , "age" , "18");
        jedis.hset(userkey , "phone" , "123243");
        print(12 , jedis.hget(userkey , "name"));
        print(13 , jedis.hgetAll(userkey));
        jedis.hdel(userkey , "phone");
        print(14 , jedis.hgetAll(userkey));
        print(15 , jedis.hexists(userkey , "email"));
        print(16 , jedis.hexists(userkey , "age"));
        print(17 , jedis.hkeys(userkey));
        print(18 , jedis.hvals(userkey));
        jedis.hsetnx(userkey , "school" , "zju");
        jedis.hsetnx(userkey , "name" , "haha");
        print(19 , jedis.hgetAll(userkey));

        //集合对象
        String like1 = "newsLike1";
        String like2 = "newsLike2";
        for(int i=1;i<=10 ; ++i){
            jedis.sadd(like1 , String.valueOf(i));
            jedis.sadd(like2 , String.valueOf(i*i));
        }
        print(20 , jedis.smembers(like1));
        print(21 , jedis.smembers(like2));
        print(22 , jedis.sunion(like1 ,like2));
        print(23 , jedis.sdiff(like1 ,like2));
        print(24 , jedis.sismember(like1 , "6"));
        print(25 , jedis.sismember(like1 , "19"));
        jedis.srem(like1 , "7");
        print(26 , jedis.smembers(like1));
        jedis.smove(like1 , like2 , "9");
        print(27 , jedis.smembers(like1));
        print(28 , jedis.smembers(like2));


        //排序集合，有限队列
        String rankKey = "rankKey";
        jedis.zadd(rankKey , 15 , "alice");
        jedis.zadd(rankKey , 60 , "jack");
        jedis.zadd(rankKey , 90 , "lily");
        jedis.zadd(rankKey , 75 , "lucy");
        jedis.zadd(rankKey , 80 , "Anne");
        print(30 , jedis.zcard(rankKey));
        print(31 , jedis.zcount(rankKey , 61 , 100));
        print(32 , jedis.zscore(rankKey , "lily"));
        jedis.zincrby(rankKey , 5 , "lily");
        print(33 , jedis.zscore(rankKey , "lily"));
        jedis.zincrby(rankKey , 8 , "Alisa");
        print(34 , jedis.zcount(rankKey , 0 , 100));
        print(35 , jedis.zrange(rankKey , 0 , 10));
        print(36 , jedis.zrange(rankKey , 0 , 3));
        for(Tuple tuple : jedis.zrangeByScoreWithScores(rankKey , 60 , 100)){
            print(37 , tuple.getElement() + " : " + String.valueOf(tuple.getScore()));
        }
        print(38 , jedis.zrank(rankKey , "Anne"));
        print(39 , jedis.zrange(rankKey , 0 , 20));
        print(40 , jedis.zrevrank(rankKey , "Anne"));

        String setKey = "zset";
        jedis.zadd(setKey , 1 , "a");
        jedis.zadd(setKey , 1 , "b");
        jedis.zadd(setKey , 1 , "c");
        jedis.zadd(setKey , 1 , "d");
        jedis.zadd(setKey , 1 , "e");
        print(41 , jedis.zlexcount(setKey , "-" , "+"));
        print(42 , jedis.zlexcount(setKey , "(b" , "[d"));
        print(43 , jedis.zlexcount(rankKey , "[b" , "[d"));
        jedis.zrem(setKey , "b");
        print(44 , jedis.zrange(setKey , 0 , 10));

        JedisPool pool = new JedisPool();
        for(int i=0;i<100;++i){
            Jedis j = pool.getResource();
            j.get("a");
            j.close();
        }
    }

    private Jedis jedis = null;
    private JedisPool pool = null;

    //当spring加载完该类后会自动调用的方法
    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost" , 6379);
    }

    //获取redis对象
    private Jedis getJedis(){
        return pool.getResource();
    }

    public String get(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.get(key);
        }catch (Exception e){
            logger.error("redis获取值错误" + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void set(String key ,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.set(key, value);
        }catch (Exception e){
            logger.error("redis存储值发生错误" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long sadd(String key ,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key ,value);
        }catch (Exception e){
            logger.error("redis存储值发生错误" + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long srem(String key ,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key , value);
        }catch (Exception e){
            logger.error("redis存储值发生错误" + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public boolean sismember(String key ,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key ,value);
        }catch (Exception e){
            logger.error("redis存储值发生错误" + e.getMessage());
            return false;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("redis存储值发生错误" + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    //在指定的时间内有效
    public void setex(String key , String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.setex(key , 10 , value);
        }catch (Exception e){
            logger.error("redis存储值发生错误" + e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long lpush(String key , String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.lpush(key , value);
        }catch (Exception e){
            logger.error("redis存储值发生错误" + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout , String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        }catch (Exception e){
            logger.error("redis存储值发生错误" + e.getMessage());
            return null;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public void setObject(String key , Object obj){
        set(key , JSON.toJSONString(obj));
    }

    public <T> T getObject(String key , Class<T> clazz){
        String value = get(key);
        if(value!=null){
            return JSON.parseObject(value , clazz);
        }
        return null;
    }

}
