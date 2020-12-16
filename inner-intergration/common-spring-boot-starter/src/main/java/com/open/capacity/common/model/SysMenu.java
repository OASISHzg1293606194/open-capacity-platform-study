package com.open.capacity.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author 作者 gitgeek 
* @version 创建时间：2018-08-06 21:29
* 类说明  菜单实体
*/
@Data
@TableName("sys_menu")
@EqualsAndHashCode(callSuper=true)
public class SysMenu extends Model<SysPermission> implements Serializable {

	private static final long serialVersionUID = 749360940290141180L;
	@JsonSerialize(using=ToStringSerializer.class)
	private Long id;
	@TableField(value="parent_id")
	@JsonSerialize(using=ToStringSerializer.class)
	private Long parentId;
	private String name;
	private String url;
	private String path;
	private String css;
	private Integer sort;
	@TableField(value="create_time")
	private Date createTime;
	@TableField(value="update_time")
	private Date updateTime;
	@TableField(value="is_menu")
	private Integer isMenu;
	private Boolean hidden;
	
	@TableField(exist=false)
	private List<SysMenu> subMenus;
	@TableField(exist=false)
	private Long roleId;
	@TableField(exist=false)
	private Set<Long> menuIds;



}
