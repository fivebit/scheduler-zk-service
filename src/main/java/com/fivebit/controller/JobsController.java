package com.fivebit.controller;

import com.alibaba.fastjson.JSONObject;
import com.fivebit.common.Slog;
import com.fivebit.common.Utils;
import com.fivebit.controller.InterfaceBean.JobInfoIn;
import com.fivebit.controller.InterfaceBean.JobSearchIn;
import com.fivebit.entity.JobInfosEntity;
import com.fivebit.errorhandling.AppException;
import com.fivebit.service.CreateAkJobService;
import com.fivebit.service.JobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by fivebit on 2017/6/16.
 * jobs 接口包括创建任务，查看任务列表，查看任务详情
 */
@Component
@Path("/jobs")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class JobsController {
    @Context
    private HttpServletRequest request;

    @Autowired
    private JobsService jobsService;

    @Autowired
    private CreateAkJobService createAkJobService;
    @Autowired
    Slog slog;

    /**
     * 创建一个job
     *
     * @return
     * @throws AppException
     * @params user_id, job_id
     */
    @POST
    public Response createJob(JobInfoIn jobInfoIn) throws AppException {
        slog.info("create job:" + jobInfoIn.toString());
        JobInfosEntity jobInfosEntity = jobsService.insertJobInfo(jobInfoIn);
        //createAkJobService.start();
//        jobsService.createNewJob(jobinfo);
        return Response.status(Response.Status.OK)// 201
                .entity(Utils.getRespons(jobInfosEntity.getJobId())).build();
    }


    @DELETE
    @Path("/{job_id}")
    public Response delJob(@PathParam("job_id") Integer job_id) throws AppException {
        //权限控制
//        String token = request.getHeader("token");
        Boolean st = jobsService.delJobByJobId(job_id);
        Integer id = jobsService.createDeleteJob(job_id);
        return Response.status(Response.Status.OK)
                .entity(Utils.getRespons()).build();
    }

    /**
     * 查询任务列表。包括检索单个任务，分页，任务状态等。
     *
     * @param jobSearchIn
     * @return
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    //@Consumes("text/plain; charset=UTF-8")
    public Response getJobList(@BeanParam JobSearchIn jobSearchIn) throws AppException {
        slog.info("get job list search in:" + jobSearchIn);
        JSONObject job_list = jobsService.getJobList(jobSearchIn);
        return Response.status(Response.Status.OK)
                .entity(Utils.getRespons(job_list)).build();
    }

    @Path("/list")
    @POST
    public Response getJobListByPost(JobSearchIn jobSearchIn) throws AppException {
        slog.info("get job list search in:" + jobSearchIn);
        Map<String, Object> job_list = jobsService.getJobList(jobSearchIn);
        return Response.status(Response.Status.OK)
                .entity(Utils.getRespons(job_list)).build();
    }


    @PUT
    @Path("/rerun/{job_id}")
    public Response reRunJob(@PathParam("job_id") Integer job_id) throws AppException {
//        jobsService.rerunJobByJobId(job_id);
//        通过job_id获得job所有信息

        JobInfoIn jobInfoIn = jobsService.getJobInfoById(job_id);
        slog.info("rerun jobInfoIn is " + jobInfoIn);
//        逻辑删除作业
        Boolean st = jobsService.delJobByJobId(job_id);
//        创建一个跟原job配置相同的job  （重新读取资源配置文件）
//        覆盖JobInfoIn 初始化的
//        jobInfoIn.setJob_type("ranking");
//        jobsService.createNewJob(jobInfoIn);
        jobsService.insertJobInfo(jobInfoIn);
        //createAkJobService.start();
        return Response.status(Response.Status.OK).entity(Utils.getRespons()).build();
    }

    @GET
    @Path("/{job_id}")
    @Consumes("text/plain; charset=UTF-8")
    public Response getJobInfo(@PathParam("job_id") Integer job_id) throws AppException {
        slog.info("get job info in:" + job_id);
        JobInfosEntity info = jobsService.getJobInfoByJobId(job_id);
        return Response.status(Response.Status.OK)
                .entity(Utils.getRespons(info)).build();
    }
}
