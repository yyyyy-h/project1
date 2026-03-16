package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //注解加在什么位置上（方法，类....）
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    OperationType value();

}
