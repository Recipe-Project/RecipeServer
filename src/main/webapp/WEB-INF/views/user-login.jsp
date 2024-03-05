<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String kakaoClientId = (String) request.getAttribute("kakaoClientId");
    String kakaoRedirectURI = (String) request.getAttribute("kakaoRedirectURI");
%>

<!DOCTYPE html>
<html>
<body>

<a href="https://kauth.kakao.com/oauth/authorize?client_id=<%=kakaoClientId%>&redirect_uri=<%=kakaoRedirectURI%>&response_type=code">
    <img src="https://k.kakaocdn.net/14/dn/btroDszwNrM/I6efHub1SN5KCJqLm1Ovx1/o.jpg" width="150"
         alt="카카오 로그인 버튼"/>
</a>

</body>
</html>
