package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dell on 2017/5/8.
 */
@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }

    public List<Comment> getCommentsByEntity(int entityId , int entityType){
        return commentDAO.selectByEntity(entityId, entityType);
    }

    public int getCommentCount(int entityId , int entityType){
        return commentDAO.getCommentCount(entityId, entityType);
    }
}
