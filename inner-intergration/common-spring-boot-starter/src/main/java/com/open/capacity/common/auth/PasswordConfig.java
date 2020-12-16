package com.open.capacity.common.auth;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** 
* @author 作者 owen 
* @version 创建时间：2017年11月12日 上午22:57:51
* 装配密码匹配器
* blog: https://blog.51cto.com/13005375 
* code: https://gitee.com/owenwangwen/open-capacity-platform
*/
@Configuration
public class PasswordConfig {
	@Bean
	public PasswordEncoder passwordEncoder()	{
		return new BCryptPasswordEncoder();
	}
}
