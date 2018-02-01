package com.fivebit.service;

import com.fivebit.common.Slog;
import com.fivebit.common.ZipCompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;

/**
 * Created by fivebit on 2017/6/20.
 * 文件操作。包括替换，zip，mv 等
 */
@Service("fileService")
public class FileService {
    @Autowired
    private  Slog slog;
    @Autowired
    private ZipCompressor zipC;


    public Boolean buildNewJobZipFiles(String src_dir,String dest_dir,Map<String,String> config){
        if (!dest_dir.endsWith(File.separator)) {
            dest_dir = dest_dir+ File.separator;
        }
        slog.debug("build new job zip files begin:src_dir:"+src_dir+" dest_dir:"+dest_dir);
        String dest_file_dir = dest_dir+"file/";
        Boolean st = buildNewJobFiles(src_dir,dest_file_dir,config);
        if(st == true){
            zipC.compress(dest_file_dir,dest_dir+"job.zip");
        }
        return true;
    }
    /**
     * 替换模版里的占位符，生成一套新的job文件。
     * @param src_dir
     * @param dest_dir
     * @param config
     * @return
     */
    public Boolean buildNewJobFiles(String src_dir,String dest_dir,Map<String,String> config){
        slog.debug("build new job zip files begin:src_dir:"+src_dir+" dest_dir:"+dest_dir+" config:"+config);
        if (!src_dir.endsWith(File.separator)) {
            src_dir = src_dir+ File.separator;
        }
        if (!dest_dir.endsWith(File.separator)) {
            dest_dir = dest_dir+ File.separator;
        }
        makeDir(dest_dir);
        File src_file = new File(src_dir);
        if(src_file.isDirectory() == false){
            slog.error("build new job files: src_dir not exist:"+src_dir);
            return false;
        }
        String[] children = src_file.list();
        for(int i=0;i<children.length;i++){
            replaceJobTemp(src_dir+children[i],dest_dir+children[i],config);
        }
        return true;
    }
    /**
     * 用模版文件，通过map配置，进行替换。生成另一个文件。
     * @param file_in   输入文件
     * @param file_out  输出文件
     * @param config   配置文件
     * @return
     */
    public Boolean replaceJobTemp(String file_in,String file_out, Map<String,String> config){

        String file_content = readFile(file_in);
        if(file_content.length() > 0 ){
            for(Map.Entry<String,String> item:config.entrySet()){
                String key = item.getKey();
                String value = item.getValue();
                file_content = file_content.replace("#"+key+"#",value);

            }
        }
        writeFile(file_content,file_out);
        return true;
    }


    /**
     * 读取文件内容
     * @param file_name
     * @return
     */
    public String readFile(String file_name){
        File file = new File(file_name);
        BufferedReader reader = null;
        StringBuffer file_content = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                file_content.append(tempString);
                file_content.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            slog.error("read file error:"+e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        slog.debug("file content:file_name:"+file_name+" content:"+file_content.toString());
        return file_content.toString();
    }

    public Boolean writeFile(String file_content,String file_name){

        try{
            BufferedWriter out=new BufferedWriter(new FileWriter(file_name));
            out.write(file_content);
            out.close();
        }catch (IOException ee){
            slog.error("write file error:"+ee.getMessage());
        }
        return true;
    }
    public Boolean makeDir(String file_dir){
        File dir = new File(file_dir);
        if (dir.exists()) {
            deleteDir(dir);
        }
        if (!file_dir.endsWith(File.separator)) {
            file_dir = file_dir+ File.separator;
        }
        if (dir.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }
    public Boolean deleteDir(File dir){
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir,children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
