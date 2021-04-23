package cn.cym.codetoolkit.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * IO工具类
 *
 * @author wuyy
 * @date 2018年1月2日下午4:47:36
 */
public class IoUtils {

    private static final Logger logger = LoggerFactory.getLogger(IoUtils.class);
    /**
     * UTF-8 编码
     */
    public static final String ENCODING_UTF8 = "UTF-8";

    /**
     * 读取文本文件
     *
     * @param file
     * @return
     */
    public static String readTxtFile(File file) throws Exception {
        return readTxtFile(file, IoUtils.ENCODING_UTF8);
    }

    /**
     * 读取文本文件
     *
     * @param file
     * @param encoding
     * @return
     */
    public static String readTxtFile(File file, String encoding) throws Exception {
        if (!file.exists()) {
            logger.info("文件不存在，文件：{}", file.getAbsolutePath());
            return StringUtils.EMPTY;
        }
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

            String s = null;
            int idx = 0;
            //使用readLine方法，一次读一行
            while ((s = br.readLine()) != null) {
                if (idx > 0) {
                    result.append("\n");
                }
                result.append(s);
                idx++;
            }
            br.close();
        } catch (Exception e) {
            //logger.error("读取文本文件异常", e);
            throw e;
        }
        return result.toString();
    }

    /**
     * 读json文件反序列化为对象
     *
     * @param clazz
     * @param file
     * @param encoding
     * @return
     */
    public static <T> T readJoinFile(Class<T> clazz, File file, String encoding) throws Exception {
        String content = readTxtFile(file, encoding);
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        content = content.replaceAll("(?<![\\\\])([\r\n\t]+)", "");
        return JsonUtils.parseObject(clazz, content);
    }

    public static void appendTxtFile(File file, String txtContent) throws Exception {
        appendTxtFile(file, txtContent, ENCODING_UTF8);
    }

    /**
     * 保存文件文件
     *
     * @param file
     * @param txtContent
     */
    public static void writeTxtFile(File file, String txtContent) throws Exception {
        writeTxtFile(file, txtContent, ENCODING_UTF8);
    }

    /**
     * 保存文件文件
     *
     * @param file
     * @param txtContent
     * @param encoding
     */
    public static void writeTxtFile(File file, String txtContent, String encoding) throws Exception {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file, false), encoding);
            out.write(txtContent);
            out.flush();
        } catch (FileNotFoundException e) {
            //logger.error("保存文本文件异常", e);
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 保存文件文件
     *
     * @param file
     * @param txtContent
     * @param encoding
     */
    public static void appendTxtFile(File file, String txtContent, String encoding) throws Exception {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file, false), encoding);
            out.append(txtContent);
            out.flush();
        } catch (FileNotFoundException e) {
            //logger.error("保存文本文件异常", e);
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String[] args) {
    }
}
