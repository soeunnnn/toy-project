package com.kh.toy.common.code;

public enum Config {
	
	//DOMAIN("https://pclass.ga"), //배포용 도메인 예시
	DOMAIN("http://localhost:9090"), //개발자용 도메인
	COMPANY_EMAIL("thdms12333@gmail.com"),
	SMTP_AUTHENTICATION_ID("thdms12333@gmail.com"),
	SMTP_AUTHENTICATION_PASSWORD("Dkssud11@@@"),
	UPLOAD_PATH("C:\\CODE\\upload\\");
	
	public final String DESC;
	
	Config(String desc){
		this.DESC = desc;
	}

}
