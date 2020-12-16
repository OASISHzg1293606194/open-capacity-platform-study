package com.sharding.demo.dao;

import com.sharding.demo.model.Company;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

 
@Mapper
public interface CompanyMapper {

	/**
	* 保存对象
	* @param info  保存对象
	* @return  返回成功保存对象数量
	*/
	int save(Company info);

	/**
	* 修改对象
	* @param info  待修改对象
	* @return  返回成功修改对象数量
	*/
	int update(Company info);

	/**
	* 删除对象
	* @param id  对象id
	* @return  返回成功删除数量
	*/
	int delete(int id);

	/**
	* 根据ID查询对象
	* @param id  对象id
	* @return  返回根据id查询的对象
	*/
	Company findById(int id);

	/**
	* 根据条件查询所有对象
	* @param params  查询参数map对象
	* @return  返回查询对象集合
	*/
	List<Map> findAll(Map<String, Object> params);

	/**
	* 根据条件查询所有对象数量
	* @param params  查询参数map对象
	* @return  返回根据条件查下对象的数量
	*/
	int findCount(Map<String, Object> params);

}