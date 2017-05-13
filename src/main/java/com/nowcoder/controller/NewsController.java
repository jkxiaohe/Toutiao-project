package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.QiniuService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2017/5/7.
 */
@Controller
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
    @Autowired
    NewsService newsService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QiniuService qiniuService;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/uploadImage/"} , method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){
        try{
//            String fileUrl = newsService.saveImage(file);
            String fileUrl =  qiniuService.saveImage(file);
            if(fileUrl == null){
                return ToutiaoUtil.getJSONString(1 , "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        }catch (Exception e){
            logger.error("上传图片错误" + e.getMessage());
            return ToutiaoUtil.getJSONString(1,"上传失败");
        }
    }

    //图片展示
    @RequestMapping(path = {"/image"} , method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response){
        try{
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName)) ,
                    response.getOutputStream());
        }catch (Exception e){
            logger.error("读取图片错误" + e.getMessage());
        }
    }

    //分享功能，添加资讯消息
    @RequestMapping(path = {"/user/addNews/"} , method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@Param("image") String image,
                          @Param("title") String title,
                          @Param("link") String link){
        try{
            News news = new News();
            news.setImage(image);
            news.setTitle(title);
            news.setLink(link);
            news.setCreatedDate(new Date());
            news.setLikeCount(0);
            if(hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                //如果当前没有登陆的用户，设置为匿名用户发帖
                news.setUserId(2);
            }
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        }catch(Exception e ){
            logger.error("添加资讯失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1 , "添加资讯失败");
        }
    }

    //添加评论
    @RequestMapping(path = {"/addComment"} , method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try{
            //新建一个comment对象，将用户的信息封装到comment 中，再添加如db
            Comment comment = new Comment();
            //评论的用户为当前登录的用户id
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);
            //当添加用户评论成功后，立即更新当前评论的数量
            int count = commentService.getCommentCount(comment.getEntityId() , comment.getEntityType());
            newsService.updateCommentCount(count , newsId);
        }catch (Exception e ){
            logger.error("提交评论错误" + e.getMessage());
        }
        //不管有没有更新成功，都将当前资讯页面进行刷新
        return "redirect:/news/" + newsId;
    }

    //获取某条资讯的详细信息
    @RequestMapping(path = {"/news/{newsId}"} , method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId , Model model){
        try{
            News news = newsService.getById(newsId);
            if(news != null){
                //获取这条资讯下的所有评论
                List<Comment> comments = commentService.getCommentsByEntity(newsId , EntityType.ENTITY_NEWS);
                List<ViewObject> commentVOs = new ArrayList<ViewObject>();
                //将每条评论及其评论的用户的信息加入viewObject中
                for(Comment comment : comments){
                    ViewObject commentVO = new ViewObject();
                    commentVO.set("comment" , comment);
                    commentVO.set("user" , userService.getUser(comment.getUserId()));
                    commentVOs.add(commentVO);
                }
                model.addAttribute("comments" , commentVOs);
            }
            model.addAttribute("news" , news );
            model.addAttribute("owner" , userService.getUser(news.getUserId()));
        }catch(Exception e ){
            logger.error("获取资讯明细错误 " + e.getMessage());
        }
        //当信息获取完毕后，跳转回详情页
        return "detail";
    }

}
