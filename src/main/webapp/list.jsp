<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
    <thead>
    <tr>
        <th>Name</th>
        <th>Gender</th>
        <th>Date of birth</th>
        <th>Date of death</th>
        <th>Clearance level</th>
    </tr>
    </thead>
    <c:forEach items="${agents}" var="agent">
        <tr>
            <td><c:out value="${agent.name}"/></td>
            <td><c:out value="${agent.gender}"/></td>
            <td><c:out value="${agent.dateOfBirth}"/></td>
            <td><c:out value="${agent.dateOfDeath}"/></td>
            <td><c:out value="${agent.clearanceLevel}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/agent/delete?id=${agent.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Input agent</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/agent/add" method="post">
    <table>
        <tr>
            <th>name of agent:</th>
            <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
        </tr>
        <tr>
            <th>gender:</th>
            <td><input type="text" name="gender" value="<c:out value='${param.gender}'/>"/></td>
        </tr>
        <tr>
            <th>birth(yyyy:mm:dd):</th>
            <td><input type="text" name="birth" value="<c:out value='${param.birth}'/>"/></td>
        </tr>
        <tr>
            <th>death(yyyy:mm:dd):</th>
            <td><input type="text" name="death" value="<c:out value='${param.death}'/>"/></td>
        </tr>
        <tr>
            <th>clearance:</th>
            <td><input type="text" name="clearance" value="<c:out value='${param.clearance}'/>"/></td>
        </tr>

    </table>
    <input type="Submit" value="Zadat" />
</form>

</body>
</html>