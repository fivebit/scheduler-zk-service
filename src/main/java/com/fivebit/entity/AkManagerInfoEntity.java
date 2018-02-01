package com.fivebit.entity;

/**
 * Created by fivebit on 2017/6/19.
 */
public class AkManagerInfoEntity {
    private Integer akManagerId;
    private Integer jobId;
    private String projectName;
    private String projectFile;
    private String flowIds;
    private String jobIds;
    private String execIds;
    private String executionStatus = "";
    private String createTime;
    private String updateTime;
    private String status;
    private String message;

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getAkManagerId() {
        return akManagerId;
    }

    public void setAkManagerId(Integer akManagerId) {
        this.akManagerId = akManagerId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectFile() {
        return projectFile;
    }

    public void setProjectFile(String projectFile) {
        this.projectFile = projectFile;
    }

    public String getFlowIds() {
        return flowIds;
    }

    public void setFlowIds(String flowIds) {
        this.flowIds = flowIds;
    }

    public String getJobIds() {
        return jobIds;
    }

    public void setJobIds(String jobIds) {
        this.jobIds = jobIds;
    }

    public String getExecIds() {
        return execIds;
    }

    public void setExecIds(String execIds) {
        this.execIds = execIds;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AkManagerInfoEntity{" +
                "akManagerId=" + akManagerId +
                ", jobId=" + jobId +
                ", projectName='" + projectName + '\'' +
                ", projectFile='" + projectFile + '\'' +
                ", flowIds='" + flowIds + '\'' +
                ", jobIds='" + jobIds + '\'' +
                ", execIds='" + execIds + '\'' +
                ", executionStatus='" + executionStatus + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

}
