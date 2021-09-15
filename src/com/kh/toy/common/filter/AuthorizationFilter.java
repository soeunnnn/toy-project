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
import javax.servlet.http.HttpSession;

import com.kh.toy.common.code.ErrorCode;
import com.kh.toy.common.code.MemberGrade;
import com.kh.toy.common.exception.HandlableException;
import com.kh.toy.member.model.dto.Member;

public class AuthorizationFilter implements Filter {

    /**
     * Default constructor. 
     */
    public AuthorizationFilter() {
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
		
		String[] uriArr = httpRequest.getRequestURI().split("/"); 
		
		if(uriArr.length != 0) {
			
			switch (uriArr[1]) {
			case "member":
				memberAuthorize(httpRequest, httpResponse, uriArr);
				break;
			case "admin":
				adminAuthorize(httpRequest, httpResponse, uriArr);
				break;
			case "board":
				boardAuthorize(httpRequest, httpResponse, uriArr);
				break;
			default:
				break;
			}
		}
		
		chain.doFilter(request, response); //다음필터 호출(없으면 내부적으로 서블릿의 서비스 호출?) 
	}

	//게시글 작성 권한 설정
	private void boardAuthorize(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String[] uriArr) {
		HttpSession session = httpRequest.getSession();
		Member member = (Member) session.getAttribute("authentication");
		
		//로그인 한 사람만 게시글 작성할 수 있도록 
		switch (uriArr[2]) {
		case "board-form":
			if(member == null) {
				throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE_ERROR);
			}
			break;
		case "upload":
			if(member == null) {
				throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE_ERROR);
			}
			break;
		default:
			break;
		}
		
	}

	//회원등급에 따라 페이지 접근 정하기
	private void adminAuthorize(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String[] uriArr) {

		HttpSession session = httpRequest.getSession();
		Member member = (Member) session.getAttribute("authentication"); //authentication로 저장해놓은 사용자 정보 가져오기
		
		//비회원과 사용자 회원인지를 판단
		if(member == null || MemberGrade.valueOf(member.getGrade()).ROLE.equals("user")) {
			throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE_ERROR);
		}
		
		//슈퍼관리자라면 모든 admin페이지에 접근할 수 있다. return을 해서 아래에서 권한관리를 할 필요가 없고 doFilter에서 chain.doFilter(request, response)를 타고 컨트롤러로..?
		if(MemberGrade.valueOf(member.getGrade()).DESC.equals("super")) {
			return;
		}
		
		switch (uriArr[2]) {
		case "member":
			if(!MemberGrade.valueOf(member.getGrade()).DESC.equals("member")) { //등급이 member가 아니라면
				throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE_ERROR);
			}
			break;
		case "board":
			if(!MemberGrade.valueOf(member.getGrade()).DESC.equals("board")) { //등급이 board가 아니라면
				throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE_ERROR);
			}
			break;

		default:
			break;
		}
	}

	private void memberAuthorize(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String[] uriArr) {
		HttpSession session = httpRequest.getSession();
		
		switch (uriArr[2]) {
		
		case "join-impl":
			String serverToken = (String) session.getAttribute("persistToken"); //우리 세션에 담겨있는 토큰 값
			String clientToken = httpRequest.getParameter("persistToken"); //사용자가 보낸 토큰 값
			
			if(serverToken == null || !serverToken.equals(clientToken)) { //우리 세션에 토큰 값이 없거나 || 우리 세션에 담겨있는 토큰 값과 사용자가 보낸 토큰 값이 같지 않다면,
				throw new HandlableException(ErrorCode.AUTHENTICATION_FAILED_ERROR);
			}
			break;
		case "mypage":
			if(session.getAttribute("authentication") == null) { //로그인하지 않은 사용자는 마이페이지에 접근하지 못하도록
				throw new HandlableException(ErrorCode.REDIRECT.setURL("/member/login-form")); //로그인 폼으로 돌려보냄
			}
			break;

		default:
			break;
		}
		
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
