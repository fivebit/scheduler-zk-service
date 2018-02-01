package com.fivebit.controller.InterfaceBean;

import com.alibaba.fastjson.JSONObject;
import com.fivebit.entity.JobInfosEntity;

/**
 * Created by fivebit on 2017/6/16.
 */
public class JobInfoIn {
    private Integer job_id;
    private String user_id;             //用户ID
    private String source_name;         //数据源
    private String job_name;            //job name
    private String project;             //job 来源
    private String job_type = "default";      //job 运行类型
    private String begin_time;      //预测周期的起始时间
    private String end_time;        //预测周期的结束时间
    private String product_ids = "";

    public Integer getJob_id() {
        return job_id;
    }

    public void setJob_id(Integer job_id) {
        this.job_id = job_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getJob_type() {
        return job_type;
    }

    public void setJob_type(String job_type) {
        this.job_type = job_type;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getProduct_ids() {
        return product_ids;
    }

    public void setProduct_ids(String product_ids) {
        this.product_ids = product_ids;
    }

    public void init(JobInfosEntity jobInfosEntity) {
        this.user_id = jobInfosEntity.getUserId();
        this.job_name = (jobInfosEntity.getJobName());
//            获取job对应的数据源
        this.source_name = jobInfosEntity.getJobName().split("_")[0];
        this.project = (jobInfosEntity.getProject());
        this.job_type = (jobInfosEntity.getJobType());
        JSONObject j_params = JSONObject.parseObject(jobInfosEntity.getParams());

        this.begin_time = ((String) j_params.get("begin_time"));
        this.end_time = ((String) j_params.get("end_time"));
        this.product_ids = ((String) j_params.get("products"));
    }

    @Override
    public String toString() {
        return "JobInfoIn{" +
                "job_id=" + job_id +
                ", user_id='" + user_id + '\'' +
                ", source_name='" + source_name + '\'' +
                ", job_name='" + job_name + '\'' +
                ", project='" + project + '\'' +
                ", job_type='" + job_type + '\'' +
                ", begin_time='" + begin_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", product_ids='" + product_ids + '\'' +
                '}';
    }
}
