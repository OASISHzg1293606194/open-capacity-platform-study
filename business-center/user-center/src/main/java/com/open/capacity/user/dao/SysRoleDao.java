package com.open.capacity.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.capacity.common.model.SysMenu;
import com.open.capacity.common.model.SysRole;

/**
* @author 作者 owen 
* @version 创建时间：2017年11月12日 上午22:57:51
 * 角色
 */
@Mapper
public interface SysRoleDao extends BaseMapper<SysRole>{

	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into sys_role(code, name, create_time, update_time) values(#{code}, #{name}, #{createTime}, #{createTime})")
	int save(SysRole sysRole);

	
	@Delete("delete from sys_role where id = #{id}")
	int deleteByPrimaryKey(Long id);
	
	@Update("update sys_role t set t.name = #{name} ,t.update_time = #{updateTime} where t.id = #{id}")
	int updateByPrimaryKey(SysRole sysRole);

	@Select("select id ,code, name, create_time createTime , update_time  updateTime from sys_role t where t.id = #{id}")
	SysRole findById(Long id);

	@Select("select id ,code, name, create_time createTime , update_time  updateTime from sys_role t where t.code = #{code}")
	SysRole findByCode(String code);

	int count(Map<String, Object> params);

	List<SysRole> findList(Map<String, Object> params);

}
