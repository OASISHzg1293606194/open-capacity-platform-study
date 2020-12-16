package com.open.capacity.client.entity;

import lombok.Getter;

import java.util.Date;

@Getter
public class GatewayRoutes {
    private String id;

    private String uri;

    private String predicates;

    private String filters;

    private Integer order;

    private String description;

    private Integer delFlag;

    private Date createTime;

    private Date updateTime;


    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }


    public void setUri(String uri) {
        this.uri = uri == null ? null : uri.trim();
    }


    public void setPredicates(String predicates) {
        this.predicates = predicates == null ? null : predicates.trim();
    }


    public void setFilters(String filters) {
        this.filters = filters == null ? null : filters.trim();
    }


    public void setOrder(Integer order) {
        this.order = order == null ? 0 : order;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    /**
     * 一般delFlag==1表示删除，delFlag==0表示存在
     */
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag == null ? 0 : delFlag;
    }


    public void setCreateTime(Date createTime) {
        this.createTime = createTime == null ? new Date(System.currentTimeMillis()) : createTime;
    }


    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime == null ? new Date(System.currentTimeMillis()) : updateTime;
    }
}