package ra.project.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* ra.project.service.impl.*.*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* ra.project.controller.*.*(..))")
    public void controllerLayer() {}

    @Around("serviceLayer() || controllerLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        log.info("[START] {} | Args: {}", methodName, Arrays.toString(joinPoint.getArgs()));

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        log.info("[END] {} | Duration: {}ms", methodName, duration);

        return result;
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("[SUCCESS] {} | Return: {}", joinPoint.getSignature().toShortString(),
                result != null ? result.getClass().getSimpleName() : "void");
    }

    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("[ERROR] {} | Exception: {} - {}", joinPoint.getSignature().toShortString(),
                ex.getClass().getSimpleName(), ex.getMessage());
    }
}
