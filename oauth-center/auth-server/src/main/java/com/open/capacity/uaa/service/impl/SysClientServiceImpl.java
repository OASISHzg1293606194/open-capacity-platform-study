package com.open.capacity.uaa.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.open.capacity.common.constant.UaaConstant;
import com.open.capacity.common.exception.service.ServiceException;
import com.open.capacity.common.model.SysClient;
import com.open.capacity.common.web.PageResult;
import com.open.capacity.common.web.Result;
import com.open.capacity.uaa.dao.SysClientDao;
import com.open.capacity.uaa.dao.SysClientServiceDao;
import com.open.capacity.uaa.service.SysClientService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("all")
public class SysClientServiceImpl implements SysClientService {


    @Autowired
    private SysClientDao sysClientDao;

    @Autowired
    private SysClientServiceDao sysClientServiceDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
  
    @Autowired
    private JdbcClientDetailsService jdbcClientDetailsService ;



     
    @Override
    public Result saveOrUpdate(SysClient sysClient) {
        try {
			sysClient.setClientSecret(passwordEncoder.encode(sysClient.getClientSecretStr()));

			if (sysClient.getId() != null) {// 修改
			    sysClientDao.updateByPrimaryKey(sysClient);
			} else {// 新增
				SysClient r = sysClientDao.getClient(sysClient.getClientId());
			    if (r != null) {
			        return Result.failed(sysClient.getClientId()+"已存在");
			    }
			    sysClientDao.save(sysClient);
			}
			return Result.succeed("操作成功");
		} catch (Exception e) {
			throw new ServiceException(e);
		}
    }

     

    @Override
    @Transactional
    public void delete(Long id) {
        try {
        	SysClient client = sysClientDao.getById(id);
			sysClientDao.delete(id);
			sysClientServiceDao.delete(id,null);
			redisTemplate.boundHashOps(UaaConstant.CACHE_CLIENT_KEY).delete(client.map().getClientId()) ;
			log.debug("删除应用id:{}", id);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
    }

	@Override
	public PageResult<SysClient> list(Map<String, Object> params) {

        try {
			//设置分页信息，分别是当前页数和每页显示的总记录数【记住：必须在mapper接口中的方法执行之前设置该分页信息】
			PageHelper.startPage(MapUtils.getInteger(params, "page"),MapUtils.getInteger(params, "limit"),true);
			List<SysClient> list = sysClientDao.findList(params);
			PageInfo<SysClient> pageInfo = new PageInfo<>(list);
			return PageResult.<SysClient>builder().data(pageInfo.getList()).code(0).count(pageInfo.getTotal()).build()  ;
		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}
	public  SysClient getById(Long id) {
		try {
			return sysClientDao.getById(id);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public List<SysClient> findList(Map<String, Object> params) {
		return sysClientDao.findList(params);
	}



	@Override
	public Result updateEnabled(Map<String, Object> params) {
		try {
			Long id = MapUtils.getLong(params, "id");
			Boolean enabled = MapUtils.getBoolean(params, "status");
			SysClient client = sysClientDao.getById(id);
			if (client == null) {
				return Result.failed("应用不存在");
				//throw new IllegalArgumentException("用户不存在");
			}
			client.setStatus(enabled);

			int i = sysClientDao.updateByPrimaryKey(client) ;
			
			ClientDetails clientDetails = client.map();
			
			if(enabled){
				redisTemplate.boundHashOps(UaaConstant.CACHE_CLIENT_KEY).put(client.getClientId(), JSONObject.toJSONString(clientDetails));
			}else{
				redisTemplate.boundHashOps(UaaConstant.CACHE_CLIENT_KEY).delete(client.getClientId()) ;
			}
			
			log.info("应用状态修改：{}", client);

			return i > 0 ? Result.succeed(client, "更新成功") : Result.failed("更新失败");
		} catch (InvalidClientException e) {
			throw new ServiceException(e);
		}
	}

}
