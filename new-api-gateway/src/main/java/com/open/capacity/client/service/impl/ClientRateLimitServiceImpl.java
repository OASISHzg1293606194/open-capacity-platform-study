package com.open.capacity.client.service.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.open.capacity.client.service.RateLimitService;
import com.open.capacity.client.service.SysClientService;
import com.open.capacity.client.utils.RedisLimiterUtils;
import com.open.capacity.common.util.StringUtil;
import com.open.capacity.common.web.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 程序名 : ClientRateLimitServiceImpl 建立日期: 2018-09-09 作者 : someday 模块 : 网关 描述 :
 * 根据应用限流 version20180909001
 * <p>
 * 修改历史 序号 日期 修改人 修改原因
 */
@Slf4j
@Service
public class ClientRateLimitServiceImpl implements RateLimitService {
	// url匹配器
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Autowired
	private RedisLimiterUtils redisLimiterUtils;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TokenStore tokenStore;

	@Resource
	SysClientService sysClientService;

	/**
	 * 1. 判断token是否有效 2. 如果token有对应clientId 2.1 判断clientId是否有效 2.2
	 * 判断请求的服务service是否有效 2.3 判断clientId是否有权限访问service 3. 判断 clientId+service
	 * 每日限流
	 *
	 * @param exchange
	 * @param accessToken
	 * @return
	 */
	@Override
	public boolean checkRateLimit(String reqUrl, String accessToken) {
		try {

			if (StringUtil.isNotBlank(accessToken)) {
				// 1. 按accessToken查找对应的clientId
				OAuth2Authentication oauth2Authentication = tokenStore.readAuthentication(accessToken);
				if (oauth2Authentication != null) {
					String clientId = oauth2Authentication.getOAuth2Request().getClientId();
					// 根据应用 url 限流
					// oauth_client_details if_limit 限流开关
					// limit_count 阈值
					Map client = sysClientService.getClient(clientId);
					if (client != null) {
						String flag = MapUtils.getString(client, "ifLimit");

						if ("1".equals(flag)) {
							String accessLimitCount = MapUtils.getString(client, "limitCount");
							if (StringUtil.isNotBlank(accessLimitCount)) {
								Result result = redisLimiterUtils.rateLimitOfDay(clientId, reqUrl,
										Long.parseLong(accessLimitCount));
								if (-1 == result.getCode()) {
									log.trace("token: {} , limitCount: {} , desc: {} " ,accessToken ,accessLimitCount ,result.getMsg() ); 
									return false;
								}
							}
						}
					}

				}
			}
		} catch (Exception e) {
			StackTraceElement stackTraceElement = e.getStackTrace()[0];
			log.error(
					"checkRateLimit:" + "---|Exception:" + stackTraceElement.getLineNumber() + "----" + e.getMessage());
		}

		return true;
	}

}
