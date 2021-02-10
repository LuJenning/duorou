package com.len.config;

import com.len.util.SpringUtil;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Properties;

/**
 * @author zhuxiaomeng
 * @date 2018/1/30.
 * @email 154040976@qq.com
 * 事务管理
 */
@Configuration
public class TransactionalConfig {

    private static final String PROPAGATION_REQUIRED = "PROPAGATION_REQUIRED,-Throwable";
    private static final String PROPAGATION_REQUIRED_READ = "PROPAGATION_REQUIRED,-Throwable,readOnly";
    private static final String[] REQUIRED_RULE_TRANSACTION = {"insert*", "add*", "update*", "del*", "create*", "save*"};
    private static final String[] READ_RULE_TRANSACTION = {"select*", "get*", "count*", "find*"};

    /**
     * aop 自动注入 无需手动
     *
     * @return
     */
    @Bean("lenTransaction")
    public TransactionInterceptor lenTransaction() {
        PlatformTransactionManager platformTransactionManager = SpringUtil.getBean(PlatformTransactionManager.class);
        TransactionInterceptor interceptor = new TransactionInterceptor();
        Properties properties = new Properties();
        for (String s : REQUIRED_RULE_TRANSACTION) {
            //PROPAGATION_REQUIRED 级别事务
            properties.setProperty(s, PROPAGATION_REQUIRED);
        }
        for (String s : READ_RULE_TRANSACTION) {
            properties.setProperty(s, PROPAGATION_REQUIRED_READ);
        }
        interceptor.setTransactionManager(platformTransactionManager);
        interceptor.setTransactionAttributes(properties);
        return interceptor;
    }

    @Bean
    public BeanNameAutoProxyCreator getBeanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator proxyCreator = new BeanNameAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        proxyCreator.setBeanNames("*ServiceImpl", "*Controller");
        proxyCreator.setInterceptorNames("lenTransaction");
        return proxyCreator;
    }


}
