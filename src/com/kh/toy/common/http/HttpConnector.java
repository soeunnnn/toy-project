package com.kh.toy.common.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.kh.toy.common.code.ErrorCode;
import com.kh.toy.common.exception.HandlableException;

////Http 통신 관련된 역할을 수행해 줄 클래스 분리
public class HttpConnector {
	
	private static Gson gson = new Gson(); //한번만 올려놓고 서버 내려갈 때 까지 살아있게 하기 위해 위로 올림?
	
	public String get(String url) {
		
		String responseBody = ""; //공백으로 잡아놓으면 바깥에서 널포인트이셉션이 안남
		
		try {
			HttpURLConnection conn = getConnection(url, "GET");
			responseBody = getResponseBody(conn);
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR,e); //우리가 만들어놓은 예외처리 넣어줌
		}

		return responseBody;
	}
	
	//우리가 원하는 헤더 입력한 것으로 변경해주는 get메서드로 오버로드
	public String get(String url, Map<String,String> headers) {
		
		String responseBody = ""; //공백으로 잡아놓으면 바깥에서 널포인트이셉션이 안남
		
		try {
			HttpURLConnection conn = getConnection(url, "GET"); //GET방식으로 통신 할 메서드를 만드는거니까 GET넣어주기
			setHeaders(headers, conn);
			responseBody = getResponseBody(conn);
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR,e); //우리가 만들어놓은 예외처리 넣어줌
		}

		return responseBody;
	}
	
	
	public JsonElement getAsJson(String url, Map<String,String> headers) {
		
		JsonElement responseBody = null;
		
		try {
			HttpURLConnection conn = getConnection(url, "GET"); //GET방식으로 통신 할 메서드를 만드는거니까 GET넣어주기
			setHeaders(headers, conn);
			responseBody = gson.fromJson(getResponseBody(conn),JsonElement.class); //getResposneBody 메서드를 통해서 넘어오는 애를 JsonElement로 파싱해서 반환해주기
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR,e); //우리가 만들어놓은 예외처리 넣어줌
		}

		return responseBody;
	}
	
	//Json으로 반환해주는 메서드
	public JsonElement postAsJson(String url, Map<String,String> headers, String body) {
		
		JsonElement responseBody = null;
		
		try {
			HttpURLConnection conn = getConnection(url, "POST");
			setHeaders(headers, conn);
			setBody(body, conn); 
			responseBody = gson.fromJson(getResponseBody(conn),JsonElement.class); 
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR,e);
		}

		return responseBody;
	}
	
	
	//post메서드로 통신하는 방식
	public String post(String url, Map<String,String> headers, String body) {
		
		String responseBody = "";
		
		try {
			HttpURLConnection conn = getConnection(url, "POST");
			setHeaders(headers,conn);
			setBody(body, conn); //GET 방식과의 차이점은 Body가 있어야 한다는 점
			responseBody = getResponseBody(conn);
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR,e);
		}
		
		return responseBody;
	}
	

	//커넥션 생성하는 메서드
	private HttpURLConnection getConnection(String url, String method) throws IOException {
		URL u = new URL(url); //매개변수로 받아 온 url로 넣기
		//mailTemplate 파라미터는 MainHandler에서 사용하고, userId는 join-auth-mail.jsp에서 사용
		HttpURLConnection conn = (HttpURLConnection) u.openConnection(); //받아 온 url로 커넥션 생성하고,
		conn.setRequestMethod(method); 
		
		//커넥션이 끊기면 핸들러블이셉션 걸도록
		//연결 시 최대 지연시간  
		conn.setConnectTimeout(10000);
		//응답 받을 때 최대 대기시간
		conn.setReadTimeout(10000);
		return conn;
	}
	
	//responseBody 읽어오는 메서드
	private String getResponseBody(HttpURLConnection conn) throws IOException {
		StringBuffer responseBody = new StringBuffer();
		
		//해당 인풋스트림 땡겨오기
		try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));){
			String line = null;
			while((line = br.readLine()) != null) {
				responseBody.append(line);
			}
		}
		return responseBody.toString(); //StringBuffer니까 toString으로 반환해서 내보냄
	}
	

	private void setHeaders(Map<String,String> headers, HttpURLConnection conn) {
		for(String key : headers.keySet()) {
			conn.setRequestProperty(key, headers.get(key));
		}
	}
	
	private void setBody(String body, HttpURLConnection conn) throws IOException {
		//HttpURLConnection을 사용해서 OutputStream을 사용하려고 할 때는
		//HttpURLConnection의 doOutput이 true 이어야 한다.(출력스트림 사용여부를 true)
		conn.setDoOutput(true);
		
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))){
			bw.write(body);
			bw.flush();
		}
	}
	
	
	//인코딩처리하는 코드(카카오api 활용하기 위해 만들어봄)
	public String urlEncodedForm(RequestParams params) {
		String res = "";
		Map<String,String> paramsMap = params.getParams();

		try {
			// name=value&name=value&name=value
			for (String key : paramsMap.keySet()) {
				res += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(paramsMap.get(key), "UTF-8");
			}

			if (res.length() > 0) {
				res = res.substring(1); // 위의 코드가 돌면 맨 처음부터 &가 들어있으니까 잘라주기 위해 1번 인덱스부터로
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
