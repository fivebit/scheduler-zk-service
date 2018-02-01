package com.fivebit.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fivebit on 2017/5/19.
 * 一些静态配置
 */
public class AppConstants {
    public static final int APP_ERROR_CODE = 5055;

    public static final String JOB_STATUS_NEW = "new";
    public static final String JOB_STATUS_INIT = "init";
    public static final String JOB_STATUS_SUCCESS = "success";
    public static final String JOB_STATUS_FAIL = "failed";
    public static final String JOB_STATUS_KILLED = "killed";
    public static final String JOB_STATUS_RUNNING = "running";
    public static final String JOB_STATUS_DEL = "del";
    public static final String JOB_STATUS_RERUN = "rerun";
    public static final String JOB_STATUS_QUEUED = "queued";

    public static final Integer JOB_LIST_PAGE_SIZE=10;


    public static final String JOB_ZIP_FILE_NAME = "job.zip";

    public static List<String> TASK_STATUS = new ArrayList<String>(){
        {
            add("new");     //新创建
        }
    };

}
