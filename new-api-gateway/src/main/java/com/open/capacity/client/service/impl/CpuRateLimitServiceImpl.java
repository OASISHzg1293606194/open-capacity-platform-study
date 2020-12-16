//package com.open.capacity.client.service.impl;
//
//import java.util.Objects;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.actuate.metrics.MetricsEndpoint;
//import org.springframework.stereotype.Service;
//
//import com.open.capacity.client.service.RateLimitService;
//
//import lombok.extern.slf4j.Slf4j;
//
///**
// *  程序名 : CpuRateLimitServiceImpl 
// *  建立日期: 2018-09-09 
// *  作者 : someday 
// *  模块 : 网关 
// *  描述 : 根据cpu限流
// *  version20180909001
// *  <p>
// *  修改历史 序号 日期 修改人 修改原因
// */
//@Slf4j
//@Service
//public class CpuRateLimitServiceImpl implements RateLimitService{
//	 
//	@Autowired
//	private MetricsEndpoint metricsEndpoint ;
//	
//	private static final String METRIC_NAME="system.cpu.usage";
//	
//	private static final Double MAX_USAGE = 0.99D;
//	
//	@Override
//	public boolean checkRateLimit(String reqUrl, String accessToken) {
//		try {
//
//
//			Double systemCpuUsage = metricsEndpoint.metric(METRIC_NAME, null)
//					.getMeasurements()
//					.stream()
//					.filter(Objects::nonNull)
//					.findFirst()
//					.map(MetricsEndpoint.Sample::getValue)
//					.filter(Double::isFinite)
//					.orElse(0.0D);
//
//			
//			boolean isNormal = systemCpuUsage < MAX_USAGE ;
//			log.trace("system.cpu.usage: {} , isNormal: {} " ,systemCpuUsage ,isNormal );
//			
//			return isNormal ;
//			
//        } catch (Exception e) {
//            StackTraceElement stackTraceElement = e.getStackTrace()[0];
//            log.error("checkRateLimit:" + "---|Exception:" + stackTraceElement.getLineNumber() + "----" + e.getMessage());
//        }
//
//        return true;
//    }
//
//}
