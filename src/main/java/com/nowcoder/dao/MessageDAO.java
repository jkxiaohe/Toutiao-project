package com.nowcoder.dao;

import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2017/5/11.
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = " from_id , to_id , content , created_date , has_read , conversation_id ";
    String SELECT_FIELDS = "id , " + INSERT_FIELDS;

    @Insert({" insert into " , TABLE_NAME  , "(" , INSERT_FIELDS , ")" ,
            "values(#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select conversation_id,from_id,to_id,count(id) as id from( select * from message where to_id=#{userId} order by id desc) tt group by conversation_id, from_id,to_id;"})
    List<Message> getConversationList(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);


    @Select({"select count(id)  from " ,TABLE_NAME , " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}" })
    int getConversationUnReadCount(@Param("userId") int userId , @Param("conversationId") String conversationId);

    @Select({"select count(id) from " , TABLE_NAME , "where has_read=0 and to_id=#{userId}"})
    int getConversationTotalCount(@Param("userId") int userId , @Param("conversationId") String conversationId);


    @Select({"select " , SELECT_FIELDS , " from " , TABLE_NAME , "where conversation_id=#{conversationId} order by id desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId , @Param("offset") int offset , @Param("limit") int limit);

    @Select({"select min(created_date) as created_date from "+ TABLE_NAME +" where conversation_id=#{conversationId}"})
    Date getConversationDate(@Param("conversationId")  String conversationId);

}
