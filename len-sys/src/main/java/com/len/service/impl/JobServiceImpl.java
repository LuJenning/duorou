package com.len.service.impl;

import com.len.base.impl.BaseServiceImpl;
import com.len.core.quartz.JobTask;
import com.len.entity.SysJob;
import com.len.exception.MyException;
import com.len.mapper.SysJobMapper;
import com.len.service.JobService;
import com.len.util.BeanUtil;
import com.len.util.LenResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhuxiaomeng
 * @date 2018/1/6.
 * @email 154040976@qq.com
 */
@Service
@Slf4j
public class JobServiceImpl extends BaseServiceImpl<SysJob, String> implements JobService {

    @Autowired
    SysJobMapper jobMapper;

    @Autowired
    JobTask jobTask;

   /* @Autowired
    JobService jobService;*/


    @Override
    public boolean updateJob(SysJob job) {
        try {
            SysJob oldJob = getById(job.getId());
            BeanUtil.copyNotNullBean(job, oldJob);
            updateById(oldJob);
            return true;
        } catch (MyException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public LenResponse del(String id) {
        LenResponse j = new LenResponse();
        j.setFlag(false);
        if (StringUtils.isEmpty(id)) {
            j.setMsg("获取数据失败");
            return j;
        }
        SysJob job = getById(id);
        boolean flag = jobTask.checkJob(job);
        if ((flag && !job.getStatus()) || !flag && job.getStatus()) {
            j.setMsg("您任务表状态和web任务状态不一致,无法删除");
            return j;
        }
        if (flag) {
            j.setMsg("该任务处于启动中，无法删除");
            return j;
        }
        removeById(id);
        j.setFlag(true);
        j.setMsg("任务删除成功");

        return j;
    }

    @Override
    public boolean startJob(String id) {
        SysJob job = getById(id);
        jobTask.startJob(job);
        job.setStatus(true);
        updateById(job);
        return true;
    }

    @Override
    public boolean stopJob(String id) {
        SysJob job = getById(id);
        jobTask.remove(job);
        job.setStatus(false);
        updateById(job);
        return true;
    }
}
