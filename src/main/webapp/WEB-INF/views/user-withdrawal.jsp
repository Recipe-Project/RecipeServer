<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String jwtToken = (String) request.getAttribute("jwtToken");
    System.out.println(jwtToken);
%>
<!DOCTYPE html>
<html>
<body>
<script src="http://code.jquery.com/jquery-latest.js"></script>
<script type="text/javascript">
    function withdraw() {
        $.ajax({
            method: 'DELETE',
            url: 'http://localhost:19090/users',
            headers: {
                "X-ACCESS-TOKEN": "<%=jwtToken%>"
            }
        })

        window.location.href='http://localhost:19090/users/withdrawal'
    }
</script>

<button onclick="javascript:withdraw()">탈퇴하기</button>

</body>
</html>