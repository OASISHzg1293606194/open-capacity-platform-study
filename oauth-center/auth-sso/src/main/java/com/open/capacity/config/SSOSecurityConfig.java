package com.open.capacity.config;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.open.capacity.common.interceptor.RestTemplateInterceptor;
import com.open.capacity.factory.DefaultUserInfoRestTemplateFactory;
import com.open.capacity.utils.HttpClientUtils;

/**
 * @author 作者 owen
 * @version 创建时间：2017年11月12日 上午22:57:51 类说明
 * @EnableOAuth2Sso注解。如果WebSecurityConfigurerAdapter类上注释了@EnableOAuth2Sso注解， 那么将会添加身份验证过滤器和身份验证入口。
 *                                                                           如果只有一个@EnableOAuth2Sso注解没有编写在WebSecurityConfigurerAdapter上，
 *                                                                           那么它将会为所有路径启用安全，并且会在基于HTTP
 *                                                                           Basic认证的安全链之前被添加。详见@EnableOAuth2Sso的注释。
 */
@Component
@Configuration
@EnableOAuth2Sso
public class SSOSecurityConfig extends WebSecurityConfigurerAdapter {

	private final String XSRF_TOKEN = "XSRF-TOKEN";
	private final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
	private final ResourceServerProperties resource;

	protected SSOSecurityConfig(ResourceServerProperties resource) {
		this.resource = resource;
	}

	
	@Bean
	@Primary
	public RemoteTokenServices remoteTokenServices() {
		RestTemplate restTemplate = new RestTemplate();
		CloseableHttpClient httpClient = HttpClientUtils.getCloseableHttpClient();
		HttpComponentsClientHttpRequestFactory httpsFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		httpsFactory.setReadTimeout(40000);
		httpsFactory.setConnectTimeout(40000);
		restTemplate.setRequestFactory(httpsFactory);
		RemoteTokenServices services = new RemoteTokenServices();
		services.setRestTemplate(restTemplate);
		services.setCheckTokenEndpointUrl(this.resource.getTokenInfoUri());
		services.setClientId(this.resource.getClientId());
		services.setClientSecret(this.resource.getClientSecret());
		return services;
	}

	 

	@Bean
	@Primary
	public UserInfoRestTemplateFactory userInfoRestTemplateFactory(
			ObjectProvider<List<UserInfoRestTemplateCustomizer>> customizers,
			ObjectProvider<OAuth2ProtectedResourceDetails> details,
			ObjectProvider<OAuth2ClientContext> oauth2ClientContext) {
		return new DefaultUserInfoRestTemplateFactory(customizers, details, oauth2ClientContext);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/dashboard/**").authorizeRequests().anyRequest().authenticated().and().csrf()
				.csrfTokenRepository(csrfTokenRepository()).and().addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
				.logout().logoutUrl("/dashboard/logout").permitAll().logoutSuccessUrl("/");
	}

	private Filter csrfHeaderFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
					FilterChain filterChain) throws ServletException, IOException {
				CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
				if (csrf != null) {
					Cookie cookie = new Cookie(XSRF_TOKEN, csrf.getToken());
					cookie.setPath("/");
					response.addCookie(cookie);
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName(X_XSRF_TOKEN);
		return repository;
	}

}
