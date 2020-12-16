package com.open.capacity.client.service;

public interface RateLimitService {

	public boolean checkRateLimit(String reqUrl, String accessToken) ;
	
}
