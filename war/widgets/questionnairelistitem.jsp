<li>
  ${questionnaire.title.english}
  <a href="${questionnaire.id}">Edit</a>
  <span class="a" onclick="$('#activate_${questionnaire.id}').submit()">Activate</span>
  <span class="a" onclick="$('#retire_${questionnaire.id}').submit()">Retire</span>
  <form id="activate_${questionnaire.id}" name="activate_${questionnaire.id}" method="post" action="activate/${questionnaire.id}"></form>
  <form id="retire_${questionnaire.id}" name="retire_${questionnaire.id}" method="post" action="retire/${questionnaire.id}"></form>
</li>
