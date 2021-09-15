package com.kh.toy.common.exception;

import com.kh.toy.common.code.ErrorCode;

//우리가 모듈화 해서 다룰 예외처리
//개발자가 작성한 코드와는 무관하게 사고로 인해서 언제든지 발생할 수 있기 때문에, 반드시 예외처리를 해줘야하는 예외(우리가 처리할 수 없는 예외들)
public class HandlableException extends RuntimeException {


	private static final long serialVersionUID = -3930409458039432875L;
	public ErrorCode error; //우리가 만들어놓은 이넘 
	
	//콘솔창 에러코드를 초기화 해주는 용도?
	public HandlableException(ErrorCode error) {
		this.error = error;
		this.setStackTrace(new StackTraceElement[0]);
	}
	
	public HandlableException(ErrorCode error, Exception e) {
		this.error = error;
		e.printStackTrace();
		this.setStackTrace(new StackTraceElement[0]);
	}
	
	//1. 콘솔에 로그
	//2. result.jsp를 사용해 사용자에게 알림메세지 띄워주기, 경로 재지정
	//	 발생한 예외별 에러 메세지와 재지정할 경로
	
	
}
