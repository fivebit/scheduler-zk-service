package com.fivebit.dao;

import com.fivebit.entity.JobConfigurationEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by fivebit on 2017/6/21.
 */
public interface JobConfigurationDao {
    public List<JobConfigurationEntity> getJobConfigurationsByJobTypeAndSourceName(@Param("project") String project,
                                                                                   @Param("job_type") String job_type,
                                                                                   @Param("source_name") String source_name);

    public void insertJobConfigurationsToHistoryJobInfo(@Param("job_name") String job_name,
                                                        @Param("project_name") String project_name,
                                                        @Param("job_type") String job_type,
                                                        @Param("source_name") String source_name);
}
