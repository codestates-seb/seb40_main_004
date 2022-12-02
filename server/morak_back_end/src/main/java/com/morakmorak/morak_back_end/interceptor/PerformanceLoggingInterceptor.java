package com.morakmorak.morak_back_end.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;

import java.util.Date;

public class PerformanceLoggingInterceptor extends AbstractMonitoringInterceptor {
    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log logger) throws Throwable {
        String name = createInvocationTraceName(invocation);
        long start = System.currentTimeMillis();
        logger.info("Method" + name + " execution started at:" + new Date());

        try {
            return invocation.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long time = end - start;
            logger.info("Method " + name + " execution lasted:" + time + " ms");
            logger.info("Method " + name + " execution ended at:" + new Date());

            if (time > 4000) {
                logger.warn("☠️☠️☠️☠️☠️ 해당 메서드의 실행 시간이 3500ms를 초과했습니다. ☠️☠️☠️☠️☠️");
            }else if (time > 3000) {
                logger.warn("🚨🚨🚨🚨🚨 해당 메서드의 실행 시간이 2500ms를 초과했습니다. 🚨🚨🚨🚨🚨");
            }else if (time > 2000) {
                logger.warn("⚠️⚠️⚠️⚠️⚠️ 해당 메서드의 실행 시간이 1500ms를 초과했습니다. ⚠️⚠️⚠️⚠️⚠️");
            }else if (time > 1000) {
                logger.warn("🤫🤫🤫🤫🤫 해당 메서드의 실행 시간이 500ms를 초과했습니다. 🤫🤫🤫🤫🤫");
            }
        }
    }
}
