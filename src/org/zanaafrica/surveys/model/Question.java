package org.zanaafrica.surveys.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Serialize;

@Entity
public class Question
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private QuestionType type;
    private QuestionCategory category;
    private Text prompt;
    private Integer rangeMin;
    private Integer rangeMax;
    private Boolean required;
    
    @Serialize
    private List<DiscreteValue> options = new ArrayList<DiscreteValue>();

    ///////////////////////////////////////////////////////////////

    protected Question () { }
    
    protected Question (QuestionType type, QuestionCategory category, Text prompt, Integer rangeMin,
        Integer rangeMax, Boolean required, List<DiscreteValue> options) {
        this.type = type;
        this.category = category;
        this.prompt = prompt;
        this.rangeMin = rangeMin;
        this.rangeMax = rangeMax;
        this.required = required;
        this.options = options;
    }
    
    public static Question createMultipleChoice (QuestionCategory category, Text prompt, boolean required,
        List<DiscreteValue> options) {
        return new Question(QuestionType.MultipleChoice, category, prompt, null, null, required, options);
    }

    public static Question createNumeric (QuestionCategory category, Text prompt, boolean required) {
        return new Question(QuestionType.NumericRange, category, prompt, null, null, required, null);
    }

    public static Question createRange (QuestionCategory category, Text prompt, boolean required,
        Integer rangeMin, Integer rangeMax) {
        return new Question(QuestionType.NumericRange, category, prompt, rangeMin, rangeMax, required, null);
    }

    public static Question createFreeText (QuestionCategory category, Text prompt, boolean required) {
        return new Question(QuestionType.FreeText, category, prompt, null, null, required, null);
    }

    public static Question createMultipleChoiceWithFreeText (QuestionCategory category, Text prompt,
        boolean required, List<DiscreteValue> options) {
        return new Question(QuestionType.MCAndText, category, prompt, null, null, required, options);
    }

    public static Question createNumericWithFreeText (QuestionCategory category, Text prompt, boolean required) {
        return new Question(QuestionType.NumAndText, category, prompt, null, null, required, null);
    }

    public static Question createRangeWithFreeText (QuestionCategory category, Text prompt, boolean required,
        Integer rangeMin, Integer rangeMax) {
        return new Question(QuestionType.NumAndText, category, prompt, rangeMin, rangeMax, required, null);
    }

    ///////////////////////////////////////////////////////////////

    public Long getId () { return id; }
    public QuestionType getType () { return type; }
    public QuestionCategory getCategory () { return category; }
    public Text getPrompt () { return prompt; }
    public Integer getRangeMin () { return rangeMin; }
    public Integer getRangeMax () { return rangeMax; }
    public boolean isRequired () { return required; }
    public List<DiscreteValue> getOptions () { return options; }
    
    /**
     * Determines whether an answer to a particular question is valid. Questions that are marked as required 
     * must be answered, but unrequired questions that are left blank are always considered valid. 
     * For range questions, the value must be within the numeric range specified by the question.
     * For multiple choice questions, the value must match the numeric value assigned to one of the discrete values.
     * 
     * @param answer the answer to check (should match this question id)
     * @return null if the answer to this question is considered valid, otherwise the first error found
     * @author etaub
     * @since Oct 10, 2013
     */
    public QuestionError isValid (Answer answer) {
        if (required && type.hasFreeText() && (answer.getFreeText() == null || answer.getFreeText().trim().length() == 0))
            return QuestionError.MissingText;
        if (required && !type.isFreeTextOnly() && answer.getValue() == null)
            return QuestionError.MissingValue;
        if (type.hasNumberValue() && rangeMin != null && answer.getValue() < rangeMin)
            return QuestionError.ValueTooLow;
        if (type.hasNumberValue() && rangeMax != null && answer.getValue() > rangeMax)
            return QuestionError.ValueTooHigh;
        if (type.hasMultipleChoice() && options != null) {
            boolean found = false;
            for (DiscreteValue option : options)
                found |= (answer.getValue().equals(option.getValue()));
            if (!found)
                return QuestionError.InvalidOption;            
        }
        return null;
    }
}
