package com.open.capacity.client.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.open.capacity.client.dao.SysClientDao;
import com.open.capacity.client.dao.SysServiceDao;
import com.open.capacity.client.service.SysClientService;
import com.open.capacity.common.constant.UaaConstant;
import com.open.capacity.common.exception.service.ServiceException;
import com.open.capacity.common.model.SysClient;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 作者 owen
 * @version 创建时间：2018年4月5日 下午19:52:21
 *  类说明 查询应用绑定的资源权限 
 * blog:https://blog.51cto.com/13005375 
 * code:https://gitee.com/owenwangwen/open-capacity-platform
 */
@Slf4j
@SuppressWarnings("all")
@Service("sysClientService")
public class SysClientServiceImpl implements SysClientService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private SysClientDao sysClientDao;

	@Autowired
	private SysServiceDao sysServiceDao;

	public Map getClient(String clientId) {
		// 先从redis获取
		Map client = null;
		String value = (String) redisTemplate.boundHashOps(UaaConstant.CACHE_CLIENT_KEY).get(clientId);
		// 没有从数据库获取并缓存
		if (StringUtils.isBlank(value)) {
			client = cacheAndGetClient(clientId);
		} else {
			client = JSONObject.parseObject(value, Map.class);
		}
		return client;
	}

	private Map cacheAndGetClient(String clientId) {
		
		Map client = null;
		try {
			client = sysClientDao.getClient(clientId);	// 从数据库读取
			if (client != null) {
				SysClient sysClient = BeanUtil.toBean(client, SysClient.class);
				// 写入redis缓存
				redisTemplate.boundHashOps(UaaConstant.CACHE_CLIENT_KEY).put(clientId,
						JSONObject.toJSONString(sysClient.map())); //缓存
				log.info("缓存clientId:{},{}", clientId, sysClient);
			}
		} catch (Exception e) {
			throw new ServiceException("应用状态不合法") ;
		}

		return client;
	}

	@Cacheable(value = "service", key = "#clientId")
	public List<Map> listByClientId(Long clientId) {
		return sysServiceDao.listByClientId(clientId);
	}

}
