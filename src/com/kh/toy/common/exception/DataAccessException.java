package com.kh.toy.common.exception;

import com.kh.toy.common.code.ErrorCode;

//예외처리가 강제되지 않는 UnCheckedException
//핸들러블이셉션을 상속받고 있기 때문에, 데이터이셉션이 발생했을 때 우리가 캐치해주지 않으면 서블릿컨트롤러로 넘어가게 될 것임
public class DataAccessException extends HandlableException{

	private static final long serialVersionUID = 2015874601413512517L;

	//기본생성자 만들기
	public DataAccessException(Exception e) {
		super(ErrorCode.DATABASE_ACCESS_ERROR,e);
	}

}
