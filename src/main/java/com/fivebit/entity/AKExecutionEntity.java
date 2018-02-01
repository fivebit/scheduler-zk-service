package com.fivebit.entity;

/**
 * Created by fivebit on 2017/6/20.
 * Fetch Executions of a Flow
 */
public class AKExecutionEntity {
    private Long startTime;
    private String submitUser;
    private String status;
    private Long submitTime;
    private Integer execId;
    private Integer projectId;
    private Long endTime;
    private String flowId;

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public void setSubmitTime(Long submitTime) {
        this.submitTime = submitTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getSubmitTime() {
        return submitTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getExecId() {
        return execId;
    }

    public void setExecId(Integer execId) {
        this.execId = execId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    @Override
    public String toString() {
        return "AKExecutionEntity{" +
                "startTime='" + startTime + '\'' +
                ", submitUser='" + submitUser + '\'' +
                ", status='" + status + '\'' +
                ", submitTime='" + submitTime + '\'' +
                ", execId=" + execId +
                ", projectId=" + projectId +
                ", endTime='" + endTime + '\'' +
                ", flowId='" + flowId + '\'' +
                '}';
    }
}
