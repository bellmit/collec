package com.unicom.roleRightShiro.filter;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.unicom.common.constant.ResponseCodeConstants;
import com.unicom.common.util.Result;
import com.unicom.roleRightShiro.common.BodyReaderHttpServletRequestWrapper;
import com.unicom.roleRightShiro.common.RequestWapper;
import com.unicom.roleRightShiro.mapper.SignMapper;
import com.unicom.roleRightShiro.utils.HttpUtils;
import com.unicom.roleRightShiro.utils.SignUtils;
import com.unicom.roleRightShiro.utils.SpringUtils;
import com.unicom.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
@Slf4j
public class SignFilter extends AccessControlFilter {
    private final static String TIMESTAMP_KEY_NAME = "timestamp";
    /**
     * 最大有效时间 默认 10秒钟失效 超出10s失效
     */
    private final static Long MAX_EFFECTIVE_TIMESTAMP = 60L * 1000;


    @Autowired
    private SignMapper signMapper;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return true;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        //包装request  获取里面包含的内容
        BodyReaderHttpServletRequestWrapper requestWrapper = null;
        if (!(request instanceof BodyReaderHttpServletRequestWrapper)) {
            requestWrapper = new BodyReaderHttpServletRequestWrapper(
                    (HttpServletRequest) request);
        }
        if (requestWrapper.getMethod().equals(RequestMethod.OPTIONS.name())) {
            return;
        }
        //获取全部参数
        SortedMap<String, Object> allParams = null;
        try {
            allParams = HttpUtils.getAllParams(requestWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //取出时间戳 做超时校验
        Long timestamp = MapUtil.getLong(allParams, TIMESTAMP_KEY_NAME);

        if (ObjectUtil.isNull(timestamp)) {
            ResponseUtils.outJson(response, Result.failed(ResponseCodeConstants.SIGN_FAIL_CODE, ResponseCodeConstants.SIGN_FAIL_MSG));
            // ResponseUtils.responseError("请求无效！");
            return;
        }
        Long diffTimestamp = System.currentTimeMillis() - timestamp;

        if (diffTimestamp > MAX_EFFECTIVE_TIMESTAMP) {
            log.info("时间戳验证问题！"+diffTimestamp);
            ResponseUtils.outJson(response, Result.failed(ResponseCodeConstants.SIGN_FAIL_CODE, ResponseCodeConstants.SIGN_FAIL_MSG));
            return;
        }

        String nonce=MapUtil.getStr(allParams,"nonce");
        if(StringUtils.isBlank(nonce)){
            log.info("随机数验证问题！");
            ResponseUtils.outJson(response, Result.failed(ResponseCodeConstants.SIGN_FAIL_CODE, ResponseCodeConstants.SIGN_FAIL_MSG));
        }

        String appId=MapUtil.getStr(allParams,"appId");
        if(StringUtils.isBlank(appId)){
            log.info("appId问题！");
            ResponseUtils.outJson(response, Result.failed(ResponseCodeConstants.SIGN_FAIL_CODE, ResponseCodeConstants.SIGN_FAIL_MSG));
        }
        if(signMapper==null)
            signMapper=(SignMapper) SpringUtils.getBean(SignMapper.class);
        Map<String,String> sign=signMapper.select(appId);
        if(sign==null){
            log.info("签名问题！");
            ResponseUtils.outJson(response, Result.failed(ResponseCodeConstants.SIGN_FAIL_CODE, ResponseCodeConstants.SIGN_FAIL_MSG));
        }

        //对参数进行签名验证
        boolean verifySign = SignUtils.verifySign(allParams, sign.get("secertId"));
        if (verifySign) {
            chain.doFilter(requestWrapper, response);
            return;
        } else {
            ResponseUtils.outJson(response, Result.failed(ResponseCodeConstants.SIGN_FAIL_CODE, ResponseCodeConstants.SIGN_FAIL_MSG));
        }

       super.doFilterInternal(request, response, chain);
    }

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}
