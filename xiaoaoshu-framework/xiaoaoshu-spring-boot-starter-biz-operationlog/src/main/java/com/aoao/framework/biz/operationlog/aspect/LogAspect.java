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
@Component("operationLogAspect")
@Slf4j
public class LogAspect {

    // 切点表达式
    @Pointcut("@annotation(com.aoao.framework.biz.operationlog.annotation.Log))")
    public void log(){}

    // 增强日志
    @Around("log()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MDC.put("traceId", UUID.randomUUID().toString());

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // 处理参数
        StringBuilder argsStrBuilder = new StringBuilder("[");
        for (Object arg : args) {
            if (arg instanceof org.springframework.web.multipart.MultipartFile file) {
                argsStrBuilder.append("{MultipartFile: name=")
                        .append(file.getOriginalFilename())
                        .append(", size=")
                        .append(file.getSize())
                        .append("}, ");
            } else {
                try {
                    argsStrBuilder.append(JsonUtil.toJson(arg)).append(", ");
                } catch (Exception e) {
                    argsStrBuilder.append("{unserializable-arg: ").append(arg).append("}, ");
                }
            }
        }
        if (argsStrBuilder.length() > 1) {
            argsStrBuilder.setLength(argsStrBuilder.length() - 2); // 去掉最后的逗号
        }
        argsStrBuilder.append("]");
        String argsStr = argsStrBuilder.toString();

        String value = getValue(joinPoint);

        Object result = joinPoint.proceed();

        log.info("====== 请求开始: [{}], 入参: {}, 请求类: {}, 请求方法: {} ====== ",
                value, argsStr, className, methodName);

        long endTime = System.currentTimeMillis();

        log.info("====== 请求结束: [{}], 耗时: {}ms, 出参: {} ====== ",
                value, (endTime - startTime), JsonUtil.toJson(result));

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
