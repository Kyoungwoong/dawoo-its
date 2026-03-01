package com.example.spring.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Aspect
@Component
public class ExecutionTimingAspect {
    private static final Logger log = LoggerFactory.getLogger(ExecutionTimingAspect.class);

    @Value("${app.timing.threshold-ms:0}")
    private long defaultThresholdMs;

    @Around(
            "within(@org.springframework.stereotype.Service *) && execution(public * *(..))"
    )
    public Object time(ProceedingJoinPoint pjp) throws Throwable {

        long startNs = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long tookNs = System.nanoTime() - startNs;
            long tookMs = tookNs / 1_000_000;
            long tookUs = tookNs / 1_000;

            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            long thresholdMs = defaultThresholdMs;

            String className = pjp.getTarget() != null ? pjp.getTarget().getClass().getSimpleName() : pjp.getSignature().getDeclaringTypeName();
            String methodName = method.getName();

            if (tookMs >= thresholdMs) {
                log.info("tookMs={} tookUs={} target={}.{}", tookMs, tookUs, className, methodName);
            }
        }
    }
}
