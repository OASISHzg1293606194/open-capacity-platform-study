package com.open.capacity.es.dao;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.open.capacity.es.entity.NinxLogDocument;

/**
 * ELK收集nginx中的日志查询接口
 */
@Repository
public interface NginxLogDao extends ElasticsearchRepository<NinxLogDocument, String> {

}