package com.kh.toy.member.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.kh.toy.common.db.JDBCTemplate;
import com.kh.toy.common.exception.DataAccessException;
import com.kh.toy.member.model.dto.Member;



//DAO(DATA ACCESS OBJECT)
//DBMS에 접근해 데이터의 조회, 수정, 삽입, 삭제 요청을 보내는 클래스
//DAO의 메서드는 하나의 메서드 당 하나의 쿼리만 처리하도록 작성

public class MemberDao {
	
	private JDBCTemplate template = JDBCTemplate.getInstance();

	public Member memberAuthenticate(String userId, String password, Connection conn) {
		Member member = null;
		//Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rset = null;
		String query = "select * from member where user_id = ? and password = ?";
		
		//conn = template.getConnection();
		
		try {
			pstm = conn.prepareStatement(query);
			pstm.setString(1, userId);
			pstm.setString(2, password);
			rset = pstm.executeQuery();
			
			if(rset.next()) {
				member = convertAllToMember(rset);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}finally {
			template.close(rset, pstm);
			
		}
		
		return member;
	}

	public Member selectMemberById(String userId, Connection conn) {
		Member member = null;
		//Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rset = null;
		String query = "select * from member where user_id = ?";
		
		//conn = template.getConnection();
		
		try {
			pstm = conn.prepareStatement(query);
			pstm.setString(1, userId);
			rset = pstm.executeQuery();
			if(rset.next()) {
				member = convertAllToMember(rset);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			template.close(rset, pstm);
		}
		
		return member;
	}

	public List<Member> selectMemberList(Connection conn) {
		List<Member> memberList = new ArrayList<Member>();
		PreparedStatement pstm = null;
		ResultSet rset = null;
		
		String columns = "user_id, email, tell, grade";
		String query = "select * from member";
	
		try {
			pstm = conn.prepareStatement(query);
			rset = pstm.executeQuery();
			
			while(rset.next()) {
				Member member = convertAllToMember(rset);
				memberList.add(member);
			}
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			template.close(rset, pstm, conn);
		}
		return memberList;
	}

	public int insertMember(Member member, Connection conn) {
		int res = 0;
		PreparedStatement pstm = null;
		String query = "insert into member(user_id, password, email, tell) values(?,?,?,?)";

		try {
			pstm = conn.prepareStatement(query);
			pstm.setString(1, member.getUserId());
			pstm.setString(2, member.getPassword());
			pstm.setString(3, member.getEmail());
			pstm.setString(4, member.getTell());
			res = pstm.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			template.close(pstm);
		}
		return res;	
	}

	// userId로 ' or 1=1 or user_id = ' 값을 전달받으면 모든 회원의 비밀번호가 수정
	// SQL Injection 공격
	// 악의적인 SQL구문을 주입해서 상대방의 DataBase를 공격하는 기법

	// SQL Injection 공격 막기 위해 PreparedStatement 사용
	// 인스턴스를 생성할 때 쿼리 템플릿을 미리 등록
	// 생성시 등록된 쿼리 템플릿의 구조가 변경되는 것을 방지
	// 문자열에 대해서 자동으로 이스케이프 처리
	// ex) ->\' or 1=1 or user_id = \'
	public int updatePassword(String userId, String password, Connection conn) {
		int res = 0;
		PreparedStatement pstm = null;
		
		String query = "update member set password = ? where user_id = ?";
		
		try {
			pstm = conn.prepareStatement(query);
			pstm.setString(1, password);
			pstm.setString(2, userId);
			res = pstm.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			template.close(pstm);
		}
		return res;
	}

	public int deleteMember(String userId, Connection conn) {
		int res = 0;
		PreparedStatement pstm = null;
		
		String query = "delete from member where user_id = ?";
		
		try {
			pstm = conn.prepareStatement(query);
			pstm.setString(1, userId);
			res = pstm.executeUpdate();
		} catch (SQLException e) {
			throw new DataAccessException(e);
		} finally {
			template.close(pstm);
		}
		return res;
	}

	private Member convertAllToMember(ResultSet rset) throws SQLException {
		Member member = new Member();
		member.setUserId(rset.getString("user_id"));
		member.setPassword(rset.getString("password"));
		member.setEmail(rset.getString("email"));
		member.setGrade(rset.getString("grade"));
		member.setIsLeave(rset.getInt("is_leave"));
		member.setRegDate(rset.getDate("reg_date"));
		member.setRentableDate(rset.getDate("rentable_date"));
		member.setTell(rset.getString("tell"));

		return member;
	}
	
	//쿼리에 원하는 컬럼값을 전달하면 그 컬럼값을 가져오는 메서드
	private Member convertRowToMember(String[] columns, ResultSet rset) throws SQLException {
		Member member = new Member();
		
		for (int i = 0; i < columns.length; i++) {
			String column = columns[i].toLowerCase(); //입력하는 컬럼명을 소문자로 변경
			column = column.trim();
			
			switch (column) {
			case "user_id": member.setUserId(rset.getString("user_id")); break;
			case "password" : member.setPassword(rset.getString("password")); break;
			case "email" : member.setEmail(rset.getString("email")); break;
			case "grade" : member.setGrade(rset.getString("grade")); break;
			case "is_leave" : member.setIsLeave(rset.getInt("is_leave")); break;
			case "reg_date" : member.setRegDate(rset.getDate("reg_date")); break;
			case "rentable_date" : member.setRentableDate(rset.getDate("rentable_date")); break;
			case "tell" : member.setTell(rset.getString("tell")); break;
			default : throw new SQLException("부적절한 컬럼명을 전달했습니다."); //예외처리
			}
		}
		return member;
	}

}
