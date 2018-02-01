package com.fivebit.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fivebit.common.Jdate;
import com.fivebit.common.JhttpClient;
import com.fivebit.common.Jlog;
import com.fivebit.common.Slog;
import com.fivebit.entity.AKExecutionEntity;
import com.fivebit.errorhandling.AppException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by fivebit on 2017/6/15.
 * azkaban
 */
//@Service("akService")
public class AkService {
    private String ak_host ;
    private String user_name ;
    private String password;
    private String session_id = null;
    private String ak_manager_host;
    private String ak_executor_host;
    private String base_day = "";
    @Autowired
    private Slog slog;
    public AkService(String ak_host,String user_name,String password){
        this.ak_host = ak_host;
        this.user_name = user_name;
        this.password = password;
        this.ak_manager_host = ak_host+"/manager";
        this.ak_executor_host = ak_host+"/executor";
    }
    public AkService(){
        myinit();
    }
    public void myinit(){
        this.ak_host = "https://192.168.1.2:8444";
        this.user_name = "azkaban";
        this.password = "azkaban";
        this.ak_manager_host = this.ak_host+"/manager";
        this.ak_executor_host = this.ak_host+"/executor";
    }
    public String getSessionId() throws AppException {
        if(this.base_day.equals(Jdate.getNowHourStr()) == false){
            this.session_id = null;
        }
        if( this.session_id == null ) {
            Map<String, String> params = Maps.newHashMap();
            params.put("action", "login");
            params.put("username", this.user_name);
            params.put("password", this.password);
            String url = this.ak_host;
            JSONObject ret = JhttpClient.httpsAjaxPost(url, params);
            if (ret.containsKey("status") == true && ret.get("status").equals("success") == true) {
                this.session_id = ret.get("session.id").toString();
            } else {
                Jlog.error("get session id error:" + ret.toJSONString());
                throw new AppException("0","get sesson id error");
            }
            this.base_day = Jdate.getNowHourStr();
        }
        return this.session_id;
    }
    public Boolean createPorjcet(String name,String description) throws AppException {
        slog.info("create project begin :"+name+" desc:"+description);
        String session = this.getSessionId();
        Map<String, String> params = Maps.newHashMap();
        params.put("action", "create");
        params.put("session.id",session );
        params.put("name", name);
        params.put("description", description);
        String url = this.ak_manager_host;
        JSONObject ret = JhttpClient.httpsAjaxPost(url, params);
        if (ret.containsKey("status") == true && ret.get("status").equals("success") == true) {
            return true;
        }else{
            slog.error("create project error:name:" +name+" ret:"+ ret.toJSONString());
            throw new AppException("0","create project  error");
        }
    }
    public void delPorjcet(String project) throws Exception {
        String session = this.getSessionId();
        Map<String, String> params = Maps.newHashMap();
        params.put("delete", "true");
        params.put("session.id",session );
        params.put("project", project);
        String url = this.ak_manager_host;
        Jlog.info("url"+url);
        JhttpClient.httpsAjaxDel(url, params);
    }
    public Boolean uploadProjectZip(String project,String file) throws Exception {
        String session = this.getSessionId();
        Map<String, String> params = Maps.newHashMap();
        params.put("ajax", "upload");
        params.put("session.id",session );
        params.put("project", project);
        String url = this.ak_manager_host;
        JhttpClient.httpsUploadFile(url,params,file);
        return true;
    }
    public List<String> fetchFlowsByProject(String project) throws Exception {
        String session = this.getSessionId();
        Map<String, String> params = Maps.newHashMap();
        params.put("ajax", "fetchprojectflows");
        params.put("session.id",session );
        params.put("project", project);
        String url = this.ak_manager_host;
        JSONObject ret = JhttpClient.httpsAjaxGet(url, params);
        List<String> flows = Lists.newArrayList();
        if(ret != null){
            JSONArray json_flows = ret.getJSONArray("flows");
            if(json_flows!= null && json_flows.size() >0){
                for(int i=0;i<json_flows.size();i++){
                    flows.add(((JSONObject)json_flows.get(i)).getString("flowId"));
                }
            }
        }
        slog.info("get flows from project:"+project+" flows:"+flows);
        return flows;
    }
    public Map<String,JSONArray> fetchJobsByFlowAndProject(String project, String flow) throws Exception {
        String session = this.getSessionId();
        Map<String, String> params = Maps.newHashMap();
        params.put("ajax", "fetchflowgraph");
        params.put("session.id",session );
        params.put("project", project);
        params.put("flow", flow);
        String url = this.ak_manager_host;
        JSONObject ret = JhttpClient.httpsAjaxGet(url, params);
        Map<String,JSONArray> jobs= Maps.newHashMap();
        if(ret != null){
            JSONArray json_flows = ret.getJSONArray("nodes");
            jobs.put(flow,json_flows);
        }
        slog.info("get flow jobs from flow:"+flow+" jobs:"+jobs);
        return jobs;
    }
    public List<AKExecutionEntity> fetchAllExecutionsByFlow(String project,String flow)throws Exception {
        String session = this.getSessionId();
        Map<String, String> params = Maps.newHashMap();
        params.put("ajax", "fetchFlowExecutions");
        params.put("session.id",session );
        params.put("project", project);
        params.put("flow", flow);
        params.put("start","0");
        params.put("length","100");
        String url = this.ak_manager_host;
        JSONObject ret = JhttpClient.httpsAjaxGet(url, params);
        List<AKExecutionEntity> exec_status = Lists.newArrayList();
        if(ret != null){
            if(ret.getInteger("total") > 0 && ret.getInteger("length") > 0){
                JSONArray executions = ret.getJSONArray("executions");
                for(int i=0;i<executions.size();i++){
                    //ugly
                    JSONObject item = executions.getJSONObject(i);
                    AKExecutionEntity akExecutionEntity = new AKExecutionEntity();
                    akExecutionEntity.setStartTime(item.getLong("startTime"));
                    akExecutionEntity.setSubmitTime(item.getLong("submitTime"));
                    akExecutionEntity.setStatus(item.getString("status"));
                    akExecutionEntity.setSubmitUser(item.getString("submitUser"));
                    akExecutionEntity.setExecId(item.getInteger("execId"));
                    akExecutionEntity.setProjectId(item.getInteger("projectId"));
                    akExecutionEntity.setEndTime(item.getLong("endTime"));
                    akExecutionEntity.setFlowId(item.getString("flowId"));
                    exec_status.add(akExecutionEntity);
                }
            }
        }
        Jlog.debug("get running execution:"+exec_status);
        return exec_status;
    }

    public List<AKExecutionEntity> fetchRunningExecutionsByFlow(String project, String flow)throws Exception {
        String session = this.getSessionId();
        Map<String, String> params = Maps.newHashMap();
        params.put("ajax", "getRunning");
        params.put("session.id",session );
        params.put("project", project);
        params.put("flow", flow);
        String url = this.ak_executor_host;
        JSONObject ret = JhttpClient.httpsAjaxGet(url, params);
        List<AKExecutionEntity> exec_status = Lists.newArrayList();
        if(ret != null){
            if(ret.getInteger("total") > 0 && ret.getInteger("length") > 0){
                JSONArray executions = ret.getJSONArray("executions");
                for(int i=0;i<executions.size();i++){
                    //ugly
                    JSONObject item = executions.getJSONObject(i);
                    AKExecutionEntity akExecutionEntity = new AKExecutionEntity();
                    akExecutionEntity.setStartTime(item.getLong("startTime"));
                    akExecutionEntity.setSubmitTime(item.getLong("submitTime"));
                    akExecutionEntity.setStatus(item.getString("status"));
                    akExecutionEntity.setSubmitUser(item.getString("submitUser"));
                    akExecutionEntity.setExecId(item.getInteger("execId"));
                    akExecutionEntity.setProjectId(item.getInteger("projectId"));
                    akExecutionEntity.setEndTime(item.getLong("endTime"));
                    akExecutionEntity.setFlowId(item.getString("flowId"));
                    exec_status.add(akExecutionEntity);
                }
            }
        }
        Jlog.debug("get running execution:"+exec_status);
        return exec_status;
    }

    public Integer executeFlow(String project,String flow)throws Exception {
        String session = this.getSessionId();
        Map<String, String> params = Maps.newHashMap();
        params.put("ajax", "executeFlow");
        params.put("session.id",session );
        params.put("project", project);
        params.put("flow", flow);
        String url = this.ak_executor_host;
        JSONObject ret = JhttpClient.httpsAjaxGet(url, params);
        Integer execid = 0;
        if(ret != null && ret.containsKey("execid") == true){
            execid = ret.getInteger("execid");
        }
        slog.info("execute flow:"+flow+" and execid:"+execid);
        return execid;
    }

    public JSONObject cancleExecute(Integer execid)throws Exception {
        String session = this.getSessionId();
        Map<String, String> params = Maps.newHashMap();
        params.put("ajax", "cancelFlow");
        params.put("session.id",session );
        params.put("execid", execid.toString());
        String url = this.ak_executor_host;
        JSONObject ret = JhttpClient.httpsAjaxGet(url, params);
        Jlog.info("cancle job"+ret.toJSONString());
        return null;
    }
}
