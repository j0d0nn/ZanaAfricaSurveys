package org.zanaafrica.surveys.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Questionnaire
{
    public enum QuestionnaireStatus { Started, Active, Retired }
    public static class LoadQuestions {}
    
    ///////////////////////////////////////////////////////////////

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Text title;
    private String author;
    private Text description;
    private Long nextPartQuestionnaireId;    
    
    @Index
    private QuestionnaireStatus status;
    
    @Load(LoadQuestions.class)
    private List<Ref<Question>> questions = new ArrayList<>();

    ///////////////////////////////////////////////////////////////

    protected Questionnaire () { }
    
    public Questionnaire (Text title, Text description, User author) {
        this.title = title;
        this.author = author.getNickname();
        this.description = description;
        this.status = QuestionnaireStatus.Started;        
    }
    
    ///////////////////////////////////////////////////////////////
    
    public Long getId () { return id; }
    public Text getTitle () { return title; }
    public String getAuthor () { return author; }
    public Text getDescription () { return description; }
    public QuestionnaireStatus getStatus () { return status; }
    public Long getNextPartQuestionnaireId () { return nextPartQuestionnaireId; }
    
    public void setTitle (Text title) { this.title = title; }
    public void setDescription (Text description) { this.description = description; }
    public void activate () { this.status = QuestionnaireStatus.Active; }
    public void retire () { this.status = QuestionnaireStatus.Retired; }
    public void setStatus (QuestionnaireStatus status) { this.status = status; }
    
    /**
     * @return true if this questionnaire has at least one question loaded from the store
     * @author etaub
     * @since Oct 10, 2013
     */
    public boolean hasQuestionsLoaded () {
        return questions != null && questions.size() > 0 && questions.get(0).isLoaded(); 
    }
    
    /**
     * @return a read-only ordered list of questions associated with this questionnaire
     * @author etaub
     * @since Oct 8, 2013
     */
    public List<Question> getQuestions () {
        List<Question> questions = new ArrayList<Question>(this.questions.size());
        for (Ref<Question> question : this.questions)
            questions.add(question.get());
        return questions;
    }
    
    /**
     * Adds a new question to the end of this questionnaire if it doesn't already exist
     * @param question the new question
     * @return true if the question was added; false if the question already exists
     * @author etaub
     * @since Oct 8, 2013
     */
    public boolean addQuestion (Question question) {
        Ref<Question> questionRef = Ref.create(question);
        if (this.questions.contains(questionRef))
            return false;
        else {
            this.questions.add(questionRef);
            return true;
        }
    }
    
    /**
     * Moves a question already in this questionnaire to a new position, adjusting all questions
     * @param questionId the id of the question to move
     * @param newPosition the new position to place the question (0-based)
     * @author etaub
     * @since Oct 8, 2013
     */
    public void moveQuestion (Long questionId, int newPosition) {
        Ref<Question> question = Ref.create(Key.create(Question.class, questionId));
        this.questions.remove(question);
        this.questions.add(newPosition, question);
    }
    
    /**
     * Removes a question from this questionnaire
     * @param questionId the id of the question to delete
     * @author etaub
     * @since Oct 8, 2013
     */
    public void removeQuestion (Long questionId) {
        Ref<Question> question = Ref.create(Key.create(Question.class, questionId));
        this.questions.remove(question);        
    }
    
    /**
     * Validates an entire survey against this questionnaire. This method assumes that the survey
     * matches the questionnaire, the answers match the questions, the questionnaire has all questions
     * loaded, and the survey has all answers loaded.
     * 
     * If there are no errors, then an empty map will be returned. If there are any errors, this method
     * will return a map of question id to error for all errors found.
     * 
     * @param survey the survey to validate against this questionnaire
     * @return an empty map or a map of question id to error
     * @author etaub
     * @since Oct 10, 2013
     */
    public Map<Long, QuestionError> validateSurvey (Survey survey) {
        Map<Long, Answer> answerMap = survey.getAnswers();
        Map<Long, QuestionError> errorMap = new HashMap<>();
        for (Question question : getQuestions()) {
            QuestionError error = question.isValid(answerMap.get(question.getId()));
            if (error != null)
                errorMap.put(question.getId(), error);
        }
        return errorMap;
    }
}
