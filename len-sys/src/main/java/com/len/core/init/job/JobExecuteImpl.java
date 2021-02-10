package com.len.core.init.job;

import com.len.base.handler.ExecuteContext;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author zxm
 * @date 2020/8/11 22:02
 */


public class JobExecuteImpl implements ExecuteContext {

    @Override
    public void execute(ContextRefreshedEvent event) {
        /*
         * 通过线程开启数据库中已经开启的定时任务
         */
        DataSourceJobThread myThread = event.getApplicationContext().getBean(
                DataSourceJobThread.class);
        myThread.start();
    }
}
