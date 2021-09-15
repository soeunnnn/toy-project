package com.kh.toy.common.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.toy.common.code.ErrorCode;
import com.kh.toy.common.exception.HandlableException;
import com.kh.toy.common.exception.PageNotFoundException;
import com.kh.toy.member.validator.JoinForm;


//모든 validator처리(우리의 실수로 나타나는 오류 처리?)를 여기서 하도록 코드 작성
public class ValidatorFilter implements Filter {

    /**
     * Default constructor. 
     */
    public ValidatorFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String[] uriArr = httpRequest.getRequestURI().split("/");  //넘어온 요청 url을 가져와서 파싱
		
		//  /member/join => [ , member, join]
		
		if(uriArr.length != 0) {
			
			String redirectUrl = null;
			
			switch (uriArr[1]) {
			//uriArr(넘어온 요청 url)의 1번 인덱스에 들어있는 값이 member 라면,
			case "member":
				redirectUrl = memberValidation(httpRequest, uriArr); //redirectUrl을 받아와서
				break;
			}
			
			if(redirectUrl != null) { //redirectUrl이 null이 아니라면,
				httpResponse.sendRedirect(redirectUrl);  //그 주소로 재요청
				return;
			}
		}
		
		chain.doFilter(request, response);
	}

	private String memberValidation(HttpServletRequest httpRequest, String[] uriArr) {

		//재요청 할 주소를 넣기 위한 변수 선언
		String redirectUrl = null;
		
		switch (uriArr[2]) {
		case "join":
			JoinForm joinForm = new JoinForm(httpRequest);
			if (!joinForm.test()) { //joinForm의 테스트의 결과가 false라면(validator를 통과하지 못했을 때), 밑의 경로로 리다이렉트 하도록 지정
				redirectUrl = "/member/join-form";
			}
			break;
		}
		return redirectUrl;
	}
	
	
	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}

