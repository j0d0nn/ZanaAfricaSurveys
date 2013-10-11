package org.zanaafrica.surveys.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Answer
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long questionId;    
    private Integer value;  
    private String freeText;
    
    ///////////////////////////////////////////////////////////////
    
    protected Answer () { }    
    public Answer (Long questionId) { this.questionId = questionId; }
    public Answer (Long questionId, Integer value) { this(questionId); this.value = value; }
    public Answer (Long questionId, String freeText) { this(questionId); this.freeText = freeText; }
    public Answer (Long questionId, Integer value, String freeText) { this(questionId, value); this.freeText = freeText; }
    
    ///////////////////////////////////////////////////////////////

    public Long getId () { return id; }
    public Long getQuestionId () { return questionId; }
    public Integer getValue () { return value; }
    public String getFreeText () { return freeText; }

    public void setValue (Integer value) { this.value = value; }
    public void setFreeText (String freeText) { this.freeText = freeText; }
}
