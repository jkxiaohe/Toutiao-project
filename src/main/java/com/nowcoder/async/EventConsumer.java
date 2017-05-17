package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/5/14.
 */
@Service
public class EventConsumer implements InitializingBean , ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType , List<EventHandler>> config = new IdentityHashMap<>();
    private ApplicationContext applicationContext;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //通过application获取所有的事件处理器
        Map<String , EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null){
            for(Map.Entry<String , EventHandler> entry : beans.entrySet()){
                //获取该处理器所支持的所有事件类型
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for(EventType type : eventTypes){
                    if(!config.containsKey(type)){
                        //如果当前的web项目中没有对应的事件处理器类型，进行加入
                        config.put(type , new ArrayList<EventHandler>());
                    }
                    //如果当前的web中有相应的事件处理器，注册
                    config.get(type).add(entry.getValue());
                }
            }
        }

        //启动线程去处理事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //该线程启动后会一直监控队列中是否有新的事件需要处理
                while(true){
                    //获取事件队列的名称
                    String key = RedisKeyUtil.getEventQueueKey();
                    //将事件队列中最后一个事件弹出处理
                    List<String> messges = jedisAdapter.brpop(0 , key);
                    //brpop弹出的第一个元素是队列的名字
                    for(String message : messges){
                        if (message.equals(key)) {
                            continue;
                        }
                        EventModel eventModel = JSONObject.parseObject( message , EventModel.class);
                        //找到处理这个事件的handler列表
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("不能处理的事件");
                            continue;
                        }
                        for(EventHandler handler : config.get(eventModel.getType())){
                            handler.doHandler(eventModel);
                        }
                    }

                }
            }
        });

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
