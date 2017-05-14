package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dell on 2017/5/14.
 */
@Controller
public class LikeController {
    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    NewsService newsService;

    @RequestMapping(path = {"/like"} , method = {RequestMethod.GET , RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newsId") int newsId){
        //获取当前该条资讯的点赞数目
        long likeCount = likeService.like(hostHolder.getUser().getId() , EntityType.ENTITY_NEWS , newsId);
        //更新资讯里的点赞数
        News news = newsService.getById(newsId);
        newsService.updateLikeCount((int)likeCount , newsId);
        return ToutiaoUtil.getJSONString(0 , String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"} , method = {RequestMethod.GET , RequestMethod.POST})
    @ResponseBody
    public String disLike(@Param("newsId") int newsId){
        long likeCount = likeService.disLike(hostHolder.getUser().getId() , EntityType.ENTITY_NEWS , newsId);
        newsService.updateLikeCount( (int)likeCount , newsId);
        return ToutiaoUtil.getJSONString(0 , String.valueOf(likeCount));
    }

}
