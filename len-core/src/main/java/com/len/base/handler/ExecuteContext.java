package com.len.base.handler;

import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author zxm
 * @date 2020/8/11 21:57
 */


public interface ExecuteContext {

    void execute(ContextRefreshedEvent event);
}
