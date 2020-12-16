package com.sharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


 
@Data
@Builder
@AllArgsConstructor
public class UserInfo {
	/***/
	private String userId;

	/***/
	private String companyId;

	/***/
	private String userName;

	/***/
	private String account;

	/***/
	private String password;

}