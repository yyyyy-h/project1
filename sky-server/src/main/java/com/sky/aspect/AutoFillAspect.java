package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面实现自动填充
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {}

    /**
     * 公共字段赋值
     */
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint)
    {
        log.info("开始公共字段的自动填充");
        //获取当前被拦截的方法类型
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();
        //获得实体对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0)
        {
            return;
        }
        Object entity = args[0];
        log.info("准备赋值的数据：{}",entity);

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();
        //对公共字段赋值
        if(operationType == OperationType.INSERT)
        {
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreatUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //赋值
                setCreateTime.invoke(entity,now);
                setCreatUser.invoke(entity,id);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,id);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (operationType == OperationType.UPDATE)
        {
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,id);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
