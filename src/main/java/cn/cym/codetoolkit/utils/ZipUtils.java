package cn.cym.codetoolkit.utils;

import cn.cym.codetoolkit.log.LogUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    /** 压缩单个文件*/
    public static void zipFile(String filepath ,String zippath) {
        try {
            File file = new File(filepath);
            File zipFile = new File(zippath);
            InputStream input = new FileInputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            int temp = 0;
            while((temp = input.read()) != -1){
                zipOut.write(temp);
            }
            input.close();
            zipOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 一次性压缩多个文件，文件存放至一个文件夹中*/
    public static void zipMultiFile(String filepath ,String zippath, String fileSeparator) {
        //
        File src = new File(filepath);

        if (!src.exists()) {
            throw new RuntimeException(filepath + "不存在");
        }
        File zipFile = new File(zippath);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            String baseDir = "";
            compressbyType(src, zos, baseDir, fileSeparator);
            zos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按照原路径的类型就行压缩。文件路径直接把文件压缩，
     * @param src
     * @param zos
     * @param baseDir
     */
    private static void compressbyType(File src, ZipOutputStream zos, String baseDir, String fileSeparator) {
        if (!src.exists())
            return;
        System.out.println("压缩路径" + baseDir + src.getName());
        //判断文件是否是文件，如果是文件调用compressFile方法,如果是路径，则调用compressDir方法；
        if (src.isFile()) {
            //src是文件，调用此方法
            compressFile(src, zos, baseDir);

        } else if (src.isDirectory()) {
            //src是文件夹，调用此方法
            compressDir(src, zos, baseDir, fileSeparator);

        }

    }

    /**
     * 压缩文件
     */
    private static void compressFile(File file, ZipOutputStream zos,String baseDir) {
        if (!file.exists())
            return;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(baseDir + file.getName());
            zos.putNextEntry(entry);
            int count;
            byte[] buf = new byte[1024];
            while ((count = bis.read(buf)) != -1) {
                zos.write(buf, 0, count);
            }
            bis.close();

        } catch (Exception e) {
            // TODO: handle exception

        }
    }

    /**
     * 压缩文件夹
     */
    private static void compressDir(File dir, ZipOutputStream zos, String baseDir, String fileSeparator) {
        if (!dir.exists())
            return;
        File[] files = dir.listFiles();
        if(files.length == 0){
            try {
                zos.putNextEntry(new ZipEntry(baseDir + dir.getName() + fileSeparator));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (File file : files) {
            compressbyType(file, zos, baseDir + dir.getName() + fileSeparator, fileSeparator);
        }
    }

    public static void unzip(String zippath , String outzippath){
        InputStream input = null;
        OutputStream output = null;
        FileInputStream fileInputStream = null;
        ZipInputStream zipInput = null;
        try {
            File file = new File(zippath);
            File outFile = null;
            ZipFile zipFile = new ZipFile(file);
            fileInputStream = new FileInputStream(file);
            zipInput = new ZipInputStream(fileInputStream);
            ZipEntry entry = null;
            while((entry = zipInput.getNextEntry()) != null){
                LogUtils.println("解压缩文件:" + entry.getName() + "");
                outFile = new File(outzippath, entry.getName());
                LogUtils.println("解压缩文件路径:" + outFile.getCanonicalPath() + "");

                if (entry.isDirectory()) {
                    if (!outFile.exists()) {
                        outFile.mkdir();
                    }
                    continue ;
                }

                if(!outFile.getParentFile().exists()){
                    outFile.getParentFile().mkdir();
                }
                if(!outFile.exists()){
                    outFile.createNewFile();
                }
                input = zipFile.getInputStream(entry);
                output = new FileOutputStream(outFile);
                int temp = 0;
                while((temp = input.read()) != -1){
                    output.write(temp);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (zipInput != null) {
                try {
                    zipInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            String path = "C:\\Users\\chenyouming\\Desktop\\client\\template.zip";
//            new Zip().unZip(new File(path));
//            ZipUtils.zipContraMultiFile(path, new File(path).getParentFile().getCanonicalPath());
            ZipUtils.zipMultiFile("C:\\Users\\chenyouming\\Desktop\\client\\template", path, "\\");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
