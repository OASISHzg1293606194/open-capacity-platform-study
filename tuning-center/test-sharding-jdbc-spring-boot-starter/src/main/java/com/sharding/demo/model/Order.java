package com.sharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class Order {
    private String orderId;
    private String orderName;
    private Date createTime;
}
