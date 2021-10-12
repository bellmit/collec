package com.unicom.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicom.config.WebRequestConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class WebRequestAspect {

    @Autowired
    private WebRequestConfig webRequestConfig;


    @Pointcut("execution(public * com.unicom.college.controller..*.*(..))&&!execution(public * com.unicom.college.controller..*.showPic*(..))")
    public void webLog() {
    }


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        if(!webRequestConfig.getIsIntercept()){
            return;
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String contentType = request.getContentType() + "";


        if(webRequestConfig.getIsHead()) {
            // 打印请求相关参数
            log.info("========================================== 请求拦截 ==========================================");
            // 打印请求 url
            log.info("请求URL        : {}", request.getRequestURL().toString());
            // 打印 Http method
            log.info("请求 Method    : {}", request.getMethod());
            // 打印调用 controller 的全路径以及执行方法
            log.info("类 Method      : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            // 打印请求的 IP
            log.info("请求源IP       : {}", request.getRemoteAddr());
            // 打印请求入参
        }
        if(webRequestConfig.getIsArgs()) {
            String queryString = request.getQueryString();
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                if ("POST".equals(request.getMethod())) {
                    if (!contentType.contains("multipart/form-data;"))
                        log.info("请求参数       : {}", new ObjectMapper().writeValueAsString((joinPoint.getArgs())));
                    else
                        log.info("请求参数       : {}", "文件上传类型");
                } else if ("GET".equals(request.getMethod())) {
                    log.info("请求参数       : {}", queryString);
                }
            }
        }


    }

    /**
     * 在切点之后织入
     *
     * @throws Throwable
     */
    @After("webLog()")
    public void doAfter() throws Throwable {
        log.info("=========================================== 请求拦截结束 ===========================================");
        log.info("");
    }

    /**
     * 环绕
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        if(!webRequestConfig.getIsIntercept()){
            return proceedingJoinPoint.proceed();
        }
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // 打印出参
        log.info("返回参数为  : {}", new ObjectMapper().writeValueAsString(result));
        // 执行耗时
        log.info("执行耗时为  : {} ms", System.currentTimeMillis() - startTime);
        return result;
    }
}
