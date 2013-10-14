package org.zanaafrica.surveys.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zanaafrica.surveys.model.Answer;
import org.zanaafrica.surveys.model.DiscreteValue;
import org.zanaafrica.surveys.model.Language;
import org.zanaafrica.surveys.model.Participant;
import org.zanaafrica.surveys.model.Question;
import org.zanaafrica.surveys.model.QuestionCategory;
import org.zanaafrica.surveys.model.QuestionError;
import org.zanaafrica.surveys.model.Questionnaire;
import org.zanaafrica.surveys.model.Questionnaire.QuestionnaireStatus;
import org.zanaafrica.surveys.model.Survey;
import org.zanaafrica.surveys.model.Survey.SurveyStatus;
import org.zanaafrica.surveys.model.Text;
import org.zanaafrica.surveys.store.ParticipantOps;
import org.zanaafrica.surveys.store.QuestionnaireOps;
import org.zanaafrica.surveys.store.SurveyOps;

import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class StoreTest
{
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
        new LocalDatastoreServiceTestConfig(),
        new LocalMemcacheServiceTestConfig()
    );

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        helper.setUp();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testQuestionnaireOps () 
    {
        // create a new questionnaire object and assert that the status is started
        Text title = new Text("My Title");
        Text description = new Text(Lists.newArrayList("English Description", "Kiswahili Description", 
            "Amharic Description"));
        User author = new User("email", "authDomain");
        Questionnaire questionnaire = new Questionnaire(title, description, author);
        assertEquals(questionnaire.getStatus(), QuestionnaireStatus.Started);
        assertEquals(description.getTranslation(Language.Kiswahili), "Kiswahili Description");
        assertEquals(description.getTranslation(Language.Amharic), "Amharic Description");
        
        // save the questionnaire to the store and assert that we now have a non-zero id
        QuestionnaireOps.INSTANCE.saveQuestionnaire(questionnaire);
        assertNotNull(questionnaire.getId());
        assertTrue(questionnaire.getId() > 0);
        
        // add some demographics questions to the questionnaire
        Question info1 = Question.createFreeText(QuestionCategory.Demographics, new Text("Residential Area"), true);
        QuestionnaireOps.INSTANCE.saveQuestion(questionnaire, info1);
        Question info2 = Question.createFreeText(QuestionCategory.Demographics, new Text("School"), false);
        QuestionnaireOps.INSTANCE.saveQuestion(questionnaire, info2);
        assertEquals(questionnaire.getQuestions().size(), 2);
        assertTrue(info2.getType().hasFreeText());
        
        // add more questions to the questionnaire
        Question question1 = Question.createNumeric(QuestionCategory.Study, new Text("Question 1"), true);
        QuestionnaireOps.INSTANCE.saveQuestion(questionnaire, question1);
        
        Question question2 = Question.createMultipleChoice(QuestionCategory.Study, new Text("Question 2"), true,
            Lists.newArrayList(
                new DiscreteValue(1, new Text("Option 1")),
                new DiscreteValue(2, new Text("Option 2")),
                new DiscreteValue(3, new Text("Option 3"))));
        QuestionnaireOps.INSTANCE.saveQuestion(questionnaire, question2);
        Question question3 = Question.createRange(QuestionCategory.Study, new Text("Question 3"), true, 1, 10);
        QuestionnaireOps.INSTANCE.saveQuestion(questionnaire, question3);
        assertEquals(questionnaire.getQuestions().size(), 5);
        assertTrue(question2.getType().hasMultipleChoice());
        assertEquals(question2.getOptions().size(), 3);
        assertFalse(question3.getType().hasMultipleChoice());
        
        // activate the questionnaire
        questionnaire.activate();
        QuestionnaireOps.INSTANCE.saveQuestionnaire(questionnaire);
        assertEquals(questionnaire.getStatus(), QuestionnaireStatus.Active);      
        
        // create and save a second questionnaire and verify that it is not returned when querying for active ones
        Questionnaire questionnaire2 = new Questionnaire(new Text("Different Title"), description, author);
        QuestionnaireOps.INSTANCE.saveQuestionnaire(questionnaire2);
        List<Questionnaire> actives = QuestionnaireOps.INSTANCE.getQuestionnairesByStatus(QuestionnaireStatus.Active);
        assertEquals(actives.size(), 1);
        assertEquals(actives.get(0).getTitle().getTranslation(Language.English), "My Title");
        
        // verify that loading the full questionnaire also loads the questions        
        Long questionnaireId = actives.get(0).getId();
        Questionnaire questionnaire3 = QuestionnaireOps.INSTANCE.loadQuestionnaire(questionnaireId);
        assertEquals(questionnaire3.getId(), questionnaireId);
        assertTrue(questionnaire3.hasQuestionsLoaded());
        
        // verify that no question information was lost
        Question reloadedQuestion = questionnaire3.getQuestions().get(0);
        assertEquals(reloadedQuestion.getCategory(), QuestionCategory.Demographics);
        assertEquals(reloadedQuestion.getPrompt().getTranslation(Language.English), "Residential Area");
        assertTrue(reloadedQuestion.isRequired());
        
        // move a question, save the changes to the store, and verify that the changes are persisted
        questionnaire3.moveQuestion(info2.getId(), 0);
        QuestionnaireOps.INSTANCE.saveQuestionnaire(questionnaire3);
        Questionnaire questionnaire4 = QuestionnaireOps.INSTANCE.loadQuestionnaire(questionnaireId);
        assertEquals(questionnaire4.getQuestions().get(0).getPrompt().getTranslation(Language.English), "School");
    }
    
    @Test
    public void testParticipantOps () 
    {
        // add some participants to the store
        Date birthday1 = new GregorianCalendar(1997, 3, 7).getTime();
        Date birthday2 = new GregorianCalendar(1997, 5, 12).getTime();        
        Participant participant1 = new Participant("UIN-1", "Name 1", birthday1, "123-456-7890");
        ParticipantOps.INSTANCE.saveParticipant(participant1);
        Participant participant2 = new Participant("UIN-2", "Name 2", birthday2, "123-456-7890");
        ParticipantOps.INSTANCE.saveParticipant(participant2);
        Participant participant3 = new Participant("UIN-3", "Name 3", birthday2, "432-765-8657");
        ParticipantOps.INSTANCE.saveParticipant(participant3);
        
        // verify that the constructors and getters work as expected
        Long participantId1 = participant1.getId();
        assertNotNull(participantId1);
        assertTrue(participantId1 > 0);
        assertEquals(participant1.getUin(), "UIN-1");
        assertEquals(participant1.getName(), "Name 1");
        assertEquals(participant1.getBirthday(), birthday1);
        assertEquals(participant1.getPhoneNumber(), "123-456-7890");
        
        // test the queries
        assertEquals(ParticipantOps.INSTANCE.loadParticipant(participantId1).getName(), "Name 1");
        assertEquals(ParticipantOps.INSTANCE.getParticipantByUin("UIN-2").getName(), "Name 2");
        assertEquals(ParticipantOps.INSTANCE.getParticipantsByName("Name 3").get(0).getUin(), "UIN-3");
        List<Participant> resultSet1 = ParticipantOps.INSTANCE.getParticipantsByBirthday(birthday2);
        assertEquals(resultSet1.size(), 2);
        assertTrue(resultSet1.contains(participant2));
        assertTrue(resultSet1.contains(participant3));
        List<Participant> resultSet2 = ParticipantOps.INSTANCE.getParticipantsByPhoneNumber("123-456-7890");
        assertEquals(resultSet2.size(), 2);
        assertTrue(resultSet2.contains(participant1));
        assertTrue(resultSet2.contains(participant2));
        
        // test an update
        participant1.setPhoneNumber("888-867-5309");
        ParticipantOps.INSTANCE.saveParticipant(participant1);
        participant1 = ParticipantOps.INSTANCE.loadParticipant(participantId1);
        assertEquals(participant1.getPhoneNumber(), "888-867-5309");
        
        // test a delete
        ParticipantOps.INSTANCE.deleteParticipant(participant3.getId());
        assertEquals(ParticipantOps.INSTANCE.getParticipantsByName("Name 3").size(), 0);
        assertEquals(ParticipantOps.INSTANCE.getParticipantsByBirthday(birthday2).size(), 1);
    }
    
    @Test
    public void testSurveyOps () 
    {
        // create a survey and save it to the store
        Survey survey = new Survey(123L, new User("email@domain.com", "authDomain"), Language.English);
        SurveyOps.INSTANCE.saveSurvey(survey);
        Long surveyId = survey.getId();
        assertEquals(survey.getQuestionnaireId().longValue(), 123L);
        
        // add a participant id to the survey and update it
        survey.setParticipantId(456L);
        SurveyOps.INSTANCE.saveSurvey(survey);
        assertEquals(survey.getParticipantId().longValue(), 456L);
        
        // load the survey and make sure it loaded properly
        survey = SurveyOps.INSTANCE.loadSurvey(surveyId);
        assertEquals(survey.getLanguage(), Language.English);
        
        // create a second survey and save it to the store with a "submitted" status
        Survey survey2 = new Survey(789L, new User("email@domain.com", "authDomain"), Language.Kiswahili);
        survey2.submit();
        SurveyOps.INSTANCE.saveSurvey(survey2);
        
        // test the queries
        List<Survey> surveys = SurveyOps.INSTANCE.getSurveysByQuestionnaire(123L, null);
        assertEquals(surveys.size(), 1);
        assertEquals(surveys.get(0).getId(), surveyId);
        surveys = SurveyOps.INSTANCE.getSurveysByEnumerator("email@domain.com", null);
        assertEquals(surveys.size(), 2);
        surveys = SurveyOps.INSTANCE.getSurveysByEnumerator("email@domain.com", SurveyStatus.Submitted);
        assertEquals(surveys.size(), 1);
        assertEquals(surveys.get(0).getQuestionnaireId().longValue(), 789L);
        surveys = SurveyOps.INSTANCE.getSurveysByParticipant(456L, null);
        assertEquals(surveys.size(), 1);
        assertEquals(surveys.get(0).getQuestionnaireId().longValue(), 123L);
        surveys = SurveyOps.INSTANCE.getSurveysByParticipant(456L, SurveyStatus.Submitted);
        assertEquals(surveys.size(), 0);
        
        // test a delete
        SurveyOps.INSTANCE.deleteSurvey(survey2.getId(), false);
        surveys = SurveyOps.INSTANCE.getSurveysByEnumerator("email@domain.com", null);
        assertEquals(surveys.size(), 1);
        assertEquals(surveys.get(0).getId(), survey.getId());
    }
    
    @Test
    public void testSurveyAnswers ()
    {
        // create a new questionnaire object and store it
        Questionnaire questionnaire = new Questionnaire(new Text("Title"), new Text("Description"), new User("", ""));
        QuestionnaireOps.INSTANCE.saveQuestionnaire(questionnaire);
        Long questionnaireId = questionnaire.getId();
        
        // add some questions to the questionnaire
        Long question1Id = QuestionnaireOps.INSTANCE.saveQuestion(questionnaire,
            Question.createFreeText(QuestionCategory.Demographics, new Text("Residential Area"), true));
        QuestionnaireOps.INSTANCE.saveQuestion(questionnaire,
            Question.createFreeText(QuestionCategory.Demographics, new Text("School"), false));
        Long question3Id = QuestionnaireOps.INSTANCE.saveQuestion(questionnaire,
            Question.createNumeric(QuestionCategory.Study, new Text("Question 1"), true));
        Long question4Id = QuestionnaireOps.INSTANCE.saveQuestion(questionnaire, 
            Question.createMultipleChoice(QuestionCategory.Study, new Text("Question 2"), true,
                Lists.newArrayList(
                    new DiscreteValue(1, new Text("Option 1")),
                    new DiscreteValue(2, new Text("Option 2")),
                    new DiscreteValue(3, new Text("Option 3")))));
        Long question5Id = QuestionnaireOps.INSTANCE.saveQuestion(questionnaire,
            Question.createRange(QuestionCategory.Study, new Text("Question 3"), true, 1, 10));
        
        // create a survey and save it to the store
        Survey survey = new Survey(questionnaireId, new User("email@domain.com", "authDomain"), Language.English);
        SurveyOps.INSTANCE.saveSurvey(survey);
        
        // add some answers to the survey
        Answer answer1 = new Answer(question1Id, "Likoni");
        Answer answer3 = new Answer(question3Id, 7);
        Answer answer4 = new Answer(question4Id, 2);
        Answer answer5 = new Answer(question5Id, 8);    
        Set<Answer> answers = Sets.newHashSet(answer1, answer3, answer4, answer5);
        SurveyOps.INSTANCE.saveAnswers(survey, answers);
        assertEquals(survey.getAnswers().size(), 4);
        assertNotNull(answer1.getId());
        
        // validate the survey against the questionnaire
        assertEquals(questionnaire.validateSurvey(survey).size(), 0);
        
        // now let's cause some errors
        answer3.setValue(null);
        SurveyOps.INSTANCE.saveAnswers(survey, Sets.newHashSet(answer3));
        assertEquals(questionnaire.validateSurvey(survey).get(question3Id), QuestionError.MissingValue);
        answer1.setFreeText(null);
        SurveyOps.INSTANCE.saveAnswers(survey, Sets.newHashSet(answer1));
        assertEquals(questionnaire.validateSurvey(survey).get(question1Id), QuestionError.MissingText);
        answer5.setValue(0);
        SurveyOps.INSTANCE.saveAnswers(survey, Sets.newHashSet(answer5));
        assertEquals(questionnaire.validateSurvey(survey).get(question5Id), QuestionError.ValueTooLow);
        answer5.setValue(11);
        SurveyOps.INSTANCE.saveAnswers(survey, Sets.newHashSet(answer5));
        assertEquals(questionnaire.validateSurvey(survey).get(question5Id), QuestionError.ValueTooHigh);
        answer4.setValue(4);
        SurveyOps.INSTANCE.saveAnswers(survey, Sets.newHashSet(answer4));
        assertEquals(questionnaire.validateSurvey(survey).get(question4Id), QuestionError.InvalidOption);
    }
}
