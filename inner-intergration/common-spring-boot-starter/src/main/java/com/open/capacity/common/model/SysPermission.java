package com.open.capacity.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 类说明 权限标识
 */
@Data
@TableName("sys_permission")
@EqualsAndHashCode(callSuper=true)
public class SysPermission extends Model<SysPermission> implements Serializable {

	private static final long serialVersionUID = 1389727646460449239L;
	@TableId(value="id",type=IdType.ASSIGN_ID)  //雪花算法  id生成策略
	@JsonSerialize(using=ToStringSerializer.class)
	private Long id;
	private String permission;
	private String name;
	@TableField(value="create_time")
	private Date createTime;
	@TableField(value="update_time")
	private Date updateTime;
	@TableField(exist=false)
	private Long roleId;
	
	
	@TableField(exist=false)
	private Set<Long> authIds;

}
