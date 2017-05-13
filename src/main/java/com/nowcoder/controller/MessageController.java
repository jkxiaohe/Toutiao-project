package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2017/5/11.
 */
@Controller
public class MessageController {

    private final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    MessageService messageService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/detail"} , method = {RequestMethod.GET})
    public String conversationDetail(@RequestParam("conversationId") String conversationId, Model model){
        try{
            List<ViewObject> messages = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationDetail(conversationId , 0 , 10);
            for(Message msg : conversationList){
                ViewObject vo = new ViewObject();
                vo.set("message" , msg);
                User user = userService.getUser(msg.getFromId());
                if(user == null){
                    continue;
                }
                vo.set("headUrl" , user.getHeadUrl());
                vo.set("userName" , user.getName());
                messages.add(vo);
            }
            model.addAttribute("messages" , messages);
            return "letterDetail";
        }catch (Exception e){
            logger.error("获取站内信列表失败：" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = {"/msg/addMessage"} , method = {RequestMethod.GET , RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content){
        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setContent(content);
        message.setCreatedDate(new Date());
        message.setHasRead(0);
        message.setConversationId(fromId < toId ? String.format("%d_%d" , fromId , toId)  :
                String.format("%d_%d" , toId , fromId));
        messageService.addMessage(message);
        return ToutiaoUtil.getJSONString(message.getId());
    }

    @RequestMapping(path = {"/msg/list"} , method = {RequestMethod.GET})
    public String conversationList(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                msg.setCreatedDate(messageService.getConversationDate(msg.getConversationId()));
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userName", user.getName());
                vo.set("targetId", targetId);
                vo.set("totalCount", msg.getId());
                vo.set("unreadCount", messageService.getUnreadCount(localUserId , msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
            return "letter";
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

}
