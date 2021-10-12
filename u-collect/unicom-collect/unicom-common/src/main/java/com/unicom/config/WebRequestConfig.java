package com.unicom.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
public class WebRequestConfig {


    @Value("${unicom.requestIntercept.isIntercept:false}")
    private Boolean isIntercept;

    @Value("${unicom.requestIntercept.isHead:false}")
    private Boolean isHead;

    @Value("${unicom.requestIntercept.isArgs:false}")
    private Boolean isArgs;


}
