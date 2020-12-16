package com.open.capacity.es.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.open.capacity.es.entity.ServiceLogDocument;

/**
 * ELK收集ocp中info.info日志查询接口
 */
@Repository
public interface ServiceLogDao extends ElasticsearchRepository<ServiceLogDocument, String> {

}