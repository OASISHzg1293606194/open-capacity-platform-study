package com.open.capacity.user.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.open.capacity.common.model.SysRole;

 
/**
* @author 作者 owen 
* @version 创建时间：2017年11月12日 上午22:57:51
* 用户角色关系
 */
@Mapper
public interface SysUserRoleDao {

	int deleteUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

	@Insert("insert into sys_role_user(user_id, role_id) values(#{userId}, #{roleId})")
	int saveUserRoles(@Param("userId") Long userId, @Param("roleId") Long roleId);
	/**
	 * 根据用户id获取角色
	 * 
	 * @param userId
	 * @return
	 */
	@Select("select r.id ,r.code, r.name, r.create_time createTime , r.update_time  updateTime   from sys_role_user ru inner join sys_role r on r.id = ru.role_id where ru.user_id = #{userId}")
	Set<SysRole> findRolesByUserId(Long userId);

	/**
	 *  根据用户ids 获取
	 * @param userIds
	 * @return
	 */
 
	@Select("<script>select r.id  , r.code  ,r.name  , r.create_time createTime ,r.update_time updateTime ,ru.user_id userId from sys_role_user ru inner join sys_role r on r.id = ru.role_id where ru.user_id IN " +
			" <foreach item='item' index='index' collection='list' open='(' separator=',' close=')'> " +
			" #{item} " +
			" </foreach>" +
			"</script>")
	List<SysRole> findRolesByUserIds(List<Long> userIds);



}
