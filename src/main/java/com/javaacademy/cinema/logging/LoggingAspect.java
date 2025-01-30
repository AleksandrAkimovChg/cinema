package com.javaacademy.cinema.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.javaacademy.cinema..*(..))")
    public void logServiceCalls() {
    }

    @Before("logServiceCalls()")
    public void loggingBefore(JoinPoint joinPoint) {
        log.debug("Вызов: {}, аргументы {}", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "logServiceCalls()", returning = "result")
    public void loggingAfterReturning(JoinPoint joinPoint, Object result) {
        log.debug("После вызова {} результат: {}", joinPoint.getSignature(),
                result == null ? "void метод" : result.toString());
    }

    @AfterThrowing(pointcut = "logServiceCalls()", throwing = "ex")
    public void loggingAfterThrowing(JoinPoint joinPoint, Exception ex) {
        log.debug("Выброс исключения: {} - в методе: {}", ex.toString(), joinPoint.getSignature());
    }
}
