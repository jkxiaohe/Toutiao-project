package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dell on 2017/5/14.
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean releEvent(EventModel eventModel){
        try{
            String json = JSONObject.toJSONString(eventModel);
            //获取发布事件队列的名称
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key , json);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
