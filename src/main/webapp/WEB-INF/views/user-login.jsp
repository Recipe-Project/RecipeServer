<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String clientId = (String) request.getAttribute("clientId");
    String redirectURI = (String) request.getAttribute("redirectURI");
%>

<!DOCTYPE html>
<html>
<body>

<a href="https://kauth.kakao.com/oauth/authorize?client_id=<%=clientId%>&redirect_uri=<%=redirectURI%>&response_type=code">
    <img src="https://k.kakaocdn.net/14/dn/btroDszwNrM/I6efHub1SN5KCJqLm1Ovx1/o.jpg" width="100"
         alt="카카오 로그인 버튼"/>
</a>

</body>
</html>
