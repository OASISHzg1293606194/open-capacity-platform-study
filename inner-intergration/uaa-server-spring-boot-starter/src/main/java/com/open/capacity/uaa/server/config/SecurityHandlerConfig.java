package com.open.capacity.uaa.server.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedResponseTypeException;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.uaa.server.handle.OauthLogoutHandler;

/**
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
@Component
@Configuration
@SuppressWarnings("all") 
public class SecurityHandlerConfig {

	@Resource
	private ObjectMapper objectMapper; // springmvc启动时自动装配json处理类

	// url匹配器
//	private AntPathMatcher pathMatcher = new AntPathMatcher();

	/**
	 * 登陆成功，返回Token 装配此bean不支持授权码模式
	 * 
	 * @return
	 */
	@Bean
	public AuthenticationSuccessHandler loginSuccessHandler() {
		return new SavedRequestAwareAuthenticationSuccessHandler() {

			private RequestCache requestCache = new HttpSessionRequestCache();

			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {

				super.onAuthenticationSuccess(request, response, authentication);
				return;

			}
		};
	}

	/**
	 * 登陆失败
	 * 
	 * @return
	 */
	@Bean
	public AuthenticationFailureHandler loginFailureHandler() {
		return new AuthenticationFailureHandler() {

			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				String msg = null;
				if (exception instanceof BadCredentialsException) {
					msg = "密码错误";
				} else {
					msg = exception.getMessage();
				}

				Map<String, String> rsp = new HashMap<>();

				response.setStatus(HttpStatus.UNAUTHORIZED.value());

				rsp.put("code", HttpStatus.UNAUTHORIZED.value() + "");
				rsp.put("msg", msg);

				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().write(objectMapper.writeValueAsString(rsp));
				response.getWriter().flush();
				response.getWriter().close();

			}
		};

	}

	
	@Bean
	public WebResponseExceptionTranslator webResponseExceptionTranslator() {
		return new DefaultWebResponseExceptionTranslator() {

			public static final String BAD_MSG = "Bad credentials";

			@Override
			public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
				// e.printStackTrace();
				OAuth2Exception oAuth2Exception;
				if (e.getMessage() != null && BAD_MSG.equals( e.getMessage())) {
					oAuth2Exception = new InvalidGrantException("用户名或密码错误", e);
				}else if (e instanceof InvalidGrantException) {
					oAuth2Exception = new InvalidGrantException(e.getMessage(), e);
				}else if (e instanceof InternalAuthenticationServiceException) {
					oAuth2Exception = new InvalidGrantException(e.getMessage(), e);
				} else if (e instanceof RedirectMismatchException) {
					oAuth2Exception = new InvalidGrantException(e.getMessage(), e);
				} else if (e instanceof InvalidScopeException) {
					oAuth2Exception = new InvalidGrantException(e.getMessage(), e);
				} else {
					oAuth2Exception = new UnsupportedResponseTypeException("服务内部错误", e);
				}

				ResponseEntity<OAuth2Exception> response = super.translate(oAuth2Exception);
				ResponseEntity.status(oAuth2Exception.getHttpErrorCode());
				response.getBody().addAdditionalInformation("code", oAuth2Exception.getHttpErrorCode() + "");
				response.getBody().addAdditionalInformation("msg", oAuth2Exception.getMessage());

				return response;
			}

		};
	}
	
	@Bean
    public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(ApplicationContext applicationContext) {
        OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }
	
	@Bean
	public OauthLogoutHandler oauthLogoutHandler() {
		return new OauthLogoutHandler();
	}
	 

}
