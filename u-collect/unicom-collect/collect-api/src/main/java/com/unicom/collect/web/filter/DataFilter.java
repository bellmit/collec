package com.unicom.collect.web.filter;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
@Data
@Slf4j
public class DataFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("---------------到底打印不打印-------------");
        log.info("进来了-----------------数据验证");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

    }
}
