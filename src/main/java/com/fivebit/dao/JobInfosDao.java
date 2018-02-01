package com.fivebit.dao;

import com.fivebit.controller.InterfaceBean.JobSearchIn;
import com.fivebit.entity.JobInfosEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by fivebit on 2017/6/16.
 */
public interface JobInfosDao {

    /**
     * 创建一个新的jobs 记录
     * @param job_info
     * @return true/false
     */
    public Integer createJobInfo(JobInfosEntity job_info);
    public JobInfosEntity getJobInfoByJobId(@Param("job_id") Integer job_id);

    public List<JobInfosEntity> getJobInfoByStatus(@Param("status") List<String> status);

    public Boolean updateJobStatus(@Param("job_id") Integer job_id,@Param("status") String status,
                                   @Param("message") String message, @Param("update_time") String update_time);

    public void updateFinishTime(@Param("job_id") Integer job_id);

    public Boolean updateJobReRunCount(@Param("job_id") Integer job_id,@Param("status") String status,
                                   @Param("rerun_count") Integer rerun_count, @Param("update_time") String update_time);

    public List<JobInfosEntity> getRunningJob();
    public JobInfosEntity getQueuedJob();

    /**
     * 按照条件检索。
     * 包括，job status
     *      job name
     *      page
     *      user_id
     *  order 暂不支持
     * @param jobSearchIn
     * @return
     */
    public List<JobInfosEntity> searchJobInfoByCondition(JobSearchIn jobSearchIn);

    /**
     * 更新job 的信息
     * @param job_info
     * @return
     */
    public Boolean updateJobInfo(JobInfosEntity job_info);
}
