package com.open.capacity.common.feign;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.capacity.common.constant.TraceConstant;
import com.open.capacity.common.constant.UaaConstant;
import com.open.capacity.common.util.StringUtil;
import com.open.capacity.common.util.TokenUtil;

import feign.RequestInterceptor;

/**
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51
 * feign拦截器
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Configuration
public class FeignInterceptorConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        RequestInterceptor requestInterceptor = template -> {
            //传递token
            //使用feign client访问别的微服务时，将accessToken header
            //config.anyRequest().permitAll() 非强制校验token
            if (StringUtil.isNotBlank(TokenUtil.getToken())) {
            	template.header(UaaConstant.TOKEN_HEADER, TokenUtil.getToken());
//            	template.header(UaaConstant.AUTHORIZATION,  OAuth2AccessToken.BEARER_TYPE  +  " "  +  TokenUtil.getToken() );
            }
            //传递traceId
            String traceId = StringUtil.isNotBlank(MDC.get(TraceConstant.LOG_TRACE_ID)) ? MDC.get(TraceConstant.LOG_TRACE_ID) : MDC.get(TraceConstant.LOG_B3_TRACEID);
            if (StringUtil.isNotBlank(traceId)) {
                template.header(TraceConstant.HTTP_HEADER_TRACE_ID, traceId);
            }


        };

        return requestInterceptor;
    }
}
