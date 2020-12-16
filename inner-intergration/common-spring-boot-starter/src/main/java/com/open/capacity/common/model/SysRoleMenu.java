package com.open.capacity.common.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
 
/**
* @author 作者 gitgeek 
* @version 创建时间：2018-08-06 21:29
* 类说明  角色菜单实体
*/
@Data
@Builder
@TableName("sys_role_menu")
@EqualsAndHashCode(callSuper=true)
public class SysRoleMenu  extends Model<SysRoleMenu> implements Serializable{

	private static final long serialVersionUID = 64240478379218861L;
	 
	@JsonSerialize(using=ToStringSerializer.class)
	private Long id;
	@TableField(value="role_id")
	@JsonSerialize(using=ToStringSerializer.class)
	private Long roleId;
	@TableField(value="menu_id")
	@JsonSerialize(using=ToStringSerializer.class)
    private Long menuId;

}
