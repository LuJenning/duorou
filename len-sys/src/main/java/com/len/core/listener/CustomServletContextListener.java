package com.len.core.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.stereotype.Component;

/**
 * @author zhuxiaomeng
 * @date 2018/1/6.
 * @email 154040976@qq.com
 */
@Component
public class CustomServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("-------contextInitialized-----------");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("------------contextDestroyed-------------");
    }
}
