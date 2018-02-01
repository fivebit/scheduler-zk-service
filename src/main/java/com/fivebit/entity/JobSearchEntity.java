package com.fivebit.entity;

import com.fivebit.controller.InterfaceBean.JobSearchIn;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by fivebit on 2017/6/19.
 */
public class JobSearchEntity implements Serializable {

    private static final long serialVersionUID = -8039686696076337053L;
    private String userId;
    private String userGroup;
    private String jobName;
    private String jobStatus ="all";
    private Integer page = 0;
    private Integer pageSize = 10;
    private String order;
    private String orderBy;

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String toString() {
        return "JobSearchEntity{" +
                "userId='" + userId + '\'' +
                ", userGroup='" + userGroup + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", order='" + order + '\'' +
                ", orderBy='" + orderBy + '\'' +
                '}';
    }
    public void initByJobSearchIn(JobSearchIn jobSearchIn){
        try {
            BeanUtils.copyProperties(this, jobSearchIn);
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }
}
