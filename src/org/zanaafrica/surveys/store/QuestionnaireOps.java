package org.zanaafrica.surveys.store;

import static org.zanaafrica.surveys.server.OfyService.ofy;
import java.util.List;
import org.zanaafrica.surveys.model.Question;
import org.zanaafrica.surveys.model.Questionnaire;
import org.zanaafrica.surveys.model.Questionnaire.LoadQuestions;
import org.zanaafrica.surveys.model.Questionnaire.QuestionnaireStatus;

public enum QuestionnaireOps
{
    INSTANCE; 
    
    ///////////////////////////////////////////////////////////////

    /**
     * Loads a questionnaire by its unique id along with all of its questions
     * 
     * @param id the id of the questionnaire to load
     * @return the full questionnaire
     * @author etaub
     * @since Oct 8, 2013
     */
    public Questionnaire loadQuestionnaire (Long id) {
        return ofy().load().group(LoadQuestions.class).type(Questionnaire.class).id(id).now();
    }

    /**
     * Gets all questionnaires of a given status (e.g. active). This will get the basic information
     * for each matching questionnaire but will not necessarily retrieve the question data.
     * 
     * @param status the status filter
     * @return the list of questionnaires that match, possibly minus the questions
     * @author etaub
     * @since Oct 8, 2013
     */
    public List<Questionnaire> getQuestionnairesByStatus (QuestionnaireStatus status) {
        return ofy().load().type(Questionnaire.class).filter("status", status).list();
    }
    
    /**
     * Saves a new or modified questionnaire to the store 
     * 
     * @param questionnaire the questionnaire to store
     * @return the id of the questionnaire saved
     * @author etaub
     * @since Oct 8, 2013
     */
    public Long saveQuestionnaire (Questionnaire questionnaire) {
        ofy().save().entity(questionnaire).now();
        return questionnaire.getId();
    }
    
    /**
     * Saves a new or modified question to the store, associates it with a questionnaire, 
     * and then saves the questionnaire
     * 
     * @param questionnaire the questionnaire to which this question should be associated
     * @param question the new or modified question to save
     * @return the id of the question saved
     * @author etaub
     * @since Oct 8, 2013
     */
    public Long saveQuestion (Questionnaire questionnaire, Question question) {
        ofy().save().entity(question).now(); // new question will be given id
        if (questionnaire.addQuestion(question))
            ofy().save().entity(questionnaire).now();
        return question.getId();
    }
}
