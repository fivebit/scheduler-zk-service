## scheduler v2
后端使用azkaban 作为任务调度平台。

本地存放job信息，批量同步任务状态。

### 框架
spring+jersey+mybatis+postgresql

### 打包

系统使用maven构建，执行mvn clean install -P[dev,test,prod], 在target目录下，会生成开发包，测试包，线上包 scheduler-v2.war。

跳过单测，使用：mvn clean install  -Dmaven.test.skip=true -Ptest

### 系统流程
####创建job
1. 插入scheduler.job_infos记录，设置状态为`init`.
2. 创建ak的project。name为`[xxxr]_[job_id]_job_name`.
3. 获取datasource对应的配置。
4. 通过不同的project和job_type，获取对应的job文件夹。
5. 结合上面两个信息，替换生成zip文件。并上传到ak。
6. 获取该project所有的flow_id,以及flow_id下对应的job_id.
7. 调用execute flow接口。获取各个flow对应的exec_id。
8. 这些ak信息存放在scheduler.ak_manager_infos表中。
9. 更新job的状态为new。
##### 获取任务列表
接口只会按照用户和状态查询scheduler.job_infos这个数据表。后端分页之后返回。
通过`JobStatusSyncTask`这个线程，同步ak上的状态到本地数据表。


测试示例：

	POST https://192.168.1.2:8443/scheduler-v2/jobs
	{"user_id":"xxx","source_name":"xxxx","job_name":"test fromclient","project":"xxxyy",
	"job_type":"ranking","begin_time":"2017-09-12 12:10:40","end_time":"2017-09-14 12:10:40",
	"product_ids":"1,2,3"
	}
	返回：
	{
	    "status": 5055,
	    "code": 0,
	    "message": "create job error"
	}
	{
	    "code": "0",
	    "data": "",
	    "status": 200
	}
	删除job
	DELETE https://192.168.1.2:8443/scheduler-v2/jobs/{job_id}
	返回：
	{
	    "status": 5055,
	    "code": 0,
	    "message": "del job error"
	}
	{
	    "code": "0",
	    "data": "",
	    "status": 200
	}
	重新跑任务：
	PUT https://192.168.1.2:8443/scheduler-v2/jobs/rerun/12
	{
	    "code": "0",
	    "data": "",
	    "status": 200
	}
	{
	    "status": 5055,
	    "code": 0,
	    "message": "rerun job error"
	}
	
	获取job列表：
	GET  https://192.168.1.2:8443/scheduler-v2/jobs?user_id=uisdf&job_status=all&job_name=iiiig&page=3&page_size=5
	{
	    "code": "0",
	    "data": [
	        {
	            "job_id": 4,
	            "job_name": "xxx om client",
	            "job_start_time": "1970-01-01 12:00:00",
	            "job_status": "running",
	            "prediction_end_time": "",
	            "prediction_start_time": ""
	        }
	    ],
	    "status": 200
	}
	获取job列表,上一个url的？有问题。临时采用POST方式传输。
	POST  https://192.168.1.2:8443/scheduler-v2/jobs
	body:
	{"user_id":"iskdf","job_status":"all","job_name":"kljsdkf","page":1,"page_size":1}
	return:
	{
        "code": "0",
        "data": {
            "list": [
                {
                    "job_id": 4,
                    "job_name": "xxxxfrom client",
                    "job_start_time": "1970-01-01 08:00:00",
                    "job_status": "fail",
                    "prediction_end_time": "",
                    "prediction_start_time": ""
                }
            ],
            "all_count": 16,
            "page": 0,
            "page_count": 4,
            "page_size": 5
        },
        "status": 200
    }
