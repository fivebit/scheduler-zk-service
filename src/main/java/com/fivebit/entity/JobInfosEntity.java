package com.fivebit.entity;

import com.alibaba.fastjson.JSONObject;
import com.fivebit.common.AppConstants;
import com.fivebit.common.Jdate;
import com.fivebit.controller.InterfaceBean.JobInfoIn;

import java.io.Serializable;

/**
 * Created by fivebit on 2017/6/16.
 */
public class JobInfosEntity implements Serializable {

    private static final long serialVersionUID = -8039686696076337053L;
    private Integer jobId;
    private String userId;
    private String userGroup = "";
    private String jobName;
    private String project; //标示job来源
    private String jobType;
    private String params;
    private String startTime;
    private String finishedTime;
    private Integer rerunCount = 0;
    private String createTime;
    private String updateTime;
    private String status ;
    private String errorMessage ;
    public void initByJobInfoIn(JobInfoIn jobInfoIn){
        this.userId = jobInfoIn.getUser_id();
        this.jobName = jobInfoIn.getJob_name().trim().replaceAll(" ","_");
        String begin_time = jobInfoIn.getBegin_time();
        String end_time = jobInfoIn.getEnd_time();
        String products = jobInfoIn.getProduct_ids();
        JSONObject j_params = new JSONObject();
        j_params.put("begin_time",begin_time);
        j_params.put("end_time",end_time);
        j_params.put("products",products);
        this.jobType = jobInfoIn.getJob_type();
        this.project = jobInfoIn.getProject();
        this.params = j_params.toJSONString();
        this.status = AppConstants.JOB_STATUS_INIT;
        this.createTime = Jdate.getNowStrTime();
        this.updateTime = Jdate.getNowStrTime();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(String finishedTime) {
        this.finishedTime = finishedTime;
    }

    public Integer getRerunCount() {
        return rerunCount;
    }

    public void setRerunCount(Integer rerunCount) {
        this.rerunCount = rerunCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "JobInfosEntity{" +
                "jobId=" + jobId +
                ", userId='" + userId + '\'' +
                ", userGroup='" + userGroup + '\'' +
                ", jobName='" + jobName + '\'' +
                ", project='" + project + '\'' +
                ", jobType='" + jobType + '\'' +
                ", params='" + params + '\'' +
                ", startTime='" + startTime + '\'' +
                ", finishedTime='" + finishedTime + '\'' +
                ", rerunCount=" + rerunCount +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", status='" + status + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}

