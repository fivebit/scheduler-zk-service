package com.fivebit.common;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by fivebit on 2017/6/20.
 */
@Service("zipC")
public class ZipCompressor {

    static final int BUFFER = 8192;

    public ZipCompressor(){

    }

    public void compress(String srcPathName,String destZipName) {
        File file = new File(srcPathName);
        File zipFile = new File(destZipName);
        if (!file.exists())
            throw new RuntimeException(srcPathName + "不存在！");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            String basedir = "";
            compress(file, out, basedir);
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void compress(File file, ZipOutputStream out, String basedir) {
        if (file.isDirectory()) {
            this.compressDirectory(file, out, basedir);
        } else {
            this.compressFile(file, out, basedir);
        }
    }

    /** 压缩一个目录 */
    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists())
            return;

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }

    /** 压缩一个文件 */
    private void compressFile(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public boolean zipComp(String[] srcFiles, String desFile) {
        boolean isSuccessful = false;

        String[] fileNames = new String[srcFiles.length-1];
        for (int i = 0; i < srcFiles.length-1; i++) {
            fileNames[i] = parse(srcFiles[i]);
        }

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(desFile));
            ZipOutputStream zos = new ZipOutputStream(bos);
            String entryName = null;

            for (int i = 0; i < fileNames.length; i++) {
                entryName = fileNames[i];

                // 创建Zip条目
                ZipEntry entry = new ZipEntry(entryName);
                zos.putNextEntry(entry);

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFiles[i]));

                byte[] b = new byte[1024];

                while (bis.read(b, 0, 1024) != -1) {
                    zos.write(b, 0, 1024);
                }
                bis.close();
                zos.closeEntry();
            }

            zos.flush();
            zos.close();
            isSuccessful = true;
        } catch (IOException e) {
        }

        return isSuccessful;
    }


    // 解析文件名
    private String parse(String srcFile) {
        int location = srcFile.lastIndexOf("/");
        String fileName = srcFile.substring(location + 1);
        return fileName;
    }
}
