package com.github.k2ocabhinav.ubercloneapp.aspect;

import com.github.k2ocabhinav.ubercloneapp.security.CorrelationIdFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingAspect.class);

    @Pointcut("execution(* com.github.k2ocabhinav.ubercloneapp.controllers..*(..))")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID);
        
        log.info("Starting {}.{} | correlationId={}", className, methodName, correlationId);
        
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed {}.{} in {}ms | correlationId={}", 
                    className, methodName, duration, correlationId);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Failed {}.{} in {}ms | correlationId={} | error={}", 
                    className, methodName, duration, correlationId, e.getMessage());
            throw e;
        }
    }

    @Pointcut("execution(* com.github.k2ocabhinav.ubercloneapp.services..*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.debug("Service call: {}.{}", className, methodName);
        
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.debug("Service completed: {}.{} in {}ms", className, methodName, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Service failed: {}.{} in {}ms | error={}", 
                    className, methodName, duration, e.getMessage());
            throw e;
        }
    }
}