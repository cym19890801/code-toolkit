package cn.cym.codetoolkit.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;

/**
 * 文件工具类
 * @author cym
 * @date 2018/9/3
 */
public class FileUtils {

    /**
     * 根据后缀名扫描目录，检索出匹配的文件集合
     * @date 2017-12-18 23:45:21
     * @param filePool
     * @param scanDirectory
     * @param fileSuffix  为空时，不过滤
     */
    public static void searchFile(final List<File> filePool,File scanDirectory,String fileSuffix) {
        if(filePool==null) {
            throw new IllegalArgumentException("file pool must be not null");
        }
        //是否通过后缀过滤
        Boolean filter=true;
        if(StringUtils.isEmpty(fileSuffix)) {
            filter=false;
        }
        if(scanDirectory==null || !scanDirectory.exists()) {
            return;
        }
        final Boolean filter2=filter;
        final String finalFileSuffix=fileSuffix;
        File[] files = scanDirectory.listFiles(new FileFilter() {
            
            public boolean accept(File file) {
                if(!filter2) {
                    return true;
                }
                if(file.isDirectory()){
                    return true;
                }else if(file.isFile() && file.getAbsolutePath().toLowerCase().endsWith(finalFileSuffix.toLowerCase())) {
                    return true;
                }
                return false;
            }
        });
        for (File file : files) {
            if(file.isFile()) {
                filePool.add(file);
            }else if(file.isDirectory()) {
                searchFile(filePool, file, fileSuffix);
            }
        }
    }
    /**
     * 根据文件过滤器搜索符合条件的文件列表
     * @param filePool
     * @param scanDirectory
     * @param fileFilter
     */
    public static void searchFile(final List<File> filePool,File scanDirectory,FileFilter fileFilter) {
        if(filePool==null) {
            throw new IllegalArgumentException("file pool must be not null");
        }

        if(scanDirectory==null || !scanDirectory.exists()) {
            return;
        }

        File[] files = scanDirectory.listFiles(fileFilter);
        for (File file : files) {
            if(file.isFile()) {
                filePool.add(file);
            }else if(file.isDirectory()) {
                searchFile(filePool, file, fileFilter);
            }
        }
    }
    
    /**
     * 把文件copy到目的文件
     * @param source  源文件，不是文件夹
     * @param target  目标文件，不是文件夹
     * @return
     */
    public static boolean copyFile(File source, File target) {
        FileInputStream inFile = null;
        FileOutputStream outFile = null;
        try {
            inFile = new FileInputStream(source);
            if(!target.getParentFile().exists()) {
                target.getParentFile().mkdirs();
            }
            if(!target.exists()) {
                target.createNewFile();
            }
            outFile = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            int i = 0;
            while ((i = inFile.read(buffer)) != -1) {
                outFile.write(buffer, 0, i);
            }
            inFile.close();
            outFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inFile != null) {
                    inFile.close();
                }
                if (outFile != null) {
                    outFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return true;
    }
    
    /**
     * 返回文件扩展名
     * @date 2017-12-04 23:03:51
     * @param fileName
     * @return
     */
    public static String getExtendName(String fileName){
        int postion=fileName.lastIndexOf(".");
        if(postion>-1){
            return fileName.substring(postion+1);
        }
        return "";
    }
    
    /**
     * 删除目录
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }


}
