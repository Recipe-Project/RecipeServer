<%@ page import="java.math.BigInteger" %>
<%@ page import="java.security.SecureRandom" %>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%
    String kakaoClientId = (String) request.getAttribute("kakaoClientId");
    String kakaoRedirectURI = (String) request.getAttribute("kakaoRedirectURI");

    String naverClientId = (String) request.getAttribute("naverClientId");
    String naverRedirectURI = (String) request.getAttribute("naverRedirectURI");
    SecureRandom random = new SecureRandom();
    String naverState = new BigInteger(130, random).toString();

%>

<!DOCTYPE html>
<html>
<body>
<p>
    <a href="https://kauth.kakao.com/oauth/authorize?client_id=<%=kakaoClientId%>&redirect_uri=<%=kakaoRedirectURI%>&response_type=code">
        <img src="https://k.kakaocdn.net/14/dn/btroDszwNrM/I6efHub1SN5KCJqLm1Ovx1/o.jpg" width="200" height="50"
             alt="카카오 로그인 버튼"/>
    </a>
</p>
<p>
    <a href="https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=<%=naverClientId%>&redirect_uri=<%=naverRedirectURI%>&state=<%=naverState%>">
        <img width="200" height="50" src="https://static.nid.naver.com/oauth/big_g.PNG"/>
    </a>
</p>
</body>
</html>
