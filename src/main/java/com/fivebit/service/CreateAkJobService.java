package com.fivebit.service;

import com.fivebit.common.Slog;
import com.fivebit.controller.InterfaceBean.JobInfoIn;
import com.fivebit.entity.JobInfosEntity;
import com.fivebit.errorhandling.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created by fivebit on 2017/9/11.
 */
@Service
public class CreateAkJobService {
    private Thread t;
    @Autowired
    JobsService jobsService;

    @Autowired
    Slog slog;

    @Deprecated
    public void createAKJob() {
        slog.info("createAKJob begin");
        while (jobsService.getQueuedJob() != null) {
            slog.info("have queued job");
            if (jobsService.getRunningJob().size() == 0) {
                slog.info("no running job");
                JobInfosEntity jobInfosEntity = jobsService.getQueuedJob();
                JobInfoIn jobInfoIn = new JobInfoIn();
                jobInfoIn.init(jobInfosEntity);
                try {
                    jobsService.createNewJob(jobInfosEntity,jobInfoIn);
                    slog.info("createNewJob finished");
                } catch (AppException e) {
                    e.printStackTrace();
                }
            }
            try {
                t.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    public void start() {
        slog.info("CreateAkJobService start begin");
        t = new Thread() {
            @Override
            public void run() {
                createAKJob();
            }
        };
        t.start();
    }
    public void doCreateAKJob(){
        if(jobsService.getQueuedJob() != null){
            slog.info("have queued job");
            if (jobsService.getRunningJob().size() == 0) {
                JobInfosEntity jobInfosEntity = jobsService.getQueuedJob();
                JobInfoIn jobInfoIn = new JobInfoIn();
                jobInfoIn.init(jobInfosEntity);
                try {
                    jobsService.createNewJob(jobInfosEntity,jobInfoIn);
                    slog.info("createNewJob finished");
                } catch (AppException e) {
                    slog.error("create job error"+e.getMessage());
                }
            }
        }else{
            slog.info("create task ,no queued job");
        }
    }
    @Scheduled(initialDelay=1000,fixedDelay = 1000*60)
    public void doStart(){
        doCreateAKJob();
    }

}
