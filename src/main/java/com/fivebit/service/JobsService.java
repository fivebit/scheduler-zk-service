package com.fivebit.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fivebit.common.AppConstants;
import com.fivebit.common.Jdate;
import com.fivebit.common.Slog;
import com.fivebit.controller.InterfaceBean.JobInfoIn;
import com.fivebit.controller.InterfaceBean.JobListOut;
import com.fivebit.controller.InterfaceBean.JobSearchIn;
import com.fivebit.dao.AkManagerInfosDao;
import com.fivebit.dao.JobConfigurationDao;
import com.fivebit.dao.JobInfosDao;
import com.fivebit.entity.AKExecutionEntity;
import com.fivebit.entity.AkManagerInfoEntity;
import com.fivebit.entity.JobConfigurationEntity;
import com.fivebit.entity.JobInfosEntity;
import com.fivebit.errorhandling.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by fivebit on 2017/6/16.
 */
@Service("jobsService")
public class JobsService {

    @Resource
    private JobInfosDao jobInfosDao;

    @Resource
    private AkService akService;

    @Autowired
    private AkManagerInfosDao akManagerInfosDao;

    @Autowired
    private JobConfigurationDao jobConfigurationDao;

    @Autowired
    private FileService fileService;

    @Autowired
    private Slog slog;

    //本地模版的基本目录
    @Value("#{config['zk_job.base_job_dir']}")
    private String zk_base_job_dir;

    //模版替换之后，存放的基本目录
    @Value("#{config['zk_job.submited_job_dir']}")
    private String zk_submited_job_dir;

    /**
     * 创建一个任务，其中步骤包括：
     * 1，创建一个project
     * 2，获取脚本路径，及启动参数。
     * 3，替换参数，打包成一个zip文件，转移到一个目录，并上传到对应的project。
     * 4，获取该project的flows，及其对应的jobslist。
     * 5，start all the flow。
     * 6，设置状态为new。下次进度循环的时候，会取没有success的任务的状态。
     *
     * @param jobInfoIn
     * @return
     * @throws AppException
     */

    public JobInfosEntity insertJobInfo(JobInfoIn jobInfoIn) throws AppException {
        //校验参数
        checkJobInfoIn(jobInfoIn);
        JobInfosEntity jobInfosEntity = new JobInfosEntity();
        jobInfosEntity.initByJobInfoIn(jobInfoIn);
        jobInfosEntity.setStatus(AppConstants.JOB_STATUS_QUEUED);
        try {
            Integer count = jobInfosDao.createJobInfo(jobInfosEntity);

        } catch (Exception ee) {
            slog.error("create job info error:" + ee.getMessage());
            throw new AppException("0", "create job info error");
        }
        return jobInfosEntity;
    }


    public Integer createNewJob(JobInfosEntity jobInfosEntity,JobInfoIn jobInfoIn) throws AppException {
//        //校验参数
//        checkJobInfoIn(jobInfoIn);
//        JobInfosEntity jobInfosEntity = new JobInfosEntity();
//        jobInfosEntity.initByJobInfoIn(jobInfoIn);
//        Integer id = 0;
//        try {
//            Integer count = jobInfosDao.createJobInfo(jobInfosEntity);
//            if (count == 1) {
//                id = jobInfosEntity.getJobId();
//            }
//        } catch (Exception ee) {
//            slog.error("create job info error:" + ee.getMessage());
//            throw new AppException("0", "create job info error");
//        }
        Integer id = jobInfosEntity.getJobId();
        String ak_project = jobInfosEntity.getProject() + "_" + id + "_" + jobInfosEntity.getJobName();
//        通过project、job_type、source_name(model_type 默认RandomForest)获得job配置信息
        Map<String, String> job_configs = getJobConfigByTypeAndSource(jobInfosEntity.getProject(), jobInfosEntity.getJobType(),
                jobInfoIn.getSource_name(), jobInfosEntity.getJobName());
        slog.info("get job configs:" + job_configs);

        slog.info("insertJobConfigurationsToHistoryJobInfo");
        String temp_file_dir = getAkProjectFileDir(zk_base_job_dir, jobInfoIn.getProject(), jobInfoIn.getJob_type());
        String dest_file_dir = getSubmitedJobDir(zk_submited_job_dir, id);
        String project_file = dest_file_dir + AppConstants.JOB_ZIP_FILE_NAME;
        fileService.buildNewJobZipFiles(temp_file_dir, dest_file_dir, job_configs);
        try {
            akService.createPorjcet(ak_project, ak_project);
            akService.uploadProjectZip(ak_project, project_file);
            List<String> flows_info = akService.fetchFlowsByProject(ak_project);
            JSONArray db_flow_ids = new JSONArray();
            JSONObject db_job_ids = new JSONObject();
            JSONObject db_exec_ids = new JSONObject();
            for (int i = 0; i < flows_info.size(); i++) {
                String flow_id = flows_info.get(i);
                db_flow_ids.add(flow_id);
                Map<String, JSONArray> jobs = akService.fetchJobsByFlowAndProject(ak_project, flow_id);
                db_job_ids.put(flow_id, jobs.get(flow_id));
            }
            for (int i = 0; i < flows_info.size(); i++) {
                String flow_id = flows_info.get(i);
                Integer execid = akService.executeFlow(ak_project, flow_id);
                db_exec_ids.put(flow_id, execid);
            }
            AkManagerInfoEntity akManagerInfoEntity = new AkManagerInfoEntity();
            akManagerInfoEntity.setJobId(id);
            akManagerInfoEntity.setProjectName(ak_project);
            akManagerInfoEntity.setProjectFile(project_file);
            akManagerInfoEntity.setFlowIds(db_flow_ids.toJSONString());
            akManagerInfoEntity.setJobIds(db_job_ids.toJSONString());
            akManagerInfoEntity.setExecIds(db_exec_ids.toJSONString());
            akManagerInfoEntity.setCreateTime(Jdate.getNowStrTime());
            akManagerInfoEntity.setStatus(AppConstants.JOB_STATUS_NEW);
            akManagerInfosDao.createAkMamangerInfo(akManagerInfoEntity);

            jobInfosEntity.setStatus(AppConstants.JOB_STATUS_NEW);
            slog.info("##################################");
            slog.info("jobInfosEntity is " +jobInfosEntity);
            jobInfosDao.updateJobInfo(jobInfosEntity);

//        将job配置信息插入到scheduler-zk-service_history_job_info表
            jobConfigurationDao.insertJobConfigurationsToHistoryJobInfo(jobInfosEntity.getJobName(), jobInfosEntity.getProject(), jobInfosEntity.getJobType(),
                    jobInfoIn.getSource_name());
        } catch (Exception ee) {
            slog.error("create zk project error:" + ee.getMessage());
            throw new AppException("0", "create job error");
        }
        slog.info("create task:" + id);
        return id;
    }

    /**
     * 获取该用户的任务列表。
     *
     * @param params
     * @return
     */
    public JSONObject getJobList(JobSearchIn params) throws AppException {
        JSONObject ret = new JSONObject();
        slog.info("getJobList params is " + params);
        List<JobListOut> ret_list = Lists.newArrayList();
        ObjectMapper om = new ObjectMapper();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (params.getJob_status() == null || params.getJob_status().isEmpty() == true) {
            params.setJob_status("all");
        }
        if (params.getJob_status().equals(AppConstants.JOB_STATUS_SUCCESS) == false
                && params.getJob_status().equals(AppConstants.JOB_STATUS_RUNNING) == false) {
            params.setJob_status("all");

        }
        if (params.getJob_name() == null) {
            params.setJob_name("");
        }
        if (params.getOrderby() == null) {
            params.setOrderby("start_time");
        }
        if (params.getJob_name().isEmpty() == false) {
            params.setJob_name("%" + params.getJob_name() + "%");
        }
        if (params.getProject() == null) {
            throw new AppException("0", "need project");
        }
        if (params.getJob_type() == null) {
            throw new AppException("0", "need job_type");
        }

        if (params.getUser_id() == null) {
            throw new AppException("0", "need user_id");
        }
        if (params.getPage() == null || params.getPage() < 0) {
            params.setPage(0);
        }
        if (params.getPage_size() == null || params.getPage_size() > AppConstants.JOB_LIST_PAGE_SIZE) {
            params.setPage_size(AppConstants.JOB_LIST_PAGE_SIZE);
        }

        if (params.getOrderby().equals("begin_time") || params.getOrderby().equals("end_time")) {
            int page_size = params.getPage_size();
            int page = params.getPage();
            params.setPage_size(0);
            params.setPage(0);
            List<JobInfosEntity> all_jobs = jobInfosDao.searchJobInfoByCondition(params);
            slog.info("all_jobs is" + all_jobs);

            if (all_jobs != null) {
                Collections.sort(all_jobs, new Comparator<JobInfosEntity>() {
                    public int compare(JobInfosEntity j1, JobInfosEntity j2) {
                        JSONObject params1 = (JSONObject) JSONObject.parse(j1.getParams());
                        JSONObject params2 = (JSONObject) JSONObject.parse(j2.getParams());
                        Date date1 = new Date();
                        Date date2 = new Date();
                        try {
                            date1 = sdf.parse((String) params1.get(params.getOrderby()));
                            date2 = sdf.parse((String) params2.get(params.getOrderby()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (date1.before(date2)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });

            }
            slog.info("all job sort is " + all_jobs);
            int all_items = all_jobs.size();
            int n = all_items / page_size;
            List<JobInfosEntity> query_all_jobs = null;
            if ((page + 1) * page_size <= all_items) {
                query_all_jobs = all_jobs.subList(page * page_size, (page + 1) * page_size);
            } else {
                query_all_jobs = all_jobs.subList(page * page_size, all_items);
            }
            for (JobInfosEntity jobInfosEntity : query_all_jobs) {
                JobListOut item = new JobListOut();
                item.setJob_finished_time(jobInfosEntity.getFinishedTime());
                item.setJob_start_time(jobInfosEntity.getStartTime());
                item.setJob_status(jobInfosEntity.getStatus());
                item.setJob_name(jobInfosEntity.getJobName());
                item.setJob_id(jobInfosEntity.getJobId());
                ret_list.add(item);
            }
            ret.put("list", ret_list);
            ret.put("page_size", params.getPage_size());
            ret.put("page", params.getPage());
            ret.put("page_count", n + 1);
            ret.put("all_count", all_items);
            slog.info("get job list by begin_time or end_time is :" + ret);
        } else {
            params.setPage(params.getPage_size() * params.getPage());

            List<JobInfosEntity> part_job_lists = null;
            Integer all_items = 0;
            try {
                part_job_lists = jobInfosDao.searchJobInfoByCondition(params);      //获取部分列表
                if (part_job_lists == null) {
                    all_items = part_job_lists.size();
                    slog.info("part_job_lists.size() is " + part_job_lists.size());
                } else {
                    int page_size = params.getPage_size();
                    params.setPage_size(0);
                    List<JobInfosEntity> all_job_list = jobInfosDao.searchJobInfoByCondition(params);      //获取部分列表
                    if (all_job_list != null) {
                        all_items = all_job_list.size();

                    }
                    params.setPage_size(page_size);
                }

            } catch (Exception ee) {
                slog.error("search job error:" + ee.getMessage());

            }

            if (part_job_lists != null) {
                for (JobInfosEntity jobInfosEntity : part_job_lists) {
                    JobListOut item = new JobListOut();
                    item.setJob_finished_time(jobInfosEntity.getFinishedTime());
                    item.setJob_start_time(jobInfosEntity.getStartTime());
                    item.setJob_status(jobInfosEntity.getStatus());
                    item.setJob_name(jobInfosEntity.getJobName());
                    item.setJob_id(jobInfosEntity.getJobId());
                    ret_list.add(item);
                }
            }
            Integer page_count = all_items > 0 ? (int) Math.ceil((double) all_items / params.getPage_size()) : 1;


            ret.put("list", ret_list);
            ret.put("page_size", params.getPage_size());
            ret.put("page", params.getPage());
            ret.put("page_count", page_count);
            ret.put("all_count", all_items);
            slog.info("get job list:" + ret);
        }
        return ret;
    }

    /**
     * 删除任务
     *
     * @param job_id
     */
    public Boolean delJobByJobId(Integer job_id) throws AppException {
        JobInfosEntity jobInfosEntity = jobInfosDao.getJobInfoByJobId(job_id);
        if (jobInfosEntity == null) {
            slog.error("cant find job by :" + job_id);
            throw new AppException("0", "cant find job");
        }
        //check token match job_id?

        try {
            jobInfosDao.updateJobStatus(job_id, AppConstants.JOB_STATUS_DEL, "del by client", Jdate.getNowStrTime());
            AkManagerInfoEntity akManagerInfoEntity = akManagerInfosDao.getAkManagerInfoByJobId(job_id);
            slog.info("find ak info job_id:" + job_id);
            if (akManagerInfoEntity != null) {
                String exec_ids = akManagerInfoEntity.getExecIds();
                JSONArray flows = JSONArray.parseArray(akManagerInfoEntity.getFlowIds());
                JSONObject json_exec_ids = JSONObject.parseObject(exec_ids);
                if (flows.size() > 0) {
                    for (Object flow_id : flows) {
                        int exec_id = json_exec_ids.getInteger(flow_id.toString());
                        akService.cancleExecute(exec_id);
                    }
                }
                akManagerInfosDao.updateStatusByJobId(job_id, AppConstants.JOB_STATUS_DEL, "del by client", Jdate.getNowStrTime());
            }
        } catch (Exception ee) {
            slog.error("del job error:" + ee.getMessage());
            throw new AppException("0", "del job error");
        }
        return true;
    }

    /**
     * 重跑任务。
     *
     * @param job_id
     * @return
     */
    public Boolean rerunJobByJobId(Integer job_id) throws AppException {
        String update_time = Jdate.getNowStrTime();
        jobInfosDao.updateJobStatus(job_id, AppConstants.JOB_STATUS_RERUN, "reruning", update_time);
//        rerun的时候，将finish_time赋null
        jobInfosDao.updateFinishTime(job_id);
        AkManagerInfoEntity akManagerInfoEntity = akManagerInfosDao.getAkManagerInfoByJobId(job_id);
        slog.info("find ak info job_id:" + job_id + " and " + akManagerInfoEntity.toString());
        try {
            if (akManagerInfoEntity != null) {
                JSONArray flows = JSONArray.parseArray(akManagerInfoEntity.getFlowIds());
                String project = akManagerInfoEntity.getProjectName();
                JSONObject db_exec_ids = new JSONObject();
                if (flows.size() > 0) {
                    for (Object flow_id : flows) {
                        Integer execid = akService.executeFlow(project, flow_id.toString());
                        db_exec_ids.put(flow_id.toString(), execid);
                    }
                }
                akManagerInfosDao.updateStatusByJobId(job_id, AppConstants.JOB_STATUS_RERUN, "rerun by client", Jdate.getNowStrTime());
                akManagerInfosDao.updateExecIdByJobId(job_id, db_exec_ids.toJSONString(), Jdate.getNowStrTime());
            }
        } catch (Exception ee) {
            slog.error("rerun job:" + job_id + " error:" + ee.getMessage());
            throw new AppException("0", "rerun job error");
        }
        return true;
    }

    /**
     * 获取所有用户的需要从ak获取状态的job。
     * 包括：新建的，运行中的，重跑的的任务。
     *
     * @return
     */
    public List<JobInfosEntity> getJobsOfNeedSync() {
        List<String> job_status = Lists.newArrayList();
        job_status.add(AppConstants.JOB_STATUS_NEW);
        job_status.add(AppConstants.JOB_STATUS_RERUN);
        job_status.add(AppConstants.JOB_STATUS_RUNNING);
        List<JobInfosEntity> items = jobInfosDao.getJobInfoByStatus(job_status);
        slog.debug("get jobs need sync size:" + items.size());
        return items;
    }

    public Boolean updateJobStatus(Integer job_id, String status, String message) {
        jobInfosDao.updateJobStatus(job_id, status, message, Jdate.getNowStrTime());
        return true;
    }

    public Boolean updateJobInfo(JobInfosEntity jobInfosEntity) {
        try {
            jobInfosEntity.setUpdateTime(Jdate.getNowStrTime());
            jobInfosDao.updateJobInfo(jobInfosEntity);
        } catch (Exception ee) {
            slog.error("update job info error:" + jobInfosEntity);
        }
        return true;
    }

    /**
     * 通过scheduler-zk-service的job_id,查询ak中的flow对应的exec_id的执行状态
     * 同时更新到ak表中.
     *
     * @param job_id
     * @return job.status/error_message
     */
    public Map<String, String> syncAKFLowStatus(Integer job_id) throws AppException {
        AkManagerInfoEntity akManagerInfoEntity = akManagerInfosDao.getAkManagerInfoByJobId(job_id);
        slog.debug("sync ak info job_id:" + job_id);
        Map<String, String> ret = Maps.newHashMap();
        ret.put("status", AppConstants.JOB_STATUS_FAIL);
        try {
            if (akManagerInfoEntity != null) {
                slog.debug("sync get :" + akManagerInfoEntity.toString());
                JSONArray flows = JSONArray.parseArray(akManagerInfoEntity.getFlowIds());
                String project = akManagerInfoEntity.getProjectName();
                JSONObject db_exec_ids = JSONObject.parseObject(akManagerInfoEntity.getExecIds());
                if (db_exec_ids == null) {
                    slog.error("sync ak flow exec_id empty:job_id:" + job_id);
                    return null;
                }
                String status = AppConstants.JOB_STATUS_RUNNING;
                Integer part_stauts_count = 0;  //成功的flow数目
                Boolean is_runing = false;

                Long startTime = Long.valueOf(0);
                Long endTime = Long.valueOf(0);

                JSONObject db_exec_status = new JSONObject();
                if (flows.size() > 0) {
                    for (Object flow_id : flows) {
                        List<AKExecutionEntity> execution_status_list = akService.fetchAllExecutionsByFlow(project, flow_id.toString());
                        slog.debug("fetch all executions by flow" + execution_status_list.toString());
                        slog.debug("all flow is:" + flows.toJSONString());
                        Integer exec_id = db_exec_ids.getInteger(flow_id.toString());
                        for (AKExecutionEntity item : execution_status_list) {
                            if (item.getExecId().equals(exec_id) == true) {        //SUCCEEDED,RUNNING,FAILED
                                if (item.getStatus().equals("SUCCEEDED") == true) {
                                    part_stauts_count++;
                                } else if (item.getStatus().equals("FAILED") == true) {
                                    status = AppConstants.JOB_STATUS_FAIL;
                                } else if (item.getStatus().equals("KILLED") == true) {
                                    status = AppConstants.JOB_STATUS_KILLED;
                                } else if (item.getStatus().equals("RUNNING") == true) {
                                    is_runing = true;
                                }
                                db_exec_status.put(exec_id.toString(), item.getStatus());
                                startTime = startTime == 0 ? item.getStartTime() : (startTime > item.getStartTime() ? item.getStartTime() : startTime);
                                endTime = endTime == 0 ? item.getEndTime() : (endTime < item.getEndTime() ? item.getEndTime() : endTime);
                            }
                        }

                    }
                    if (part_stauts_count == flows.size()) {
                        status = AppConstants.JOB_STATUS_SUCCESS;
                    }
                    if (is_runing == true || endTime == -1) {  //如果没有成功，也没有失败
                        endTime = Long.valueOf(0);
                    }
                    slog.debug("part_stauts_count:" + part_stauts_count + " flow size:" + flows.size() + " status:" + status);
                } else {
                    status = AppConstants.JOB_STATUS_FAIL;
                }
                slog.debug("update exec status:" + db_exec_status.toJSONString());
                akManagerInfosDao.updateExecStatusByJobId(job_id, status, db_exec_status.toJSONString(), Jdate.getNowStrTime());
                ret.put("status", status);
                ret.put("message", "");
                ret.put("start_time", startTime.toString());
                ret.put("finished_time", endTime.toString());
            }
        } catch (Exception ee) {
            slog.error("rerun job:" + job_id + " error:" + ee.getMessage());
            throw new AppException("0", "sync job error");
        }
        slog.debug("syncAKFLowStatus return:" + ret.toString());
        return ret;
    }

    /**
     * 通过项目／job type／用户数据源 获取对应的job参数列表
     *
     * @param project
     * @param job_type
     * @param source_name
     * @return
     * @throws AppException
     */
    public Map<String, String> getJobConfigByTypeAndSource(String project, String job_type, String source_name, String job_name) throws AppException {
        List<JobConfigurationEntity> config_items = jobConfigurationDao.getJobConfigurationsByJobTypeAndSourceName(project, job_type, source_name);
        if (config_items == null) {
            slog.error("get job config by type return empty config:" + project + "/" + job_type + "/" + source_name);
            throw new AppException("0", "get job config error");
        }
        Map<String, String> configs = Maps.newHashMap();
        for (JobConfigurationEntity item : config_items) {
            configs.put(item.getVarName(), item.getVarValue());
        }
        configs.put("project_name", project);
        configs.put("source_name", source_name);
        configs.put("job_id", job_name);
        configs.put("job_type", job_type);
        return configs;
    }

    /**
     * 获取job模版的文件夹路径
     *
     * @param base_dir
     * @param project
     * @param job_type
     * @return
     */
    public String getAkProjectFileDir(String base_dir, String project, String job_type) {
        if (!base_dir.endsWith(File.separator)) {
            base_dir = base_dir + File.separator;
        }
        return base_dir + project + File.separator + job_type + File.separator;
    }

    /**
     * 获取job删除模版的文件夹路径
     *
     * @param base_dir
     * @param project
     * @return
     */
    public String getAkDeleteFileDir(String base_dir, String project) {
        if (!base_dir.endsWith(File.separator)) {
            base_dir = base_dir + File.separator;
        }
        return base_dir + project + File.separator + "delete" + File.separator;
    }

    /**
     * 获取替换之后的文件目录
     *
     * @param base_dir
     * @param job_id
     * @return
     */
    public String getSubmitedJobDir(String base_dir, Integer job_id) {
        if (!base_dir.endsWith(File.separator)) {
            base_dir = base_dir + File.separator;
        }
        return base_dir + job_id + File.separator;
    }

    /**
     * 检测输入参数
     *
     * @param info
     * @return
     * @throws AppException
     */
    public Boolean checkJobInfoIn(JobInfoIn info) throws AppException {
        if (info.getProject() == null || info.getProject().isEmpty() == true) {
            throw new AppException("0", "need project");
        }

        return true;
    }

    public JobInfosEntity getJobInfoByJobId(Integer job_id) throws AppException {
        JobInfosEntity jobInfosEntity = null;
        try {
            jobInfosEntity = jobInfosDao.getJobInfoByJobId(job_id);
        } catch (Exception ee) {
            slog.error("get job info error:" + ee.getMessage());
        }
        if (jobInfosEntity == null) {
            throw new AppException("0", "cant find job info ");
        }
        return jobInfosEntity;
    }


    public Integer createDeleteJob(Integer job_id) throws AppException {
        JobInfosEntity jobInfosEntity = jobInfosDao.getJobInfoByJobId(job_id);
        jobInfosEntity.setStatus(AppConstants.JOB_STATUS_DEL);
        jobInfosDao.updateJobInfo(jobInfosEntity);
        Integer id = 0;
        try {
            Integer count = jobInfosDao.createJobInfo(jobInfosEntity);
            if (count == 1) {
                id = jobInfosEntity.getJobId();
            }
        } catch (Exception ee) {
            slog.error("create job info error:" + ee.getMessage());
            throw new AppException("0", "create job info error");
        }
        String ak_project = jobInfosEntity.getProject() + "_" + id + "_" + jobInfosEntity.getJobName();
        Map<String, String> job_configs = new HashMap<>();
        job_configs.put("job_id", jobInfosEntity.getJobName());
        job_configs.put("source_name", jobInfosEntity.getJobName().split("_")[0]);
        job_configs.put("job_type", jobInfosEntity.getJobType());
        slog.info("get job configs:" + job_configs);
        String temp_file_dir = getAkDeleteFileDir(zk_base_job_dir, jobInfosEntity.getProject());
        String dest_file_dir = getSubmitedJobDir(zk_submited_job_dir, id);
        String project_file = dest_file_dir + AppConstants.JOB_ZIP_FILE_NAME;
        fileService.buildNewJobZipFiles(temp_file_dir, dest_file_dir, job_configs);
        try {
            akService.createPorjcet(ak_project, ak_project);
            akService.uploadProjectZip(ak_project, project_file);
            List<String> flows_info = akService.fetchFlowsByProject(ak_project);
            JSONArray db_flow_ids = new JSONArray();
            JSONObject db_job_ids = new JSONObject();
            JSONObject db_exec_ids = new JSONObject();
            for (int i = 0; i < flows_info.size(); i++) {
                String flow_id = flows_info.get(i);
                db_flow_ids.add(flow_id);
                Map<String, JSONArray> jobs = akService.fetchJobsByFlowAndProject(ak_project, flow_id);
                db_job_ids.put(flow_id, jobs.get(flow_id));
            }
            for (int i = 0; i < flows_info.size(); i++) {
                String flow_id = flows_info.get(i);
                Integer execid = akService.executeFlow(ak_project, flow_id);
                db_exec_ids.put(flow_id, execid);
            }
            AkManagerInfoEntity akManagerInfoEntity = new AkManagerInfoEntity();
            akManagerInfoEntity.setJobId(id);
            akManagerInfoEntity.setProjectName(ak_project);
            akManagerInfoEntity.setProjectFile(project_file);
            akManagerInfoEntity.setFlowIds(db_flow_ids.toJSONString());
            akManagerInfoEntity.setJobIds(db_job_ids.toJSONString());
            akManagerInfoEntity.setExecIds(db_exec_ids.toJSONString());
            akManagerInfoEntity.setCreateTime(Jdate.getNowStrTime());
            akManagerInfosDao.createAkMamangerInfo(akManagerInfoEntity);

        } catch (Exception ee) {
            slog.error("create zk project error:" + ee.getMessage());
            throw new AppException("0", "create job error");
        }
        slog.info("create task:" + id);
        return id;
    }

    public JobInfoIn getJobInfoById(int job_id) throws AppException {
        JobInfosEntity jobInfosEntity = jobInfosDao.getJobInfoByJobId(job_id);
        if (jobInfosEntity == null) {
            slog.error("cant find job by :" + job_id);
            throw new AppException("0", "cant find job");
        }
        JobInfoIn jobInfoIn = new JobInfoIn();
        try {
            jobInfoIn.init(jobInfosEntity);
//            jobInfoIn.setUser_id(jobInfosEntity.getUserId());
//            jobInfoIn.setJob_name(jobInfosEntity.getJobName());
////            获取job对应的数据源
//            String source_name = jobInfosEntity.getJobName().split("_")[0];
//            jobInfoIn.setProject(jobInfosEntity.getProject());
//            jobInfoIn.setJob_type(jobInfosEntity.getJobType());
//            jobInfoIn.setSource_name(source_name);
//            JSONObject j_params = JSONObject.parseObject(jobInfosEntity.getParams());
//
//            jobInfoIn.setBegin_time((String) j_params.get("begin_time"));
//            jobInfoIn.setEnd_time((String) j_params.get("end_time"));
//            jobInfoIn.setProduct_ids((String) j_params.get("products"));

        } catch (Exception ee) {
            slog.error("cant find job by :" + job_id);
            throw new AppException("0", "cant find job");
        }
        slog.info("getJobInfoById is " + jobInfoIn);
        return jobInfoIn;
    }

    public List<JobInfosEntity> getRunningJob(){
        return jobInfosDao.getRunningJob();
    }

    public JobInfosEntity getQueuedJob(){
        return jobInfosDao.getQueuedJob();
    }

}
