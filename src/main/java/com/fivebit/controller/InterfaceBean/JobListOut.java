package com.fivebit.controller.InterfaceBean;

/**
 * Created by fivebit on 2017/6/20.
 */
public class JobListOut {
    private Integer job_id;
    private String job_name;
    private String job_finished_time = "";
    private String job_start_time = "";
    private String prediction_start_time = "";
    private String prediction_end_time = "";
    private String job_status;

    public Integer getJob_id() {
        return job_id;
    }

    public void setJob_id(Integer job_id) {
        this.job_id = job_id;
    }

    public String getJob_finished_time() {
        return job_finished_time;
    }

    public void setJob_finished_time(String job_finished_time) {
        this.job_finished_time = job_finished_time;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getJob_start_time() {
        return job_start_time;
    }

    public void setJob_start_time(String job_start_time) {
        this.job_start_time = job_start_time;
    }

    public String getPrediction_start_time() {
        return prediction_start_time;
    }

    public void setPrediction_start_time(String prediction_start_time) {
        this.prediction_start_time = prediction_start_time;
    }

    public String getPrediction_end_time() {
        return prediction_end_time;
    }

    public void setPrediction_end_time(String prediction_end_time) {
        this.prediction_end_time = prediction_end_time;
    }

    public String getJob_status() {
        return job_status;
    }

    public void setJob_status(String job_status) {
        this.job_status = job_status;
    }

    @Override
    public String toString() {
        return "JobListOut{" +
                "job_id='" + job_id + '\'' +
                ", job_name='" + job_name + '\'' +
                ", job_finished_time='" + job_finished_time + '\'' +
                ", job_start_time='" + job_start_time + '\'' +
                ", prediction_start_time='" + prediction_start_time + '\'' +
                ", prediction_end_time='" + prediction_end_time + '\'' +
                ", job_status='" + job_status + '\'' +
                '}';
    }
}
