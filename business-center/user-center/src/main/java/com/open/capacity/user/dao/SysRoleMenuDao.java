package com.open.capacity.user.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.capacity.common.model.SysMenu;
import com.open.capacity.common.model.SysRoleMenu;

/**
* @author 作者 owen 
* @version 创建时间：2017年11月13日 上午22:57:51
 * 角色菜单
 */
@Mapper
public interface SysRoleMenuDao  extends BaseMapper<SysRoleMenu> {

	@Insert("insert into sys_role_menu(role_id, menu_id) values(#{roleId}, #{menuId})")
	int save( SysRoleMenu menu );

	
	void saveBatch(@Param("roleId") Long roleId, @Param("menuIds") Set<Long> menuIds);
	
	int deleteBySelective(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

	@Select("select t.menu_id menuId from sys_role_menu t where t.role_id = #{roleId}")
	Set<Long> findMenuIdsByRoleId(Long roleId);

	List<SysMenu> findMenusByRoleIds(@Param("roleIds") Set<Long> roleIds);

	
}
