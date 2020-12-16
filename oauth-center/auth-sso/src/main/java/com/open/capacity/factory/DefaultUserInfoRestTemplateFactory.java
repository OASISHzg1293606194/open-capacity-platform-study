//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.open.capacity.factory;

import com.open.capacity.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateCustomizer;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.RequestEnhancer;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 默认不支持https请求，需要重写UserInfoRestTemplateFactory
 * 并设置SslVerificationHttpRequestFactory
 */
public class DefaultUserInfoRestTemplateFactory implements UserInfoRestTemplateFactory {
	private static final AuthorizationCodeResourceDetails DEFAULT_RESOURCE_DETAILS;
	private final List<UserInfoRestTemplateCustomizer> customizers;
	private final OAuth2ProtectedResourceDetails details;
	private final OAuth2ClientContext oauth2ClientContext;
	private OAuth2RestTemplate oauth2RestTemplate;

	public DefaultUserInfoRestTemplateFactory(ObjectProvider<List<UserInfoRestTemplateCustomizer>> customizers, ObjectProvider<OAuth2ProtectedResourceDetails> details, ObjectProvider<OAuth2ClientContext> oauth2ClientContext) {
		this.customizers = (List)customizers.getIfAvailable();
		this.details = (OAuth2ProtectedResourceDetails)details.getIfAvailable();
		this.oauth2ClientContext = (OAuth2ClientContext)oauth2ClientContext.getIfAvailable();
	}

	public OAuth2RestTemplate getUserInfoRestTemplate() {
		if (this.oauth2RestTemplate == null) {
			this.oauth2RestTemplate = this.createOAuth2RestTemplate((OAuth2ProtectedResourceDetails)(this.details == null ? DEFAULT_RESOURCE_DETAILS : this.details));
			this.oauth2RestTemplate.getInterceptors().add(new AcceptJsonRequestInterceptor());
			AuthorizationCodeAccessTokenProvider accessTokenProvider = new AuthorizationCodeAccessTokenProvider();
			accessTokenProvider.setTokenRequestEnhancer(new AcceptJsonRequestEnhancer());
			CloseableHttpClient httpClient = HttpClientUtils.getCloseableHttpClient();
			HttpComponentsClientHttpRequestFactory httpsFactory =
					new HttpComponentsClientHttpRequestFactory(httpClient);
			httpsFactory.setReadTimeout(40000);
			httpsFactory.setConnectTimeout(40000);
			accessTokenProvider.setRequestFactory(httpsFactory);
			this.oauth2RestTemplate.setAccessTokenProvider(accessTokenProvider);
			if (!CollectionUtils.isEmpty(this.customizers)) {
				AnnotationAwareOrderComparator.sort(this.customizers);
				Iterator var2 = this.customizers.iterator();

				while(var2.hasNext()) {
					UserInfoRestTemplateCustomizer customizer = (UserInfoRestTemplateCustomizer)var2.next();
					customizer.customize(this.oauth2RestTemplate);
				}
			}
		}
//        else {
//            oauth2RestTemplate.setRequestFactory(sslRequestFactory);
//        }

		return this.oauth2RestTemplate;
	}

	private OAuth2RestTemplate createOAuth2RestTemplate(OAuth2ProtectedResourceDetails details) {
		return this.oauth2ClientContext == null ? new OAuth2RestTemplate(details) : new OAuth2RestTemplate(details, this.oauth2ClientContext);
	}

	static {
		AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
		details.setClientId("<N/A>");
		details.setUserAuthorizationUri("Not a URI because there is no client");
		details.setAccessTokenUri("Not a URI because there is no client");
		DEFAULT_RESOURCE_DETAILS = details;
	}

	static class AcceptJsonRequestEnhancer implements RequestEnhancer {
		AcceptJsonRequestEnhancer() {
		}

		public void enhance(AccessTokenRequest request, OAuth2ProtectedResourceDetails resource, MultiValueMap<String, String> form, HttpHeaders headers) {
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		}
	}

	static class AcceptJsonRequestInterceptor implements ClientHttpRequestInterceptor {
		AcceptJsonRequestInterceptor() {
		}

		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
			request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			return execution.execute(request, body);
		}
	}
}
