CREATE TABLE IF NOT EXISTS ACT_DE_MODEL
(
    ID                varchar(255) NOT NULL,
    NAME              varchar(400) NOT NULL,
    MODEL_KEY         varchar(400) NOT NULL,
    DESCRIPTION       varchar(4000) DEFAULT NULL,
    MODEL_COMMENT     varchar(4000) DEFAULT NULL,
    CREATED           datetime(6)   DEFAULT NULL,
    CREATED_BY        varchar(255)  DEFAULT NULL,
    LAST_UPDATED      datetime(6)   DEFAULT NULL,
    LAST_UPDATED_BY   varchar(255)  DEFAULT NULL,
    VERSION           int(11)       DEFAULT NULL,
    MODEL_EDITOR_JSON longtext,
    THUMBNAIL         longblob,
    MODEL_TYPE        int(11)       DEFAULT NULL,
    TENANT_ID         varchar(255)  DEFAULT NULL,
    PRIMARY KEY (ID),
    KEY IDX_PROC_MOD_CREATED (CREATED_BY)
);

CREATE TABLE IF NOT EXISTS ACT_DE_MODEL_HISTORY
(
    ID               varchar(255) NOT NULL,
    NAME              varchar(400) NOT NULL,
    MODEL_KEY         varchar(400) NOT NULL,
    DESCRIPTION       varchar(4000) DEFAULT NULL,
    MODEL_COMMENT     varchar(4000) DEFAULT NULL,
    CREATED         datetime(6)   DEFAULT NULL,
    CREATED_BY        varchar(255)  DEFAULT NULL,
    LAST_UPDATED      datetime(6)   DEFAULT NULL,
    LAST_UPDATED_BY  varchar(255)  DEFAULT NULL,
    REMOVAL_DATE      datetime(6)   DEFAULT NULL,
    VERSION           int(11)       DEFAULT NULL,
    MODEL_EDITOR_JSON longtext,
    MODEL_ID          varchar(255) NOT NULL,
    MODEL_TYPE        int(11)       DEFAULT NULL,
    TENANT_ID         varchar(255)  DEFAULT NULL,
    PRIMARY KEY (ID),
    KEY IDX_PROC_MOD_HISTORY_PROC (MODEL_ID)
);
CREATE TABLE IF NOT EXISTS ACT_DE_MODEL_RELATION
(
    ID              varchar(255) NOT NULL,
    PARENT_MODEL_ID varchar(255) DEFAULT NULL,
    MODEL_ID        varchar(255) DEFAULT NULL,
    RELATION_TYPE   varchar(255) DEFAULT NULL,
    PRIMARY KEY (ID),
    KEY FK_RELATION_PARENT (PARENT_MODEL_ID),
    KEY FK_RELATION_CHILD (MODEL_ID),
    CONSTRAINT FK_RELATION_CHILD FOREIGN KEY (MODEL_ID) REFERENCES ACT_DE_MODEL (ID),
    CONSTRAINT FK_RELATION_PARENT FOREIGN KEY (PARENT_MODEL_ID) REFERENCES ACT_DE_MODEL (ID)
);

create table if not exists ACT_RU_EXTENSION_TASK
(
    ID_                  int          not null AUTO_INCREMENT,
    REV_                 int           default 1,
    PROC_INST_ID_        nvarchar(64) not null,
    PROC_DEF_ID_         nvarchar(64) not null,
    EXECUTION_ID_        nvarchar(64) not null,
    TASK_MAX_DAY_        int,
    CREATE_TIME_         datetime,
    UPDATE_TIME_         datetime,
    TASK_ID_             nvarchar(64),
    CUSTOM_TASK_MAX_DAY_ int,
    TASK_DEF_KEY_        nvarchar(255),
    TENANT_ID_           nvarchar(255) default '',
    TASK_NAME_           nvarchar(255) default '',
    END_TIME_            datetime,
    TASK_REST_TIME_      int,
    FROM_KEY_            nvarchar(100),
    ASSIGNEE_            nvarchar(100),
    GROUP_ID_            nvarchar(100),
    FLOW_TYPE_           nvarchar(100),
    IS_DB_               TINYINT(4) DEFAULT 0,
    BUSINESS_INFO_       TEXT,
    primary key (ID_),
    KEY ACT_RU_EXTENSION_TASK_PROC_INST (PROC_INST_ID_),
    KEY ACT_RU_EXTENSION_TASK_ASSIGNEE (ASSIGNEE_),
    KEY ACT_RU_EXTENSION_TASK_GROUP (GROUP_ID_),
    KEY ACT_EXTENSION_TASK_ID (TASK_ID_)
);

CREATE TABLE IF NOT EXISTS ACT_HI_NOTICE_TASK (
  ID_ varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PROC_DEF_ID_ varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  TASK_DEF_ID_ varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  TASK_DEF_KEY_ varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  PROC_INST_ID_ varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  NAME_ varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PARENT_TASK_ID_ varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  DESCRIPTION_ varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  OWNER_ varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  ASSIGNEE_ varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  CREATE_TIME_ datetime(3) DEFAULT NULL,
  PRIORITY_ int DEFAULT NULL,
  FORM_KEY_ varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  CATEGORY_ varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  TENANT_ID_ varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '',
  BUSINESS_INFO_ text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
  BUSINESS_KEY_ varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  FORM_NAME_ varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (ID_)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;