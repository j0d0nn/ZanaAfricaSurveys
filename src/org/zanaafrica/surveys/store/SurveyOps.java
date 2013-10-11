package org.zanaafrica.surveys.store;

import static org.zanaafrica.surveys.server.OfyService.ofy;
import java.util.List;
import javax.annotation.Nullable;
import org.zanaafrica.surveys.model.Answer;
import org.zanaafrica.surveys.model.Survey;
import org.zanaafrica.surveys.model.Survey.LoadAnswers;
import org.zanaafrica.surveys.model.Survey.SurveyStatus;
import com.googlecode.objectify.cmd.Query;

public enum SurveyOps
{
    INSTANCE; 
 
    ///////////////////////////////////////////////////////////////

    /**
     * Loads a survey by its unique id along with all of its answers
     * 
     * @param id the id of the survey to load
     * @return the full survey
     * @author etaub
     * @since Oct 10, 2013
     */
    public Survey loadSurvey (Long id) {
        return ofy().load().group(LoadAnswers.class).type(Survey.class).id(id).now();
    }

    /**
     * Gets all surveys corresponding to a given questionnaire. This will get the basic information
     * for each matching survey but will not necessarily retrieve the answer data.
     * 
     * @param questionnaireId the questionnaire id to filter by
     * @param status an optional parameter that can be used to further filter the surveys by status
     * @return the list of surveys that match, possibly minus the answers
     * @author etaub
     * @since Oct 10, 2013
     */
    public List<Survey> getSurveysByQuestionnaire (Long questionnaireId, @Nullable SurveyStatus status) {
        Query<Survey> query = ofy().load().type(Survey.class).filter("questionnaireId", questionnaireId);
        return (status == null) ? query.list() : query.filter("status", status).list();
    }

    /**
     * Gets all surveys entered by a given enumerator. This will get the basic information
     * for each matching survey but will not necessarily retrieve the answer data.
     * 
     * @param enumerator the nickname of the GAE User object corresponding to the enumerator to filter by
     * @param status an optional parameter that can be used to further filter the surveys by status
     * @return the list of surveys that match, possibly minus the answers
     * @author etaub
     * @since Oct 10, 2013
     */
    public List<Survey> getSurveysByEnumerator (String enumerator, @Nullable SurveyStatus status) {
        Query<Survey> query = ofy().load().type(Survey.class).filter("enumerator", enumerator);
        return (status == null) ? query.list() : query.filter("status", status).list();
    }
    
    /**
     * Gets all surveys taken by a given participant. This will get the basic information
     * for each matching survey but will not necessarily retrieve the answer data.
     * 
     * @param participantId the id of the participant to filter by
     * @param status an optional parameter that can be used to further filter the surveys by status
     * @return the list of surveys that match, possibly minus the answers
     * @author etaub
     * @since Oct 10, 2013
     */
    public List<Survey> getSurveysByParticipant (Long participantId, @Nullable SurveyStatus status) {
        Query<Survey> query = ofy().load().type(Survey.class).filter("participantId", participantId);
        return (status == null) ? query.list() : query.filter("status", status).list();
    }
    
    /**
     * Saves a new or modified questionnaire to the store and updates the "last edited" date 
     * 
     * @param survey the survey to store
     * @return the id of the question saved
     * @author etaub
     * @since Oct 10, 2013
     */
    public Long saveSurvey (Survey survey) {
        ofy().save().entity(survey).now();
        return survey.getId();
    }
    
    /**
     * Saves one or more answers to the store (inserting or updating them as necessary) and
     * associates them with a given survey, finally saving the survey changes
     * 
     * @param survey the survey to which these answers should be associated
     * @param answers the new (or modified) answers to store
     * @author etaub
     * @since Oct 10, 2013
     */
    public void saveAnswers (Survey survey, Iterable<Answer> answers) {
        ofy().save().entities(answers).now(); // new answers will be given ids
        survey.addAnswers(answers);
        ofy().save().entity(survey).now();
    }
    
    /**
     * Deletes a survey from the store entirely, optionally deleting its answers as well.
     * 
     * @param id the id of the survey to delete
     * @param deleteAnswers whether or not the survey's answers should also be deleted
     * @author etaub
     * @since Oct 10, 2013
     */
    public void deleteSurvey (Long id, boolean deleteAnswers) {
        if (deleteAnswers) {
            Survey survey = ofy().load().type(Survey.class).id(id).now();
            ofy().delete().keys(survey.getAnswerKeys());
        }
        ofy().delete().type(Survey.class).id(id).now();
    }
}
