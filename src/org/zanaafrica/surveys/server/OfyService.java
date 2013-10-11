package org.zanaafrica.surveys.server;

import org.zanaafrica.surveys.model.Answer;
import org.zanaafrica.surveys.model.Participant;
import org.zanaafrica.surveys.model.Question;
import org.zanaafrica.surveys.model.Questionnaire;
import org.zanaafrica.surveys.model.Survey;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

public class OfyService
{
    static {
        factory().register(Answer.class);
        factory().register(Participant.class);
        factory().register(Question.class);
        factory().register(Questionnaire.class);
        factory().register(Survey.class);
    }
    
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
