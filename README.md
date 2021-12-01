# sipsd-flow-modeler

## 工作流详细技术文档地址(域账号登录)-沃壤赋能平台->帮助文档->工作流
http://58.210.9.133/iplatform/geekdoc

## 介绍
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

## 项目结构
* [前端](sipsd-flowable-editor/README.md)
* [后端](sipsd-flowable-parent/README.md)

## 适用场景
 1、需要动态配置工作流程的业务场景；   
 2、流服务编排； 

## 注意事项
当前适配mysql5.x数据库如果用高版本8.x数据库请修改以下内容：
1. maven的mysql版本请选择8.x.x版本 比如<mysql.version>8.0.11</mysql.version>
2. 配置文件中的driver-class-name请设置为com.mysql.cj.jdbc.Driver
3. url后面需加上&nullCatalogMeansCurrent=true

## 安装教程
1.本地创建一个数据库叫sipsd-flow-modeler；   
2.修改数据库的账号密码；   
3.自动建表  
   首次启动的application.yml 中  database-schema-update: true 必须是true ，可以自动创建表。以后可以变成false；     
4. 访问流程设计器 
http://192.168.126.25/sipsd-flow-modeler 
5.restful 封装接口说明：  
http://192.168.126.25/sipsd-flow-modeler/doc.html
5. 样例地址： http://192.168.126.25/ifp-simple-flowable

## 自定义流程用户
- 创建用户视图
1.更改 act_id_user 表名(作为备份)    
2.根据自己业务场景一个视图名为act_id_user的视图，其结构同act_id_user表结构  
- 创建角色/部门视图
简单的 用户组，根据角色获得  
1.更改 act_id_group 表名(作为备份)    
2.根据自己业务场景一个视图名为act_id_group的视图，其结构同act_id_group表结构

## 流程接口实例API-V4
- 地址 
postman：https://www.postman.com/collections/75533c2653809a48406b
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

[3] 实例ID自定义属性任务查询
- [x] 1.根据实例ID查询代办任务  
- [x] 2.根据实例ID查询已办任务  
- [x] 3.根据实例ID查询全部任务  
- [x] 4.更新流程节点最大审批天数
- [x] 5.根据ID更新督办信息 
......
## 流程实例操作
以户外广告工作流为例  
1. 导入《户外广告设置审批.bpmn20.xml》文件  
2. 点击发布按钮启动工作流
3. postman或者swagger-ui页面请求api

bug:
1.并行网关跳转驳回bug(已解决，驳回可以驳回到任意节点)
2.多实例加签减签bug
3.当在流程实例中设置候选用户时无法在act_ru_task中插入assignee，可以用最新的附加接口来查询


1.查询代办任务和已办任务接口中新增 表单名称和业务主键属性的查询
2. (1.在stencilset_bpmn.json文件中新增自定义属性节点-最大审批天数
   (2.新增modelui界面的节点的自定义属性最大审批天数-继承UserTaskJsonConverter类,BpmnJsonConverter类
     来实现增强
   (3.新增UI界面属性-最大审批天数(定义在xml中为task_max_day)，新增拓展表-act_ru_extension_task中设置
了最大审批天数的字段，TASK_MAX_DAY_为流程定义时的默认天数，CUSTOM_TASK_MAX_DAY_为自定义最大审批天数
3.定义了附加表act_ru_extension_task --resource/db/flowable.mysql.create.extension.sql

提示：
在设计流程的时候节点需要显示的设置是串行还是并行属性
并行判断提示：
1.nrOfInstances 该会签环节中总共有多少个实例
2.nrOfActiveInstances 当前活动的实例的数量，即还没有 完成的实例数量。
3.nrOfCompletedInstances 已经完成的实例的数量

V4更新内容：
1. 驳回：只能驳回到上一个审批节点 
2.跳转只能跳转到以前的审批过的节点，无法跳转到从未执行过的新节点
3.新增了最大审批时间，在编辑器中定义的时候请设置为整数，查询的结果中有两个字段taskMaxDay是编辑流程的
时候初始的最大审批天数，customTaskMaxDay是重新定义最大审批时间的字段，请都以此字段为准，restTime为到期审批时间与当前
时间的时间差，如果状态为代办，正数为还未到期，如果为负，则超期，如果状态是已办，则字段duration表示这个节点的完成时间段
4.新增根据实例ID来查询已办代办任务，请参考接口列表-自定义属性任务列表

部署地址：
1. 后端接口地址：http://192.168.126.25/sipsd-flow-modeler/doc.html
2. 后端流程注册地址：http://192.168.126.25/sipsd-flow-modeler/#/processes
3. 前端静态流程显示地址：http://192.168.126.25/flowable-editor/#/bpmn/viewer?type=1&xmlId=4b99159a-bc63-11eb-b2ee-5e2c421612f0
4. 前端动态流程显示地址:http://192.168.126.25/flowable-editor/#/bpmn/viewer?type=2&instanceId=31947db4cf4511eba5465e2c421612f0
5. [前端执行器DEMO](http://192.168.126.25/flowable-editor/#/bpmn/staticViewer)




 