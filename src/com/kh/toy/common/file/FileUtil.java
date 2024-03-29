package com.kh.toy.common.file;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.kh.toy.common.code.Config;
import com.kh.toy.common.code.ErrorCode;
import com.kh.toy.common.exception.HandlableException;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

public class FileUtil {
	
	private final int MAX_SIZE = 1024*1024*10;
	
	//FileDTO, MultipartRequest를 담고 있는 Map을 반환
	//메서드 반환타입을 Map<String,List> => MultiPartParams로 바꿔서 테스트해보기
	public MultiPartParams fileUpload(HttpServletRequest request){
		
		List<FileDTO> fileDtoList = new ArrayList<FileDTO>();
		Map<String,List> paramMap = new HashMap<String, List>();
		
		try {
			
			//3. 경로와 파일이름으로 파일을 저장, MultipartParser 생성
			MultipartParser parser = new MultipartParser(request, MAX_SIZE);
			parser.setEncoding("UTF-8");
			Part part = null;
			
			while((part = parser.readNextPart()) != null) {
				if(part.isFile()) {
					
					//MultipartParser는 form태그에 input[type=file] 이 존재하면, 사용자가 파일을 첨부하지 않더라도
					//빈 FilePart객체를 생성해서 MultipartParser객체에 넘겨준다.
					//사용자가 파일을 첨부하지 않은 경우에는 getFileName 메서드의 결과가 null이 반환된다.
					
					FilePart filePart = (FilePart) part;
					if(filePart.getFileName() != null) {
						FileDTO fileDTO = createFileDTO(filePart);
						uploadFile(filePart, fileDTO);
						fileDtoList.add(fileDTO); //생성한 FileDTO를 list에 넣어주기
					}
					
					/*
					 * System.out.println("=============파일=============");
					 * System.out.println(filePart.getFileName());
					 * System.out.println(filePart.getName());
					 * System.out.println(filePart.getFilePath());
					 */
				}else {
					ParamPart paramPart = (ParamPart) part;
					setParameterMap(paramPart, paramMap);
					
					/*
					 * System.out.println("=============파라미터=============");
					 * System.out.println(paramPart.getStringValue());
					 * System.out.println(paramPart.getName());
					 */
				}
			}
			
			//5. 3번에서 생성한 MiltipartRequest와 List<FileDTO>를 반환
			paramMap.put("com.kh.toy.files", fileDtoList);
			
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.FAILED_FILE_UPLOAD_ERROR);
		}
		
		return new MultiPartParams(paramMap);
	}
	
	
	
	private String createSubPath() {
		
		//2. 파일 업로드 날짜 기준으로 저장될 파일 경로 생성
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int date = cal.get(Calendar.DAY_OF_MONTH);
		
		return year + "\\" + month + "\\" + date + "\\";
	}
	
	private String createUploadPath(String subPath) {
		String uploadPath = Config.UPLOAD_PATH.DESC + subPath;
		
		File dir = new File(uploadPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		return uploadPath;
	}
	
	private FileDTO createFileDTO(FilePart filePart) {
		String renameFileName = UUID.randomUUID().toString(); //1. 서버에 저장될 유니크한 파일이름 생성(난수생성)
		String originFileName = filePart.getFileName(); //4. File_INFO 테이블에 저장할 FileDTO 생성
		String savePath = createSubPath();
		
		FileDTO fileDTO = new FileDTO();
		fileDTO.setOriginFileName(originFileName);
		fileDTO.setRenameFileName(renameFileName);
		fileDTO.setSavePath(savePath);
		
		return fileDTO;
	}
	
	private void uploadFile(FilePart filePart, FileDTO fileDTO) throws IOException {
		File file = new File(createUploadPath(fileDTO.getSavePath()) + fileDTO.getRenameFileName()); //새 파일 생성
		filePart.writeTo(file); //file을 생성해서 넣어주면 해당 파일을 생성해줌?
	}
	
	private void setParameterMap(ParamPart paramPart, Map<String,List> paramMap) throws UnsupportedEncodingException {
		//	 파라미터명으로 기존에 저장된 데이터가 없다.
		//	 새로운 List를 생성해서 파라미터 value를 저장
		if(!paramMap.containsKey(paramPart.getName())) { //해당 파라미터명을 가지고 있는 key가 없다면
			List<String> params = new ArrayList<String>(); //새로운 list 생성
			params.add(paramPart.getStringValue()); //list에 param value를 넣고
			paramMap.put(paramPart.getName(), params); //maplist에 넣어주기
		}else {
			// 파라미터명으로 기존에 저장된 데이터가 있다.
			// 기존의 List를 불러와서 파라미터 value를 저장
			paramMap.get(paramPart.getName()).add(paramPart.getStringValue());
			
		}
	}
	
	
	
	

}
