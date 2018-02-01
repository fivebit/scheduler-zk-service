package com.fivebit.task;

import com.fivebit.common.Jdate;
import com.fivebit.common.Slog;
import com.fivebit.entity.JobInfosEntity;
import com.fivebit.errorhandling.AppException;
import com.fivebit.service.JobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;

/**
 * Created by fivebit on 2017/6/19.
 */
public class JobStatusSyncTask implements Runnable {
    @Autowired
    private ThreadPoolTaskExecutor threadPool;
    @Autowired
    private Slog slog;
    @Autowired
    JobsService jobsService;
    public JobStatusSyncTask(){
    }
    public JobStatusSyncTask(ThreadPoolTaskExecutor taskExecutor) {
        this.threadPool= taskExecutor;
    }
    @Override
    public void run() {
        slog.info("schedule sync job status begin;");
        List<JobInfosEntity> need_sync_jobs = jobsService.getJobsOfNeedSync();
        if(need_sync_jobs == null || need_sync_jobs.size() == 0){
            slog.info("schedule sync job status end; no job to sync");
            return;
        }
        for(JobInfosEntity jobInfosEntity:need_sync_jobs){
            try {
                Map<String,String> ret = jobsService.syncAKFLowStatus(jobInfosEntity.getJobId());
                slog.info("sync ak status:"+ret);
                if(ret.isEmpty() == false){
                    jobInfosEntity.setStatus(ret.get("status"));
                    jobInfosEntity.setErrorMessage(ret.get("message"));
                    if(ret.containsKey("start_time")) {
                        jobInfosEntity.setStartTime(Jdate.getStrTimeByLong(ret.get("start_time")));
                    }
                    if(ret.containsKey("finished_time") && ret.get("finished_time").equals("0") == false) {
                        jobInfosEntity.setFinishedTime(Jdate.getStrTimeByLong(ret.get("finished_time")));
                    }

                    slog.debug("update job info:"+jobInfosEntity.toString());
                    jobsService.updateJobInfo(jobInfosEntity);
                }
            } catch (AppException e) {
                slog.error("get sync ak flow status error:"+e.getMessage());
            }

        }
    }
}
