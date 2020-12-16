package com.open.capacity.log.selector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author owen
 * log-spring-boot-starter 自动装配 
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */


public class LogImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		
		return new String[] { 
				"com.open.capacity.log.aop.LogAnnotationAOP",
//				"com.open.capacity.log.config.SentryAutoConfig",
				"com.open.capacity.log.service.impl.LogServiceImpl",
				"com.open.capacity.log.config.LogAutoConfig"
				
		};
	}

}