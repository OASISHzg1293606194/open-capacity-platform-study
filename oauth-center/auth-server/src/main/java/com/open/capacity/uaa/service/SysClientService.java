package com.open.capacity.uaa.service;

import java.util.List;
import java.util.Map;

import com.open.capacity.common.model.SysClient;
import com.open.capacity.common.web.PageResult;
import com.open.capacity.common.web.Result;

@SuppressWarnings("all")
public interface SysClientService {

	
	Result saveOrUpdate(SysClient clientDto);
	
	void delete(Long id);
	
	Result updateEnabled(Map<String, Object> params);
	
	SysClient getById(Long id) ;

  
    
    public PageResult<SysClient> list(Map<String, Object> params);
    
    List<SysClient> findList(Map<String, Object> params) ;
    

	
    
}
