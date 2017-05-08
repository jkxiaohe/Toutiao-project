package com.nowcoder.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.nowcoder.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by dell on 2017/5/7.
 */
@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    //...生成上传凭证，然后准备上传
    String accessKey = "_Sn_oO1D3viWWolZeqvOn2MnlNzDOIJNIlCrKCSd";
    String secretKey = "KdAUO67sisYK3z4Mh3uhJD--nuYitqNOZUpratZB";
    String bucket = "toutiao";

    private static String QINIU_IMAGE_DOMAIN = "http://opkcbb1ma.bkt.clouddn.com/";
    Auth auth = Auth.create(accessKey, secretKey);
    String upToken = auth.uploadToken(bucket);

    UploadManager uploadManager = new UploadManager();

    public String saveImage(MultipartFile file) {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf('.');
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }
            //生成一个新的文件名字
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            System.out.println(response.bodyString());
            if (response.isOK() && response.isJson()) {
                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(response.bodyString()).get("key");
            } else {
                logger.error("七牛上传图片异常" + response.bodyString());
                return null;
            }
            //解析上传成功的结果
//            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//            System.out.println(putRet.key);
//            System.out.println(putRet.hash);
        } catch (Exception e) {
            logger.error("七牛异常 : " + e.getMessage());
            return null;
        }
    }
}
