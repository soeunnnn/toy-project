package com.kh.toy.common.exception;

//사용자가 잘못된 경로로 요청을 했을 때 처리해줄 예외 만든것임 => web.xml에 에러페이지로 등록해주기
public class PageNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -1313747291601761454L;

	public PageNotFoundException() {
		//stackTrace를 비워준다. => 콘솔창에 에러메세지가 너무 길게 뜨니까
		this.setStackTrace(new StackTraceElement[0]); //비어있는 stackTrace를 넘기기..?
	}
	
}
