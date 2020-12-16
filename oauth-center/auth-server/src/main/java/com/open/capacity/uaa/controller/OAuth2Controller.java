package com.open.capacity.uaa.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.open.capacity.common.auth.details.LoginAppUser;
import com.open.capacity.common.exception.controller.ControllerException;
import com.open.capacity.common.model.SysPermission;
import com.open.capacity.common.token.SmsCodeAuthenticationToken;
import com.open.capacity.common.util.ResponseUtil;
import com.open.capacity.common.util.SysUserUtil;
import com.open.capacity.common.web.PageResult;
import com.open.capacity.log.annotation.LogAnnotation;
import com.open.capacity.uaa.server.service.RedisClientDetailsService;
import com.open.capacity.uaa.service.SysTokenService;
import com.open.capacity.uaa.utils.SpringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen
 * @version 创建时间：2018年4月28日 下午2:18:54 类说明
 */
@Slf4j
@RestController
@Api(tags = "OAuth API")
@SuppressWarnings("all")
public class OAuth2Controller {

	@Autowired
	private SysTokenService sysTokenService;

	
	@ApiOperation(value = "clientId获取token")
	@PostMapping("/oauth/client/token")
	@LogAnnotation(module = "auth-server", recordRequestParam = false)
	public void getClientTokenInfo() {

		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		HttpServletResponse response = servletRequestAttributes.getResponse();

		try {
			String clientId = request.getHeader("client_id");
			String clientSecret = request.getHeader("client_secret");
			OAuth2AccessToken oAuth2AccessToken = sysTokenService.getClientTokenInfo(clientId, clientSecret);

			ResponseUtil.renderJson(response, oAuth2AccessToken);

		} catch (Exception e) {

			Map<String, String> rsp = new HashMap<>();
			rsp.put("code", HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());

			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());

		}
	}
	
	@ApiOperation(value = "用户名密码获取token")
	@PostMapping("/oauth/user/token")
	@LogAnnotation(module = "auth-server", recordRequestParam = false)
	public void getUserTokenInfo(
			@ApiParam(required = true, name = "username", value = "账号") @RequestParam(value = "username") String username,
			@ApiParam(required = true, name = "password", value = "密码") @RequestParam(value = "password") String password) {

		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		HttpServletResponse response = servletRequestAttributes.getResponse();
		try {

			String clientId = request.getHeader("client_id");
			String clientSecret = request.getHeader("client_secret");

			OAuth2AccessToken oAuth2AccessToken = sysTokenService.getUserTokenInfo(clientId, clientSecret, username,
					password);

			ResponseUtil.renderJson(response, oAuth2AccessToken);

		} catch (Exception e) {

			Map<String, String> rsp = new HashMap<>();
			rsp.put("code", HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());
			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());

		}
	}


	@PostMapping("/authentication/sms")
	public void getMobileInfo(
			@ApiParam(required = true, name = "deviceId", value = "手机号") @RequestParam(value = "deviceId") String deviceId,
			@ApiParam(required = true, name = "validCode", value = "验证码") @RequestParam(value = "validCode", required = false) String validCode) {

		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		HttpServletResponse response = servletRequestAttributes.getResponse();

		try {
			String clientId = request.getHeader("client_id");
			String clientSecret = request.getHeader("client_secret");
			OAuth2AccessToken oAuth2AccessToken = sysTokenService.getMobileTokenInfo(clientId, clientSecret, deviceId,
					validCode);

			ResponseUtil.renderJson(response, oAuth2AccessToken);

		} catch (Exception e) {

			Map<String, String> rsp = new HashMap<>();
			rsp.put("code", HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());

			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());
		}
	}
	
	@ApiOperation(value = "access_token刷新token")
	@PostMapping(value = "/oauth/refresh/token", params = "access_token")
	@LogAnnotation(module = "auth-server", recordRequestParam = false)
	public void refreshTokenInfo(String access_token) {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = servletRequestAttributes.getRequest();
		HttpServletResponse response = servletRequestAttributes.getResponse();

		try {

			OAuth2AccessToken oAuth2AccessToken = sysTokenService.getRefreshTokenInfo(access_token);

			ResponseUtil.renderJson(response, oAuth2AccessToken);

		} catch (Exception e) {
			Map<String, String> rsp = new HashMap<>();
			rsp.put("code", HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("msg", e.getMessage());
			ResponseUtil.renderJsonError(response, rsp, HttpStatus.UNAUTHORIZED.value());
		}

	}

	/**
	 * 移除access_token和refresh_token
	 * 
	 * @param access_token
	 */
	@ApiOperation(value = "移除token")
	@PostMapping(value = "/oauth/remove/token", params = "access_token")
	@LogAnnotation(module = "auth-server", recordRequestParam = false)
	public void removeToken(String access_token) {

		try {
			sysTokenService.removeToken(access_token);
		} catch (Exception e) {
			throw new ControllerException(e);
		}
	}

	@ApiOperation(value = "获取token信息")
	@PostMapping(value = "/oauth/get/token", params = "access_token")
	@LogAnnotation(module = "auth-server", recordRequestParam = false)
	public OAuth2AccessToken getTokenInfo(String access_token) {

		try {
			return sysTokenService.getTokenInfo(access_token);
		} catch (Exception e) {
			throw new ControllerException(e);
		}

	}

	/**
	 * 当前登陆用户信息
	 * security获取当前登录用户的方法是SecurityContextHolder.getContext().getAuthentication()
	 * 这里的实现类是org.springframework.security.oauth2.provider.OAuth2Authentication
	 * 
	 * @return
	 */
	@ApiOperation(value = "当前登陆用户信息")
	@GetMapping(value = { "/oauth/userinfo" }, produces = "application/json") // 获取用户信息。/auth/user
	@LogAnnotation(module = "auth-server", recordRequestParam = false)
	public Map<String, Object> getCurrentUserDetail() {
		try {
			Map<String, Object> userInfo = new HashMap<>();
			userInfo.put("code", "0");
			LoginAppUser loginUser = SysUserUtil.getLoginAppUser();
			userInfo.put("user", loginUser);
			List<SysPermission> permissions = new ArrayList<>();
			new ArrayList(loginUser.getAuthorities()).forEach(o -> {
				SysPermission sysPermission = new SysPermission();
				sysPermission.setPermission(o.toString());
				permissions.add(sysPermission);
			});
			// userInfo.put("authorities",
			// AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
			// );
			userInfo.put("permissions", permissions);
			return userInfo;
		} catch (Exception e) {
			throw new ControllerException(e);
		}
	}

	@ApiOperation(value = "token列表")
	@PostMapping("/oauth/token/list")
	@LogAnnotation(module = "auth-server", recordRequestParam = false)
	public PageResult<Map<String, String>> getTokenList(@RequestParam Map<String, Object> params) throws Exception {

		try {
			return sysTokenService.getTokenList(params);
		} catch (Exception e) {
			throw new ControllerException(e);
		}

	}

	

}
