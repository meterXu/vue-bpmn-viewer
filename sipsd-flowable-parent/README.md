# sipsd-flow-modeler

#### 介绍
flowable modeler集成项目
- 利用flowabel6.4.2为基础，进行SpringBoot+Mybatis-plus 整合；  
- 支持热发布；
- 支持工作流模型导入导出；
- 支持单线和并联审批、会签、条件判断；
- 支持流程撤销、支持流程的动态跳转；
- 支持整体事件整合；
- 支持自定义事件 监听处理 具体业务，达到完全解耦；  
- 进行了常用接口的restFul 封装；  
- 改造了model的新增和插入，支持外部租户改造；

#### 适用场景
 1、需要动态配置工作流程的业务场景；   
 2、流服务编排； 
 
#### 安装教程
1.本地创建一个数据库叫sipsd-flow-modeler；   
2.修改数据库的账号密码；   
3.自动建表  
   首次启动的application.yml 中  database-schema-update: true 必须是true ，可以自动创建表。以后可以变成false；     
4. 访问流程设计器 
http://127.0.0.1:8989/sipsd-flow-modeler/  
5.restful 封装接口说明：  
http://127.0.0.1:8989/sipsd-flow-modeler/doc.html

#### 自定义流程用户
- 创建用户视图
1.更改 act_id_user 表名(作为备份)    
2.根据自己业务场景一个视图名为act_id_user的视图，其结构同act_id_user表结构  
- 创建角色/部门视图
简单的 用户组，根据角色获得  
1.更改 act_id_group 表名(作为备份)    
2.根据自己业务场景一个视图名为act_id_group的视图，其结构同act_id_group表结构

#### 流程接口实例API
- 地址 
postman：https://www.getpostman.com/collections/2830462af356ed1164c9
- 核心API    
[1] 流程操作
- [x] 1.流程启动    
- [x] 2.流程图（运行）  
- [x] 3.流程设计图 
- [x] 4.审批  
- [x] 5.终止流程  
- [x] 6.撤回流程  
- [x] 7.流程转办  
- [x] 8.签收  
- [x] 10.委派  
- [x] 11.驳回节点  
- [x] 13.流程所有节点  
......
  
[2] 任务查询
- [x] 1.待办任务  
- [x] 2.已办任务  
- [x] 3.表单详情  
- [x] 4.我创建的流程
- [x] 5.审核操作列表  

#### 流程实例操作
以户外广告工作流为例  
1. 导入《户外广告设置审批.bpmn20.xml》文件  
2. 点击发布按钮启动工作流
3. postman或者swagger-ui页面请求api


1.查询代办任务和已办任务接口中新增 表单名称和业务主键属性的查询
2. (1.在stencilset_bpmn.json文件中新增自定义属性节点-最大审批天数
   (2.新增modelui界面的节点的自定义属性最大审批天数-继承UserTaskJsonConverter类,BpmnJsonConverter类
     来实现增强
   (3.新增UI界面属性-最大审批天数(定义在xml中为task_max_day)，新增拓展表-act_ru_extension_task中设置
了最大审批天数的字段，TASK_MAX_DAY_为流程定义时的默认天数，CUSTOM_TASK_MAX_DAY_为自定义最大审批天数








 