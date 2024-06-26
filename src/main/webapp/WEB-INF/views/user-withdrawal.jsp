<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String userId = (String) request.getAttribute("userId");
    String jwtToken = (String) request.getAttribute("jwtToken");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests">
    <link href="/static/css/style.css" rel="stylesheet" type="text/css">
    <title>탈퇴하기</title>
</head>
<body>
<script src="https://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript">
    function withdraw() {
        $.ajax({
            method: 'DELETE',
            url: 'https://recipestorage2024.shop:9090/users/<%=userId%>',
            headers: {
                "X-ACCESS-TOKEN": "<%=jwtToken%>"
            }
        })

        window.location.href='https://recipestorage2024.shop:9090/users/withdrawal'
    }
</script>

<div class="content">
    <img src="/static/img/icon.png" alt="아이콘" width="144" height="134" />
    <h3>레시피 저장소 회원 탈퇴</h3>
    <p>레시피 저장소에서 저장한 내용은 모두 삭제되며 이후 복구가 불가능합니다.</p>
    <br>
    <button onclick="javascript:withdraw()">탈퇴하기</button>
</div>

</body>
</html>