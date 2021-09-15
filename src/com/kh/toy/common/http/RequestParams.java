package com.kh.toy.common.http;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestParams {
	
	private Map<String,String> params = new HashMap<String,String>();
	
	private RequestParams(RequestParamsBuilder builder) {
		this.params = builder.params;
	}
	
	//builder메서드를 통해서 RequestParamsBuilder클래스의 인스턴스를 반환 받고, 그 인스턴스에는 param이라는 메서드가 있어서 name,value를 넣어주고 RequestParamsBuilder를 반환해주게 됨
	public static RequestParamsBuilder builder() {
		return new RequestParamsBuilder();
	}

	
	//팩토리클래스, 팩토리메서드 : 해당 클래스의 인스턴스를 만들어주는 메소드
	public static class RequestParamsBuilder{
		
		private Map<String,String> params = new LinkedHashMap<String, String>(); //순서 보장하기 위해 LinkedHashMap으로 만듬
		
		//자기자신을 반환하는 메서드
		public RequestParamsBuilder param(String name, String value) {
			params.put(name, value);
			return this;
		}
		
		public RequestParams build() {
			return new RequestParams(this);
		}
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	
	
}
