package com.fivebit.dao;

import com.fivebit.entity.AkManagerInfoEntity;
import org.apache.ibatis.annotations.Param;

/**
 * Created by fivebit on 2017/6/19.
 */
public interface AkManagerInfosDao {
    public Integer createAkMamangerInfo(AkManagerInfoEntity akManagerInfoEntity);
    public AkManagerInfoEntity getAkManagerInfoByJobId(@Param("job_id") Integer job_id);
    public Boolean updateStatusByJobId(@Param("job_id") Integer job_id,@Param("status") String status,@Param("message") String message,
                                       @Param("update_time") String update_time);
    public Boolean updateExecIdByJobId(@Param("job_id") Integer job_id,@Param("exec_ids") String exec_ids,
                                       @Param("update_time") String update_time);
    public Boolean updateExecStatusByJobId(@Param("job_id") Integer job_id,@Param("status") String status,
                                           @Param("execution_status") String execution_status,
                                       @Param("update_time") String update_time);
}
