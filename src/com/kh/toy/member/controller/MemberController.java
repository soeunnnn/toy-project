package com.kh.toy.member.controller;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kh.toy.common.code.ErrorCode;
import com.kh.toy.common.exception.DataAccessException;
import com.kh.toy.common.exception.HandlableException;
import com.kh.toy.common.exception.PageNotFoundException;
import com.kh.toy.member.model.dto.Member;
import com.kh.toy.member.model.service.MemberService;

/**
 * Servlet implementation class MemberController
 */
@WebServlet("/member/*")
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private MemberService memberService = new MemberService();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MemberController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //브라우저가 요청을 한다 -> 브라우저로 응답이 날아간다 -> 그 안에 있는 서블릿이 하는 일 -> 요청이 들어옴 -> 필터를 타고, 필터에서 인코딩을 하고, 필터에서 리퀘스트래퍼가 돌고,
    //컨트롤러로 넘어가고, 컨트롤러에서 처리가 되고 -> 다오에서 예외 발생(예외가 없으면 뷰로 가서 화면출력) -> throws가 되니까 컨트롤러 바깥으로 넘어가고 -> 서블릿컨테이너가 해당 예외를 캐치해서 
    //우리의 이셉션핸들러를 부름 -> 보여줄 뷰를 결정해줌 -> 화면에 나타남
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] uriArr = request.getRequestURI().split("/");
		
		switch (uriArr[uriArr.length-1]) {
		case "login-form":
			loginForm(request,response);
			break;
		case "login" :
			login(request,response);
			break;
		case "logout" :
			logout(request,response);
			break;
		case "join-form" :
			joinForm(request,response);
			break;
		case "join" :   //join-form.jsp에서 가입버튼 누르면 url을 member/join으로 호출하니까 여기로 넘어옴
			join(request,response);
			break;
		case "id-check" : 
			checkId(request,response);
			break;
		case "join-impl" : 
			joinImpl(request,response);
			break;
		case "mypage" : 
			mypage(request,response);
			break;
		default: throw new PageNotFoundException();  //우리가 만든 예외처리 클래스 넣어주기
		
		}
	}
	
	
	private void mypage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/member/mypage").forward(request, response);
		
	}

	private void join(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 회원가입을 눌렀을 때 넘어오는 정보들 다 받아오기
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		String tell = request.getParameter("tell");
		String email = request.getParameter("email");

		// 받아 온 정보들 member에 넣어주기
		Member member = new Member();
		member.setUserId(userId);
		member.setPassword(password);
		member.setTell(tell);
		member.setEmail(email);
		
		//메일을 보내기 전에 난수를 하나 생성해주기
		String persistToken = UUID.randomUUID().toString();
		request.getSession().setAttribute("persistUser", member);
		request.getSession().setAttribute("persistToken", persistToken);
		
		//메일 보낼 때 멤버 정보와 랜덤으로 생성한 난수를 같이 보내주기
		memberService.authenticateByEmail(member, persistToken); //회원가입 폼에서 우리가 입력한 정보를 가져와서 넣어주고 메일 전송하는 메서드를 서비스에서 생성하도록
	
		//안내메세지 전송되도록 하고, index로 돌려보내기
		request.setAttribute("msg", "이메일이 발송되었습니다.");
		request.setAttribute("url", "/index");
		request.getRequestDispatcher("/error/result").forward(request, response);
		
	}

	//회원가입 진행(db로 넘겨줌) join-auth-mail.jsp파일의 151번줄 <a>태그에 주소 넣어줬음 => 버튼 누르면 여기로 요청 날아오도록 하려고
	private void joinImpl(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		Member member = (Member)session.getAttribute("persistUser");
		memberService.insertMember(member);
		
		//insert를 성공하면 우리가 발행한 토큰 만료시켜줘야 함(날려버리기)
		session.removeAttribute("persistToken");
		session.removeAttribute("persistUser");
		
		response.sendRedirect("/member/login-form");
		
	}

	private void checkId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("userId");
		
		Member member = memberService.selectMemberById(userId);
		
		if(member == null) {
			//우리가 페치랑 통신을 하고있기 때문에 리다이렉트나 포워드를 사용하지 않음
			response.getWriter().print("available");
		}else {
			response.getWriter().print("disable");
		}
		
	}

	private void joinForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/member/join-form").forward(request, response); //join-form.jsp로 보내줌
		
	}

	private void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//세션 : 사용자 인증정보가 담기는 공간 X
		//		session scope를 가지는 저장공간 이다.
		request.getSession().removeAttribute("authentication");  //세션에 저장되어 있는 인증정보만 없애주기
		response.sendRedirect("/index");
		
	}

	private void loginForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/member/login").forward(request, response);
		
	}
	
	private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		
		
		Member member = memberService.memberAuthenticate(userId, password);
		
		//System.out.println(member);

		//1. DataBase 또는 Service단에서 문제가 생겨서 예외가 발생(MemberDao에서 쿼리문에 오타 집어넣어서 확인해보기)
	
		
		//2. 사용자가 잘못된 아이디와 비밀번호를 입력한 경우
		//	  사용자에게 아이디나 비밀번호가 틀렸음을 알림, login-form으로 redirect
		if(member == null) {
			response.sendRedirect("/member/login-form?err=1");
			return;
		}
		
		//로그인 성공시 authentication 라는 이름으로 세션에 사용자 정보 담아놓기
		request.getSession().setAttribute("authentication", member);
		response.sendRedirect("/index");   //sendRedirect로 넘겼는데 index.jsp에서 ${authentication.userId}님 안녕? 에서 userId를 받아올 수 있는지..?세션에 저장하니까 되나..?
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
	}

}
