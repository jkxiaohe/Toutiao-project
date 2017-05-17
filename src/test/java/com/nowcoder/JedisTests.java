package com.nowcoder;

import com.nowcoder.model.User;
import com.nowcoder.util.JedisAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by dell on 2017/5/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class JedisTests {

    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void testJedis(){
        jedisAdapter.set("hello"  , "world");
        Assert.assertEquals("world" , jedisAdapter.get("hello"));
    }

    public void testObject(){
        User user = new User();
        user.setHeadUrl("http://www.baidu.com");
        user.setName("user1");
        user.setPassword("abc");
        user.setSalt("def");
        jedisAdapter.setObject("user11" , user);

        //从redis中取出该值
        User u = jedisAdapter.getObject("user11" , User.class);
        System.out.println(ToStringBuilder.reflectionToString(u));
    }

}
