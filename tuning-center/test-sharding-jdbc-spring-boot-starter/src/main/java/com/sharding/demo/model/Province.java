package com.sharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

 
@Data
@AllArgsConstructor
public class Province {
	/***/
	private String id;

	/***/
	private String provId;

	/***/
	private String provName;

	/**1 - 直辖市
		2 - 行政省
		3 - 自治区
		4 - 特别行政区
		5 - 其他国家
		见全局数据字典[省份类型] 
	*/
	private String provType;

	/**0 - 禁用
	   1 - 启用
	*/
	private String provState;

}