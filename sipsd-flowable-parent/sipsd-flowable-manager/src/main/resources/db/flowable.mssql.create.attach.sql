create table ACT_RU_EXTENSION_TASK (
    ID_ int  not null AUTO_INCREMENT,
    REV_ int default 1,
    PROC_INST_ID_ nvarchar(64) not null,
    PROC_DEF_ID_ nvarchar(64) not null,
    EXECUTION_ID_ nvarchar(64) not null,
    TASK_MAX_DAY_ int,
    CREATE_TIME_ datetime,
    UPDATE_TIME_ datetime,
    TASK_ID_ nvarchar(64),
    CUSTOM_TASK_MAX_DAY_ int,
    TASK_DEF_KEY_ nvarchar(255),
    TENANT_ID_ nvarchar(255) default '',
    TASK_NAME_ nvarchar(255) default '',
    END_TIME_ datetime,
    TASK_REST_TIME_ int,
    FROM_KEY_ nvarchar(100),
    ASSIGNEE_ nvarchar(100),
    GROUP_ID_ nvarchar(100),
    FLOW_TYPE_ nvarchar(100),
    primary key (ID_)
);
create index ACT_RU_EXTENSION_TASK_PROC_INST on ACT_RU_EXTENSION_TASK(PROC_INST_ID_);
create index ACT_RU_EXTENSION_TASK_ASSIGNEE on ACT_RU_EXTENSION_TASK(TENANT_ID_);
create index ACT_RU_EXTENSION_TASK_GROUP on ACT_RU_EXTENSION_TASK(GROUP_ID_);