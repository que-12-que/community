package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("AfterReturning");
    }
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("AfterThrowing");
    }
    @Around("pointcut()")
    public Object Around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        // 调用目标组件方法
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }

}
