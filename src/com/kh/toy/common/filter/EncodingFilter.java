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

import com.kh.toy.common.wrapper.RequestWrapper;
import com.kh.toy.common.wrapper.ResponseWrapper;

public class EncodingFilter implements Filter {
  
    public EncodingFilter() {
        // TODO Auto-generated constructor stub
    }
	
	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding("utf-8");//모든 파일의 인코딩이 여기서 실행되게끔 
		//다음 filter에게 request와 response를 전달
		//마지막 filter였다면 HttpServlet에게 request, response를 전달
		
		//ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);
		//RequestWrapper requestWrapper = new RequestWrapper((HttpServletRequest) request);
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
