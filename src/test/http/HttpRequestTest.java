package test.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kh.toy.common.http.HttpConnector;
import com.kh.toy.common.http.RequestParams;
import com.kh.toy.member.model.dto.User;

/**
 * Servlet implementation class HttpRequestTest
 */
@WebServlet("/test/http/*")
public class HttpRequestTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HttpRequestTest() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String[] uriArr = request.getRequestURI().split("/");
		
		switch (uriArr[uriArr.length -1]) {
		case "connect":
			testHttpUrlConnection();
			break;
		case "kakao-test":
			testKakao();
			break;
		case "naver-test":
			testNaver();
			break;
		default:
			break;
		}
	}

	//POST방식 통신을 위한 테스트
	private void testNaver() {
		
		HttpConnector conn = new HttpConnector();
		Gson gson = new Gson();
		
		String url = "https://openapi.naver.com/v1/datalab/search";
		
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("X-Naver-Client-Id", "3rm_c4XH5kpUjbEYfjSb");
		headers.put("X-Naver-Client-Secret", "pW1mssGFy7");
		headers.put("Content-Type", "application/json");
		
		List<String> javaKeywords = new ArrayList<String>(); //집어넣을 값이 다 String이니까 제네릭을 String으로 해줘도됨.(섞여있으면 Object로 잡아줘야됨)
		javaKeywords.add("스프링");
		javaKeywords.add("스프링부트");
		
		List<String> pythonKeywords = new ArrayList<String>();
		pythonKeywords.add("장고");
		pythonKeywords.add("플리터");
		
		Map<String, Object> javaGroup = new LinkedHashMap<String, Object>(); //문자열과 배열이 들어가야하니까 제네릭을 String, Object로
		javaGroup.put("groupName", "자바");
		javaGroup.put("keywords", javaKeywords);
		
		Map<String, Object> pythonGroup = new LinkedHashMap<String, Object>();
		pythonGroup.put("groupName", "파이썬");
		pythonGroup.put("keywords", pythonKeywords);
		
		//위에서 만든 그룹들을 하나로 담아주기
		List<Map<String, Object>> keywordsGroups = new ArrayList<Map<String,Object>>();
		keywordsGroups.add(javaGroup);
		keywordsGroups.add(pythonGroup);
		
		String[] ages = {"3","4","5","6","7","8","9","10"};
		
		Map<String,Object> datas = new LinkedHashMap<String, Object>();
		datas.put("startDate","2016-01-01");
		datas.put("endDate","2021-01-01");
		datas.put("timeUnit","month");
		datas.put("keywordGroups", keywordsGroups);
		datas.put("ages", ages);
		
		String requestBody = gson.toJson(datas); //toJson이 datas를 읽어서 Json형태 문자열로 반환해줌
		System.out.println("requestBody : " + requestBody);
		String responseBody = conn.post(url, headers, requestBody);
		System.out.println(responseBody);
		
	}

	private void testKakao() {
		//User 클래스 테스트용
		//User user = User.builder().userId("pclass").password("1234").email("aaa@bbb.com").tell("01000001111").build();
		
		HttpConnector conn = new HttpConnector();
		//Map<String,String> params = new HashMap<String,String>();
		//params.put("query","자바");
		//params.put("sort","latest");
		
		RequestParams params = RequestParams.builder().param("query", "자바").param("sort","latest").build();
		
		
		String queryString = conn.urlEncodedForm(params);
		//System.out.println(queryString);
		
		String url = "https://dapi.kakao.com/v3/search/book?" + queryString;
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Authorization", "KakaoAK 4cf9c3f319106ea0a9ebb44fe6cdbf2f");
		
		//카카오 api에서 Object로 받아오기 때문에 JsonObject로 받아오기
		JsonObject datas = conn.getAsJson(url, headers).getAsJsonObject(); //HttpConnector의 getAsJson메서드 사용한 것임
		JsonArray documents = datas.getAsJsonArray("documents");
		
		for (JsonElement jsonElement : documents) {
			JsonObject e = jsonElement.getAsJsonObject();
			System.out.println("authors : " + e.getAsJsonArray("authors")); 
			System.out.println("title : " + e.getAsJsonPrimitive("title").getAsString());
		}
		
		//gson - dafault
		//json object => Map
		//json array => List
		//json string => String
		//json number => Number, Double
		//json null => null
		//json boolean -> Boolean
		
	}

	private void testHttpUrlConnection() {

		try {
			URL url = new URL("http://localhost:9090/mail?mailTemplate=join-auth-mail"); //url 클래스 생성(우리가 요청할 경로로 지정 mailTemplate이라는 파라미터로  join-auth-mail을 보낼것임)
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET"); //get으로 지정해놓고
			StringBuffer responseBody = new StringBuffer();
			
			//데이터를 읽어오기 위한 코드
			try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));){
				String line = null; //받아 올 스트링 변수 선언
				while( (line = br.readLine()) != null) {
					responseBody.append(line);
				}
			}
			
			System.out.println(responseBody); //뭐가 날아오는지 확인용
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
