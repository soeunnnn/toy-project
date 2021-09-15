package com.kh.toy.common.wrapper;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper{

	private HttpServletRequest request;

	public RequestWrapper(HttpServletRequest request) {
		super(request);
		this.request = request;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public RequestDispatcher getRequestDispatcher(String url) {
		return this.request.getRequestDispatcher("/WEB-INF/views/" + url + ".jsp");
	}
	
	public RequestDispatcher getRequestDispatcher(String url, String prefix, String suffix) {
		return request.getRequestDispatcher(prefix + url + suffix);
	}
	
	public String[] getRequestURIArray() {
		String uri = this.getRequestURI();
		String[] uriArr = uri.split("/");
		return uriArr;
	}

}
