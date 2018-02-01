package com.fivebit.service;

import com.fivebit.common.Jdate;
import com.fivebit.common.Jlog;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author ranran
 * @version V1.0
 * @Title:
 * @Package com.fivebit.notice.service
 * @Description:
 * @date 2017/6/6 15:17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class AkServiceTest extends TestCase {

    @Autowired
    private AkService akService;

    @Test
    public void testGetSessionId() throws Exception {
        akService.getSessionId();
    }
    @Test
    public void testCreateProject() throws Exception {
        akService.createPorjcet("test_from_client2","qiongye");
    }
    @Test
    public void testDelProject() throws Exception {
        akService.delPorjcet("qiong");
    }
    @Test
    public void testuploadProject() throws Exception {
        akService.uploadProjectZip("test","/Users/fivebit/tmp/test.zip");
    }
    @Test
    public void testFetchFlowsByProject() throws Exception {
        akService.fetchFlowsByProject("test");
    }
    @Test
    public void testFetchJobsByFlowAndProject() throws Exception {
        akService.fetchJobsByFlowAndProject("test","test");
    }
    @Test
    public void testFetchExecutionsByFlow() throws Exception {
        akService.fetchAllExecutionsByFlow("ksjdf","feature");
    }
    @Test
    public void testFetchRunningExecutionsByFlow() throws Exception {
   //     akService.fetchRunningExecutionsByFlow("test","test");
    }
    @Test
    public void testExecuteFlow() throws Exception {
        akService.executeFlow("test","test");
    }

    @Test
    public void testCancleExecute() throws Exception {
        akService.cancleExecute(1);
    }
}
