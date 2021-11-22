CREATE TABLE IF NOT EXISTS ACT_HI_NOTICE_TASK
(
    ID_             varchar(64) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL,
    PROC_DEF_ID_    varchar(64) CHARACTER SET utf8 COLLATE utf8_bin   DEFAULT NULL,
    TASK_DEF_ID_    varchar(64) CHARACTER SET utf8 COLLATE utf8_bin   DEFAULT NULL,
    TASK_DEF_KEY_   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
    PROC_INST_ID_   varchar(64) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL,
    NAME_           varchar(255) CHARACTER SET utf8 COLLATE utf8_bin  DEFAULT NULL,
    PARENT_TASK_ID_ varchar(64) CHARACTER SET utf8 COLLATE utf8_bin  NOT NULL,
    DESCRIPTION_    varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
    OWNER_          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin  DEFAULT NULL,
    ASSIGNEE_       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
    CREATE_TIME_    datetime(3)                                       DEFAULT NULL,
    PRIORITY_       int                                               DEFAULT NULL,
    FORM_KEY_       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin  DEFAULT NULL,
    CATEGORY_       varchar(255) CHARACTER SET utf8 COLLATE utf8_bin  DEFAULT NULL,
    TENANT_ID_      varchar(255) CHARACTER SET utf8 COLLATE utf8_bin  DEFAULT '',
    BUSINESS_INFO_  text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci,
    BUSINESS_KEY_   varchar(255) CHARACTER SET utf8 COLLATE utf8_bin  DEFAULT NULL,
    FORM_NAME_      varchar(255) COLLATE utf8mb4_general_ci           DEFAULT NULL,
    PRIMARY KEY (ID_)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;