package com.sipsd.flow.service.flowable;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sipsd.flow.bean.NoticeTask;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.vo.flowable.NoticeTaskQuery;
import com.sipsd.flow.vo.flowable.TaskQueryVo;

import java.util.List;

/**
 * @Author: suxy
 * @Description: 抄送任务
 * @DateTime:: 2021/11/11 9:12
 */
public interface IFlowableNoticeTaskService extends IService<NoticeTask> {

    /**
     * 新增抄送任务
     * @param noticeTask
     */
    void insertNoticeTask(NoticeTask noticeTask);

    /**
     * 获取抄送任务--分页
     * @param noticeTaskQuery
     * @param query
     * @return
     */
    PageModel<NoticeTask> getNoticeTasks(NoticeTaskQuery noticeTaskQuery, Query query);

    /**
     * 获取抄送任务
     * @param noticeTaskQuery
     * @return
     */
    List<NoticeTask> noticeTaskList(NoticeTaskQuery noticeTaskQuery);

    /**
     * 获取抄送任务详情
     * @param id
     * @return
     */
    NoticeTask queryById(String id);
}
