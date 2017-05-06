package com.nowcoder.dao;

import com.nowcoder.model.News;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by dell on 2017/5/3.
 */
@Mapper
public interface NewsDAO {
    String TABLE_NAME = "news";
    String INSERT_FIELDS = " title , link , image , like_count , comment_count , user_id , created_date ";
    String SELECT_FIELDS = " id , " + INSERT_FIELDS;

    @Insert({"insert into " , TABLE_NAME  , "(" , INSERT_FIELDS , ")" ,
                "values(#{title},#{link},#{image},#{likeCount},#{commentCount},#{userId},#{createdDate})"})
    int addNews(News news);

    @Select({"select * from " , TABLE_NAME , " order by created_date desc limit #{offset},#{limit}"})
    List<News> selectByUserIdAndOffset(@Param("userId") int userId , @Param("offset") int offset , @Param("limit") int limit);

}
