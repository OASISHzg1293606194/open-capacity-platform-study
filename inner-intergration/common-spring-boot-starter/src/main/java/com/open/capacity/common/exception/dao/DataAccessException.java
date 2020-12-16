package com.open.capacity.common.exception.dao;

import com.open.capacity.common.exception.BaseException;

/**
 * 数据库相关异常
 * blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform
 */

public class DataAccessException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325096920926406459L;

	public DataAccessException(String msg) {
		super(msg);
	}

	public DataAccessException(Exception e) {
		this(e.getMessage());
	}
}
