package com.fivebit.controller.InterfaceBean;

import javax.ws.rs.QueryParam;

/**
 * Created by fivebit on 2017/6/18.
 * 任务列表检索的条件
 */
public class JobSearchIn {
    @QueryParam("user_id")
    private String user_id;
    @QueryParam("job_name")
    private String job_name;
    @QueryParam("project")
    private String project;
    @QueryParam("job_type")
    private String job_type;
    @QueryParam("job_status")
    private String job_status ="all";
    @QueryParam("page")
    private Integer page = 0;
    @QueryParam("page_size")
    private Integer page_size = 10;
    @QueryParam("order")
    private String order;
    @QueryParam("orderby")
    private String orderby;

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getJob_type() {
        return job_type;
    }

    public void setJob_type(String job_type) {
        this.job_type = job_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getJob_status() {
        return job_status;
    }

    public void setJob_status(String job_status) {
        this.job_status = job_status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPage_size() {
        return page_size;
    }

    public void setPage_size(Integer page_size) {
        this.page_size = page_size;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    @Override
    public String toString() {
        return "JobSearchIn{" +
                "user_id='" + user_id + '\'' +
                ", job_name='" + job_name + '\'' +
                ", job_status='" + job_status + '\'' +
                ", page='" + page + '\'' +
                ", page_size='" + page_size + '\'' +
                ", order='" + order + '\'' +
                ", orderby='" + orderby + '\'' +
                '}';
    }
}
