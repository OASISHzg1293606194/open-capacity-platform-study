/**
 * 
 */
package com.open.capacity.uaa.client.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

/** 
* @author 作者 owen 
* @version 创建时间：2018年4月5日 下午19:52:21
* 类说明     适用于zuul网关  
* 应用服务API接口
* blog: https://blog.51cto.com/13005375 
* code: https://gitee.com/owenwangwen/open-capacity-platform
*/
public interface RbacService {
	
	boolean hasPermission(HttpServletRequest request, Authentication authentication);

}
