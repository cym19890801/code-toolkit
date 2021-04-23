package cn.cym.codetoolkit.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 基于fastjson的json工具类
 * @author wuyy
 * @date 2018年1月3日下午3:44:01
 *
 */
public final class JsonUtils {

    /**
     * 读取txt文件的内容
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String txt2String(String fileName) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
        String s = null;
        while((s = br.readLine())!=null){//使用readLine方法，一次读一行
            result.append(System.lineSeparator()+s);
        }
        br.close();
        return result.toString();
    }

    /**
     * 序列化为字符串
     * @param obj
     * @param prettyFormat
     * @return
     */
    public static final String toString(Object obj, Boolean prettyFormat) {
        //String str=JSON.toJSONString(obj,prettyFormat);
        SerializeConfig config = SerializeConfig.getGlobalInstance();
        
        String str=JSON.toJSONString(obj, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.PrettyFormat, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteMapNullValue);
        
        return str;
    }
    /**
     * 反序列化
     * @param clazz
     * @param jsonString
     * @return
     */
    public static <T> T parseObject(Class<T> clazz,String jsonString) {
        if(StringUtils.isEmpty(jsonString)) {
            return null;
        }
        jsonString=jsonString.replaceAll("(?<![\\\\])([\r\n\t]+)", "");
        return JSON.parseObject(jsonString, clazz);
    }
    public static <T> T parseObject(TypeReference<T> typeRef,String jsonString) {
        if(StringUtils.isEmpty(jsonString)) {
            return null;
        }
        jsonString=jsonString.replaceAll("(?<![\\\\])([\r\n\t]+)", "");
        return JSON.parseObject(jsonString,typeRef);
    }
    
    public static JSONObject parseObject(String jsonString) {
        return JSON.parseObject(jsonString);
    }
}
