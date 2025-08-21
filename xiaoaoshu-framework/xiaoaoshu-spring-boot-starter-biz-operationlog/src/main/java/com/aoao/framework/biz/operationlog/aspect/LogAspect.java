package com.aoao.framework.biz.operationlog.aspect;

import com.aoao.framework.biz.operationlog.annotation.Log;
import com.aoao.framework.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author aoao
 * @create 2025-08-18-22:34
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    // 切点表达式
    @Pointcut("@annotation(com.aoao.framework.biz.operationlog.annotation.Log))")
    public void log(){}

    // 增强日志
    @Around("log()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取现在时间
        long startTime = System.currentTimeMillis();
        // MDC
        MDC.put("traceId", UUID.randomUUID().toString());
        // 获取类和方法
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        // 获取参数
        Object[] args = joinPoint.getArgs();
        String argsStr = JsonUtil.toJson(args);
        // 获取注解中的value注释
        String value = getValue(joinPoint);
        // 执行方法
        Object result = joinPoint.proceed();
        log.info("====== 请求开始: [{}], 入参: {}, 请求类: {}, 请求方法: {} ====== ",
                value, argsStr, className, methodName);

        // 结束计算消耗时间
        long endTime = System.currentTimeMillis();

        log.info("====== 请求结束: [{}], 耗时: {}ms, 出参: {} ====== ",
                value, (endTime-startTime), JsonUtil.toJson(result));

        return result;
    }

    private String getValue(JoinPoint joinPoint) {
        // 1. 从 ProceedingJoinPoint 获取方法签名 MethodSignature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 2. 获取真正的Method
        Method method = signature.getMethod();
        // 3. 获取注解
        Log annotation = method.getAnnotation(Log.class);
        // 4. 获取description属性
        return annotation.value();
    }

}
