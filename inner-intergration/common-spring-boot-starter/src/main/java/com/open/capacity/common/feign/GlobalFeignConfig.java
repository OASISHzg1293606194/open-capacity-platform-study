package com.open.capacity.common.feign;

import org.springframework.context.annotation.Bean;

import feign.Logger.Level;

/**
 * blog: https://blog.51cto.com/13005375
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */
public class GlobalFeignConfig {

    @Bean
    public Level level() {
        return Level.FULL;
    }
}
