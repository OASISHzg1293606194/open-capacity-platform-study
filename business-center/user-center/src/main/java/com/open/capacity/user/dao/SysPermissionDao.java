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
import com.open.capacity.common.model.SysPermission;

/**
* @author 作者 owen 
* @version 创建时间：2017年11月12日 上午22:57:51
 * 权限
 */
@Mapper
public interface SysPermissionDao  extends BaseMapper<SysPermission> {

	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into sys_permission(permission, name, create_time, update_time) values(#{permission}, #{name}, #{createTime}, #{createTime})")
	int save(SysPermission sysPermission);

	@Delete("delete from sys_permission where id = #{id}")
	int deleteByPrimaryKey(Long id);
	
	@Update("update sys_permission t set t.name = #{name}, t.permission = #{permission}, t.update_time = #{updateTime} where t.id = #{id}")
	int updateByPrimaryKey(SysPermission sysPermission);

	
	@Select("select id, permission, name, create_time createTime , update_time updateTime from sys_permission t where t.id = #{id}")
	SysPermission findById(Long id);

	@Select("select id, permission, name, create_time createTime , update_time updateTime from sys_permission t where t.permission = #{permission}")
	SysPermission findByPermission(String permission);

	int count(Map<String, Object> params);

	List<SysPermission> findList(Map<String, Object> params);

}
