<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:import url="/widgets/header.jsp" />

<h1>New Questionnaire</h1>
<div class="wrap">
  <form:form method="post" action="/admin/save">
    <fieldset>
      <p>
        <label for="title">Title</label>
        <input name="title" id="title" />
      </p>
      <p>
        <label for="description">Description</label>
        <textarea name="description" rows="4" cols="60"></textarea>
      </p>
      <p>
        <label for="next">Next part</label>
        <select name="next">
          <option value="">--None--</option>
          <c:forEach items="${nextQuestionnaires}" var="questionnaire" varStatus="loopStatus">
            <option value="${questionnaire.id}">${questionnaire.title}</option>
          </c:forEach>
        </select>
      <p><input type="submit" value="Save" /></p>
    </fieldset>
  </form:form>
</div>

<c:import url="/widgets/footer.jsp" />