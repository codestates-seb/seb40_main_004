package com.morakmorak.morak_back_end.config;

import com.morakmorak.morak_back_end.interceptor.PerformanceLoggingInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Aspect
@Configuration
@EnableAspectJAutoProxy
public class LoggingConfig {
    @Pointcut("execution(* com.morakmorak.morak_back_end.service..*.*(..))")
    public void monitor() { }

    @Bean
    public PerformanceLoggingInterceptor performanceLoggingInterceptor() {
        return new PerformanceLoggingInterceptor();
    }

    @Bean
    public Advisor performanceLoggingAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("com.morakmorak.morak_back_end.config.LoggingConfig.monitor()");
        return new DefaultPointcutAdvisor(pointcut, performanceLoggingInterceptor());
    }
}
