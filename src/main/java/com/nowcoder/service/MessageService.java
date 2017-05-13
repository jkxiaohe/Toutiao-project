package com.nowcoder.service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2017/5/11.
 */
@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;

    public int addMessage(Message message){
        return messageDAO.addMessage(message);
    }

    public List<Message> getConversationList(int userId , int offset , int limit){
        return messageDAO.getConversationList(userId, offset, limit);
    }

    public List<Message> getConversationDetail(String conversationId , int offset , int limit){
        return messageDAO.getConversationDetail(conversationId,offset , limit);
    }

    public int getUnreadCount(int userId , String conversationId){
        return messageDAO.getConversationUnReadCount(userId, conversationId);
    }

    public Date getConversationDate(String conversationId){
        return messageDAO.getConversationDate(conversationId);
    }

}
