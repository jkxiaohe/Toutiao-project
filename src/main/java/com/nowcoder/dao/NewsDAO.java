package com.nowcoder.dao;

import com.nowcoder.model.News;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by dell on 2017/5/3.
 */
@Mapper
public interface NewsDAO {
    String TABLE_NAME = "news";
    String INSERT_FIELDS = " title , link , image , like_count , comment_count , user_id , created_date ";
    String SELECT_FIELDS = " id , " + INSERT_FIELDS;



    List<News> selectByUserIdAndOffset(@Param("userId") int userId , @Param("offset") int offset , @Param("limit") int limit);

    @Select({"select " , SELECT_FIELDS , "from " , TABLE_NAME , "where id=#{id}"})
    News getById(int id);

    @Insert({"insert into " , TABLE_NAME , "(" , INSERT_FIELDS , ")" ,
            "values(#{title},#{link},#{image},#{likeCount},#{commentCount},#{userId},#{createdDate})"})
    int addNews(News news);

    //更新当前资讯的评论数
    @Update({"update " , TABLE_NAME , "set comment_count=#{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("commentCount") int commentCount , @Param("id") int id);

}
