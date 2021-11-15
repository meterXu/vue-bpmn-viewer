package com.sipsd.flow.dao.flowable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.sipsd.flow.bean.NoticeTask;
import com.sipsd.flow.vo.flowable.NoticeTaskQuery;
import com.sipsd.flow.vo.flowable.TaskQueryVo;
import org.apache.ibatis.annotations.Param;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: suxy
 * @Description: 抄送
 * @DateTime:: 2021/11/11 9:13
 */
@Repository
public interface IFlowableNoticeTaskDao extends BaseMapper<NoticeTask> {

    /**
     * 新增抄送
     * @param noticeTask
     */
    void insertNoticeTask(NoticeTask noticeTask);

    /**
     * 根据抄送人查询抄送信息
     */
    List<NoticeTask> noticeTaskList(NoticeTaskQuery noticeTaskQuery);

    /**
     * 根据抄送人查询抄送信息
     * @return
     */
    Page<NoticeTask> noticeTaskPage(NoticeTaskQuery noticeTaskQuery);

    /**
     * 根据任务Id 获取抄送任务详情
     * @param taskId
     * @return
     */
    NoticeTask queryById(@Param(value = "taskId") String taskId);

}
