package com.sipsd.flow.service.flowable.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.dao.flowable.IFlowableNoticeTaskDao;
import com.sipsd.flow.service.flowable.IFlowableNoticeTaskService;
import com.sipsd.flow.vo.flowable.NoticeTask;
import com.sipsd.flow.vo.flowable.NoticeTaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: suxy
 * @Description: 抄送
 * @DateTime:: 2021/11/11 9:19
 */
@Service
public class FlowableNoticeTaskServiceImpl extends ServiceImpl<IFlowableNoticeTaskDao, NoticeTask> implements IFlowableNoticeTaskService {

    @Autowired
    IFlowableNoticeTaskDao flowableNoticeTaskDao;

    @Override
    public void insertNoticeTask(NoticeTask noticeTask) {
        flowableNoticeTaskDao.insertNoticeTask(noticeTask);
    }

    @Override
    public PageModel<NoticeTask> getNoticeTasks(NoticeTaskQuery noticeTaskQuery, Query query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Page<NoticeTask> noticeTaskPage = flowableNoticeTaskDao.noticeTaskPage(noticeTaskQuery);
        return  new PageModel<>(noticeTaskPage);
    }

    @Override
    public List<NoticeTask> noticeTaskList(NoticeTaskQuery noticeTaskQuery) {
        return flowableNoticeTaskDao.noticeTaskList(noticeTaskQuery);
    }

    @Override
    public NoticeTask queryById(String id) {
        return flowableNoticeTaskDao.queryById(id);
    }
}
