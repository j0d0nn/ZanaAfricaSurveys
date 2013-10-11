package org.zanaafrica.surveys.model;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.condition.IfNotNull;

@Entity
public class Survey
{
    public enum SurveyStatus { InProgress, Submitted }
    public static class LoadAnswers {}

    ///////////////////////////////////////////////////////////////

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Index
    private Long questionnaireId;

    @Index
    private String enumerator;
    
    @Index
    private SurveyStatus status;
    
    @Index(IfNotNull.class)
    private Long participantId;

    private Language language;
    private Date lastEdited;
    
    @Load(LoadAnswers.class)
    private Set<Ref<Answer>> answers = new HashSet<>();
    
    ///////////////////////////////////////////////////////////////

    protected Survey () { }
    
    public Survey (Long questionnaireId, User enumerator, Language language) {
        this.questionnaireId = questionnaireId;
        this.enumerator = enumerator.getNickname();
        this.status = SurveyStatus.InProgress;
        this.language = language;
    }

    ///////////////////////////////////////////////////////////////
    
    public Long getId () { return id; }
    public Long getQuestionnaireId () { return questionnaireId; }
    public String getEnumerator () { return enumerator; }
    public SurveyStatus getStatus () { return status; }
    public Long getParticipantId () { return participantId; }
    public Language getLanguage () { return language; }
    public Date getLastEdited () { return lastEdited; }
    
    public void submit () { this.status = SurveyStatus.Submitted; }
    public void setParticipantId (Long participantId) { this.participantId = participantId; }

    /**
     * @return a map of answers associated with this survey keyed by question id
     * @author etaub
     * @since Oct 8, 2013
     */
    public Map<Long, Answer> getAnswers () {
        Map<Long, Answer> answers = new HashMap<Long, Answer>(this.answers.size());
        for (Ref<Answer> answerRef : this.answers) {
            Answer answer = answerRef.get();
            answers.put(answer.getQuestionId(), answer);
        }
        return answers;
    }
    
    /**
     * @return the keys of the answers associated with this survey, in a read-only iterable
     * @author etaub
     * @since Oct 9, 2013
     */
    public Iterable<Key<Answer>> getAnswerKeys () { 
        Set<Key<Answer>> answerKeys = new HashSet<>();
        for (Ref<Answer> answerRef : this.answers)
            answerKeys.add(answerRef.key());
        return answerKeys; 
    }

    /**
     * Associates a collection of answers with this survey, overwriting existing answers with matching keys
     * @param answers the answers to add or update
     * @author etaub
     * @since Oct 8, 2013
     */
    public void addAnswers (Iterable<Answer> answers) {
        Set<Ref<Answer>> answerRefs = new HashSet<>();
        for (Answer answer : answers)
            answerRefs.add(Ref.create(answer));
        this.answers.addAll(answerRefs);
    }

    ///////////////////////////////////////////////////////////////
    
    @OnSave
    void updateLastEdited () { this.lastEdited = new Date(); }
}
