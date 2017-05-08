package com.nowcoder.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;

/**
 * Created by dell on 2017/5/6.
 */
public class ToutiaoUtil {
    private static final Logger logger = LoggerFactory.getLogger(ToutiaoUtil.class);
    public static String TOUTIAO_DOMAIN = "http://127.0.0.1:8080/";
    public static String IMAGE_DIR = "D:\\java-project\\upload\\";
    public static String[] IMAEG_FILE_EXTD = new String[]{"png" , "bmp" , "jpg" , "jpeg"};

    public static boolean isFileAllowed(String fileName){
        for(String ext : IMAEG_FILE_EXTD){
            if(ext.equals(fileName)){
                return true;
            }
        }
        return false;
    }

    public static String getJSONString(int code) {
        JSONObject json = new JSONObject();
        json.put("code" , code);
        return json.toJSONString();
    }

    public static String getJSONString(int code , String msg){
        JSONObject json = new JSONObject();
        json.put("code" , code);
        json.put("msg" , msg);
        return json.toJSONString();
    }

    public static String getJsongString(int code,Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code" , code);
        for(Map.Entry<String,Object> entry : map.entrySet()){
            json.put(entry.getKey(),entry.getValue());
        }
        return json.toJSONString();
    }

    public static String MD5(String key){
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try{
            byte[] btInput = key.getBytes();
            //获得Md5加密对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用制定的字节加密
            mdInst.update(btInput);
            //获取密文
            byte[] md = mdInst.digest();
            //把密文转换为16进制的字符串形式
            int j = md.length;
            char[] str = new char[j*2];
            int k=0;
            for(int i=0;i<j;++i){
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }catch (Exception e){
            logger.error("生成MD5失败" , e);
            return null;
        }
    }
}
