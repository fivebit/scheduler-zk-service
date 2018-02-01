#table schema

CREATE SCHEMA if not exists  scheduler;

create table if not exists scheduler.job_infos(
job_id SERIAL primary key not null,
user_id text,
user_group text,
job_name text,
project text,
job_type text,
params text,
status text,
error_message text,
start_time varchar(255),
finished_time varchar(255),
rerun_count int,
create_time varchar(255),
update_time varchar(255)
);
create index idx_job_info_user_id_status on scheduler.job_infos (user_id,status);
create index idx_job_info_status on scheduler.job_infos (status);

COMMENT ON TABLE scheduler.job_infos IS '任务列表';
COMMENT ON COLUMN scheduler.job_infos.user_id IS '用户ID';
COMMENT ON COLUMN scheduler.job_infos.user_group IS '用户所属的用户组';
COMMENT ON COLUMN scheduler.job_infos.job_name IS '任务名称';
COMMENT ON COLUMN scheduler.job_infos.project IS '任务功能类型';
COMMENT ON COLUMN scheduler.job_infos.job_type IS '任务类型';
COMMENT ON COLUMN scheduler.job_infos.params IS '任务参数';
COMMENT ON COLUMN scheduler.job_infos.status IS '任务状态／new/fail/success/running';
COMMENT ON COLUMN scheduler.job_infos.start_time IS '任务开始时间';
COMMENT ON COLUMN scheduler.job_infos.rerun_count IS '重跑次数';

create table if not exists scheduler.ak_manager_infos(
ak_manager_id SERIAL primary key not null,
job_id int,
project_name text,
project_file text,
flow_ids text,
job_ids text,
execution_status text,
exec_ids text,
create_time varchar(255),
update_time varchar(255),
status varchar(255),
message text
);
#add user_id,status,job_id index
create index idx_ak_manager_info_job_id on scheduler.ak_manager_infos (job_id);
COMMENT ON TABLE scheduler.ak_manager_infos IS 'ak详细任务状态';
COMMENT ON COLUMN scheduler.ak_manager_infos.job_id IS '调度系统job id';
COMMENT ON COLUMN scheduler.ak_manager_infos.project_name IS 'project name';
COMMENT ON COLUMN scheduler.ak_manager_infos.project_file IS 'project zip file';
COMMENT ON COLUMN scheduler.ak_manager_infos.flow_ids IS 'flow ids';
COMMENT ON COLUMN scheduler.ak_manager_infos.job_ids IS 'job ids';
COMMENT ON COLUMN scheduler.ak_manager_infos.execution_status IS '每个job的执行状态';
COMMENT ON COLUMN scheduler.ak_manager_infos.exec_ids IS '每个job执行ID';
