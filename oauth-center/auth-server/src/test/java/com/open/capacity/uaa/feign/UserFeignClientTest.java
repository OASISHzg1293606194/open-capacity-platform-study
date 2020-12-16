package com.open.capacity.uaa.feign;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Maps;
import com.open.capacity.AuthServerApp;
import com.open.capacity.common.model.SysUser;
import com.open.capacity.common.web.PageResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { AuthServerApp.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 配置启动类
public class UserFeignClientTest {

	@Autowired
	private UserFeignClient userFeignClient ;
	@Test
	public void test() {
		Map params = Maps.newHashMap() ;
		params.put("page", "1");
		params.put("limit", "5");
		params.put("searchKey", "username" );
		params.put("searchValue", "test");
		
		PageResult<SysUser> result =   userFeignClient.findUsers(params);
		
		System.out.println(result.getCode());
		System.out.println(result.getCount());
		System.out.println(Arrays.asList(result.getData()));
	}

}
