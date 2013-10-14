<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<c:import url="/header.jsp" />

<h1>Current questionnaires</h1>

<h2>Started</h2>
<c:if test="${noStartedQuestionnaires}">
  <div class="none">None</div>
</c:if>
<ul>
  <c:forEach items="${startedQuestionnaires}" var="questionnaire" varStatus="loopStatus">
    <li>${questionnaire.title}</li>
  </c:forEach>
</ul>

<h2>Active</h2>
<c:if test="${noActiveQuestionnaires}">
  <div class="none">None</div>
</c:if>
<ul>
  <c:forEach items="${activeQuestionnaires}" var="questionnaire" varStatus="loopStatus">
    <li>${questionnaire.title}</li>
  </c:forEach>
</ul>

<h2>Retired</h2>
<c:if test="${noRetiredQuestionnaires}">
  <div class="none">None</div>
</c:if>
<ul>
  <c:forEach items="${retiredQuestionnaires}" var="questionnaire" varStatus="loopStatus">
    <li>${questionnaire.title}</li>
  </c:forEach>
</ul>

<c:import url="/footer.jsp" />