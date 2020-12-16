package com.open.capacity.common.util;

import com.google.gson.Gson;

public class GsonBuilder {
 
	private final static Gson GSON = new Gson();
	
	public static Gson build(){
		return GSON ;
	}
	
}
