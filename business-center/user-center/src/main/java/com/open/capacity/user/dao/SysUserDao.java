package com.open.capacity.user.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.open.capacity.common.model.SysUser;

/**
* @author 作者 owen 
* @version 创建时间：2017年11月12日 上午22:57:51
 * 用户管理
 */
@Mapper
public interface SysUserDao  extends BaseMapper<SysUser> {

	@Options(useGeneratedKeys = true, keyProperty = "id")
	@Insert("insert into sys_user(username, password, nick_name , head_img_url, phone, sex, enabled, type, create_time, update_time) "
			+ "values(#{username}, #{password}, #{nickname}, #{headImgUrl}, #{phone}, #{sex}, #{enabled}, #{type}, #{createTime}, #{updateTime})")
	int save(SysUser sysUser);

	int updateByPrimaryKey(SysUser sysUser);

	@Select("select id ,username, password,nick_name nickname, head_img_url headImgUrl , phone, sex, enabled, type, create_time createTime , update_time updateTime from sys_user t where t.username = #{username}")
	SysUser findByUsername(String username);

	@Select("select id ,username, password,nick_name nickname, head_img_url headImgUrl , phone, sex, enabled, type, create_time createTime , update_time updateTime from sys_user t where t.id = #{id}")
	SysUser findById(Long id);

	@Select("select id ,username, password,nick_name nickname, head_img_url headImgUrl , phone, sex, enabled, type, create_time createTime , update_time updateTime  from sys_user u   where u.username = #{username}")
	SysUser findUserByUsername(String username);

	@Select("select id ,username, password,nick_name nickname, head_img_url headImgUrl , phone, sex, enabled, type, create_time createTime , update_time updateTime  from sys_user u   where u.phone = #{mobile}")
	SysUser findUserByMobile(String mobile);
	
	int count(Map<String, Object> params);

	List<SysUser> findList(Map<String, Object> params);
	
	
	
	

}
