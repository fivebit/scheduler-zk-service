package com.fivebit.service;

import com.fivebit.common.Jlog;
import com.fivebit.common.Utils;
import com.fivebit.controller.InterfaceBean.JobInfoIn;
import com.fivebit.controller.InterfaceBean.JobSearchIn;
import com.fivebit.errorhandling.AppException;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static java.lang.Thread.sleep;

/**
 * Created by fivebit on 2017/6/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class JobsServiceTest extends TestCase {
    @Autowired
    private JobsService jobsService;
    @Test
    public void testJobsService() throws Exception {
        JobInfoIn jobInfoIn = new JobInfoIn();
        jobInfoIn.setJob_name("job_test");
        jobInfoIn.setBegin_time("2015-09-04 12:34:44");
        jobInfoIn.setEnd_time("2015-09-04 12:34:44");
        jobInfoIn.setJob_type("Jupiter");
        jobInfoIn.setProduct_ids("1,2,3");
        jobInfoIn.setUser_id("tutuanna");
        jobsService.createNewJob(null,jobInfoIn);
    }
    @Test
    public void testDeleteSparkTask()throws Exception{
        jobsService.createDeleteJob(30);
    }
    @Test
    public void testReRunJob() throws Exception {
        Integer job_id = 120;
        jobsService.rerunJobByJobId(job_id);

    }

    @Test
    public void testDelJob() throws AppException {
        Integer job_id = 12;
        jobsService.delJobByJobId(job_id);
        while (true){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void testGetJobByStatus(){
        //jobsService.getJobsOfNeedSync();
        while (true) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSyncAKFLowStatus() throws AppException {
        jobsService.syncAKFLowStatus(9);
        HashMap a = new HashMap<>();
        a.put("a","b");
        a.get("a");
    }
    @Test
    public void testGetJobList()throws AppException {
        JobSearchIn jobSearchIn = new JobSearchIn();
        jobSearchIn.setUser_id("tutuanna");

        jobSearchIn.setOrderby("end_time");
        jobSearchIn.setPage(0);
        jobSearchIn.setPage_size(9);
        Jlog.info(Utils.getRespons(jobsService.getJobList(jobSearchIn)));
    }


}
