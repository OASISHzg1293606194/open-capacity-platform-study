package com.sharding.demo.config;

import java.math.BigDecimal;
import java.util.Collection;

import com.alibaba.fastjson.JSON;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyPreciseDBShardingAlgorithm implements PreciseShardingAlgorithm<BigDecimal> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<BigDecimal> shardingValue) {
        log.info("Database: {}, companyShardingValue: {}",  JSON.toJSONString(availableTargetNames), JSON.toJSONString(shardingValue));
        return availableTargetNames.stream()
                .filter(t -> {
                	boolean flag =  t.endsWith(Long.valueOf(shardingValue.getValue()+"") % availableTargetNames.size()+"") ;
                	return flag ;
                })
                .findFirst()
                .orElse(null);
    }
}
