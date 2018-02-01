package com.fivebit.entity;

/**
 * Created by fivebit on 2017/6/21.
 */
public class JobConfigurationEntity {
    private Integer id;
    private String jobType;
    private String sourceName;
    private String featureType;
    private String modelType;
    private String varName;
    private String varValue;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getVarValue() {
        return varValue;
    }

    public void setVarValue(String varValue) {
        this.varValue = varValue;
    }

    @Override
    public String toString() {
        return "JobConfigurationEntity{" +
                "id=" + id +
                ", jobType='" + jobType + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", featureType='" + featureType + '\'' +
                ", modelType='" + modelType + '\'' +
                ", varName='" + varName + '\'' +
                ", varValue='" + varValue + '\'' +
                '}';
    }
}
