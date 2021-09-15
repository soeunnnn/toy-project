<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<%@ include file="/WEB-INF/views/include/head.jsp" %>
</head>
<body>
<h1>회원목록</h1>
<table border="1">
	<thead>
		<tr>
			<th>아이디</th>
			<th>휴대폰번호</th>
			<th>이메일</th>
			<th>탈퇴처리</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${members}" var="member">  <%-- AdminMemberController파일에서 members로 집어넣어놨음 --%>
			<tr>
				<td><c:out value="${member.userId}" /></td>
				<td><c:out value="${member.tell}" /></td>
				<td><c:out value="${member.email}" /></td>
				<td><a href="/admin/member/leave">탈퇴처리</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>

</body>
</html>