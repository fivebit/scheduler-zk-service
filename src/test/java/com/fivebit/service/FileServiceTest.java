package com.fivebit.service;

import com.google.common.collect.Maps;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Created by fivebit on 2017/6/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class FileServiceTest extends TestCase {
    @Autowired
    FileService fileService;

    @Test
    public void testReadFile(){
        String file_name = "/Users/fivebit/tmp/test_2.job";
        String content = fileService.readFile(file_name);

    }

    @Test
    public void testReplaceFile(){

        String file_in = "/Users/fivebit/tmp/test_2.job";
        String file_out = "/Users/fivebit/tmp/test_tmp.job";
        Map<String,String> config = Maps.newHashMap();
        config.put("name","baidu");
        config.put("qiong","123");
        fileService.replaceJobTemp(file_in,file_out,config);
    }
    @Test
    public void testMakeDir(){
        String file_dir = "/Users/fivebit/tmp/qiong";
        fileService.makeDir(file_dir);
    }
    @Test
    public void testBuildZip(){
        Map<String,String> config = Maps.newHashMap();
        config.put("name","baidu");
        config.put("qiong","123");
        fileService.buildNewJobZipFiles("/Users/fivebit/tmp/qiong","/Users/fivebit/tmp/qiong_1",config);
    }
}
