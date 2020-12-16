package com.open.capacity.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.capacity.common.model.SysMenu;
/**
* @author 作者 owen 
* @version 创建时间：2017年11月13日 上午22:57:51
 * 菜单
 */
@Mapper
public interface SysMenuDao extends BaseMapper<SysMenu> {

	@Insert("insert into sys_menu(parent_id, name, url, path, css, sort, create_time, update_time,is_menu,hidden) "
			+ "values (#{parentId}, #{name}, #{url} , #{path} , #{css}, #{sort}, #{createTime}, #{updateTime},#{isMenu},#{hidden})")
	int save(SysMenu menu);

	
	@Delete("delete from sys_menu where id = #{id}")
	int deleteByPrimaryKey(Long id);

	@Delete("delete from sys_menu where parent_id = #{id}")
	int deleteByParentId(Long id);
	
	int updateByPrimaryKey(SysMenu menu);

	@Select("select id ,parent_id parentId , name, url, path, css, sort, create_time createTime , update_time updateTime ,is_menu isMenu,hidden from sys_menu t where t.id = #{id}")
	SysMenu findById(Long id);
	
	int count(Map<String, Object> params);
	
	List<SysMenu> findList(Map<String, Object> params);
	
	
	
}
