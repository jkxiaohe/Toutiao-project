package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.service.MessageService;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.util.Hashing;

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/5/14.
 */
public class LoginExceptionHandler implements EventHandler {

    @Autowired
    MessageService messageService;
    @Autowired
    MailSender mailSender;

    @Override
    public void doHandler(EventModel model) {
        Message message = new Message();
        message.setToId(model.getActorId());
        message.setContent("你上次登陆的ip异常");
        //默认邮件由系统用户发出
        message.setFromId(2);
        message.setCreatedDate(new Date());
        messageService.addMessage(message);

        Map<String,Object> map = new IdentityHashMap<>();
        map.put("username" , model.getExt("username"));


    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return null;
    }
}
