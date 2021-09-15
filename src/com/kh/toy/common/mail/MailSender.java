package com.kh.toy.common.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import com.kh.toy.common.code.Config;
import com.kh.toy.common.code.ErrorCode;
import com.kh.toy.common.exception.HandlableException;

//회원가입 시 메일 보내주는 코드들 분리 한 클래스
public class MailSender {
	
	private static final Properties SMTP_PROPERTIES; //변할 일이 없는 변수니까 위로 올려놓기
	
	//static 초기화 블록
	static {
		SMTP_PROPERTIES = new Properties();
		SMTP_PROPERTIES.put("mail.smtp.host", "smtp.gmail.com");
		SMTP_PROPERTIES.put("mail.smtp.port", "587");
		SMTP_PROPERTIES.put("mail.smtp.starttls.enable", "true");
		SMTP_PROPERTIES.put("mail.smtp.ssl.protocols", "TLSv1.2");
		SMTP_PROPERTIES.put("mail.smtp.auth", "true");
		SMTP_PROPERTIES.put("mail.debug","true");
	}

	
	//수신자, 메일 제목, 메일 내용(body) 는 외부에서 받아오도록 코드 작성
	public void sendEmail(String to, String subject, String body) {
		
		Session session = Session.getInstance(SMTP_PROPERTIES, null);

		try {
			MimeMessage msg = setMessage(session, to, subject, body);
			sendMessage(session, msg);	
		} catch (MessagingException mex) { //예외처리
			//사용자에게 "메일 발송 중 문제가 생겼습니다." 안내 메세지 전달하고 인덱스 페이지로 리다이렉트
			//log에 에러의 stack trace 기록
			throw new HandlableException(ErrorCode.MAIL_SEND_FAIL_ERROR, mex);
		}
	}
	

	//메세지를 작성하는 메서드
	private MimeMessage setMessage(Session session, String to, String subject, String body) throws MessagingException {
		MimeMessage msg = new MimeMessage(session); //javamail api 확인해보기
		msg.setFrom(Config.COMPANY_EMAIL.DESC); //config 이넘 파일에 만들어놓은 값 가져다 쓰기
		msg.setRecipients(Message.RecipientType.TO, to);  //메일 받는 사람
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setText(body, "UTF-8", "html"); //html 양식에 맞춰 메일이 갈 수 있도록 지정(api참고)
		return msg;
	}
	
	//메일을 전송하는 메서드
	private void sendMessage(Session session, MimeMessage msg) throws MessagingException {
		Transport tr = session.getTransport("smtp");
		tr.connect("smtp.gmail.com", Config.SMTP_AUTHENTICATION_ID.DESC, Config.SMTP_AUTHENTICATION_PASSWORD.DESC);   //메일 보내는 계정?(config 이넘 파일에 만들어놓은 계정)
		msg.saveChanges();
		tr.sendMessage(msg, msg.getAllRecipients());
		tr.close();
	}
	
	
}
