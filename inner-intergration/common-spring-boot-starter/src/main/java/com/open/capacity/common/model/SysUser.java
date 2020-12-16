package com.open.capacity.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
 * 类说明 用户实体
 */
@Data
@TableName("sys_user")
@EqualsAndHashCode(callSuper=true)
public class SysUser  extends Model<SysUser>  implements Serializable {

	private static final long serialVersionUID = -5886012896705137070L;
	@TableId(value="id",type=IdType.ASSIGN_ID)  //雪花算法  id生成策略
	@JsonSerialize(using=ToStringSerializer.class)
	private Long id;
	private String username;
	private String password;
	@TableField(value="nick_name")
	private String nickname;
	@TableField(value="head_img_url")
	private String headImgUrl;
	private String phone;
	private Integer sex;
	private Boolean enabled;
	private String type;
	@TableField(value="create_time")
	private Date createTime;
	@TableField(value="update_time")
	private Date updateTime;
	
	@TableField(exist=false)
	private List<SysRole> roles;
	
	@TableField(exist=false)
	private String roleId;

	@TableField(exist=false)
	private String oldPassword;
	@TableField(exist=false)
	private String newPassword;

}
