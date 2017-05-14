package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dell on 2017/5/14.
 */
@Service
public class LikeService {
    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);

    @Autowired
    JedisAdapter jedisAdapter;

    //获取赞踩的状态（赞，踩，无  ： 1，-1 ，0）
    public int getLikeStatus(int userId , int entityType , int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if(jedisAdapter.sismember(likeKey , String.valueOf(userId))){
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey , String.valueOf(userId)) ? -1 : 0;
    }

    //添加点赞功能
    public long like(int userId , int entityType , int entityId){
        //获取与该条资讯相关的key名称
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.sadd(likeKey , String.valueOf(userId));
        //从反对里面删除该用户
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey , String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    //添加反对的功能
    public long disLike(int userId , int entityType , int entityId){
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey , String.valueOf(userId));
        //从点赞里面删除该用户
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey , String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

}
