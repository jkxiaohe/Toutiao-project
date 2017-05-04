package com.nowcoder;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.dao.UserDao;
import com.nowcoder.model.News;
import com.nowcoder.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

/**
 * Created by dell on 2017/5/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {

    @Autowired
    UserDao userDao;

    @Autowired
    NewsDAO newsDAO;

    @Test
    public void initData(){
        Random random = new Random();
        for(int i=1; i < 11 ; ++i){
            User user =new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png" , random.nextInt(1000)));
            user.setName(String.format("USER%d" , i));
            user.setPassword("");
            user.setSalt("");
            userDao.addUser(user);

            News news = new News();
            news.setCommentCount(i);
            Date date =new Date();
            date.setTime(date.getTime() + 1000*3600*i);
            news.setCreatedDate(date);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png" , random.nextInt(1000)));
            news.setLikeCount(i+1);
            news.setUserId(i+1);
            news.setTitle(String.format("TITLE{%d}" , i));
            news.setLink(String.format("http://www.nowcoder.com/%d.html" , i));
            newsDAO.addNews(news);

            user.setPassword("newpassword");
            userDao.updatePassword(user);
        }

        Assert.assertEquals("newpassword" , userDao.selectById(1).getPassword());
        userDao.deleteById(1);
        Assert.assertNull(userDao.selectById(1));
    }
}
