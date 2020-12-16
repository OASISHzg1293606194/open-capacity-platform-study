package com.sharding.demo.config;

import com.alibaba.fastjson.JSON;
import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * 定义用户精确分片策略, 规则可以自定义
 */
@Slf4j
public class UserPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {
    @Override
    public String doSharding(Collection<String>availableTargetNames, PreciseShardingValue<Long>shardingValue) {
        log.info("Tables: {}, preciseValue: {}",  JSON.toJSONString(availableTargetNames), JSON.toJSONString(shardingValue));
        return availableTargetNames.stream()
                .filter(t -> t.endsWith(shardingValue.getValue() % availableTargetNames.size()+""))
                .findFirst()
                .orElse(null);
    }
}
