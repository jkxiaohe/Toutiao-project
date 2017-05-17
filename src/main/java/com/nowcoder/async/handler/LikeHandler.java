package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2017/5/14.
 */
@Component
public class LikeHandler  implements EventHandler{

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    @Override
    public void doHandler(EventModel model) {
        //将用户点赞的事件封装为一个消息
        Message message = new Message();
        //获取点赞用户的相关信息
        User user = userService.getUser(model.getActorId());
        message.setToId(model.getEntityOwnerId());
        message.setContent("用户" + user.getName() + "对你的资讯点了个赞，地址为：http://127.0.0.1:8080/news/" + model.getEntityId());
        //这封信默认由系统发出
        message.setFromId(2);
        message.setCreatedDate(new Date());
        //将点赞的这条消息添加入数据库
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
