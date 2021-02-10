package com.len.core.listener;

import com.len.base.handler.ExecuteContext;
import com.len.core.init.job.JobExecuteImpl;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author zhuxiaomeng
 * @date 2018/1/6.
 * @email 154040976@qq.com
 * <p>
 * lenosp 初始化类
 */
@Component
public class LenospInit implements ApplicationListener<ContextRefreshedEvent> {


    public void onApplicationEvent(ContextRefreshedEvent event) {
        init(event);
    }

    /**
     * spring 加载后初始化操作
     *
     * @param event
     */
    public void init(ContextRefreshedEvent event) {
        ExecuteContext context = new JobExecuteImpl();
        context.execute(event);
    }

}
