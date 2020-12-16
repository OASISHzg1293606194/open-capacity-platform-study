package com.open.capacity.datasource.util;

import com.open.capacity.datasource.constant.DataSourceKey;

/**
 * 用于数据源切换
 * @author owen
 * @create 2017年7月2日
 *  blog: https://blog.51cto.com/13005375 
 * code: https://gitee.com/owenwangwen/open-capacity-platform 
 */
public class DataSourceHolder {

	//注意使用ThreadLocal，微服务下游建议使用信号量
    private static final ThreadLocal<DataSourceKey> DATA_SOURCE_KEY = new ThreadLocal<>();

    //得到当前的数据库连接
    public static DataSourceKey getDataSourceKey() {
        return DATA_SOURCE_KEY.get();
    }
    //设置当前的数据库连接
    public static void setDataSourceKey(DataSourceKey type) {
    	DATA_SOURCE_KEY.set(type);
    }
    //清除当前的数据库连接
    public static void clearDataSourceKey() {
    	DATA_SOURCE_KEY.remove();
    }


}