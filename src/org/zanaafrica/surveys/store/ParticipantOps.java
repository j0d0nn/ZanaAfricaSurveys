package org.zanaafrica.surveys.store;

import static org.zanaafrica.surveys.server.OfyService.ofy;
import java.util.Date;
import java.util.List;
import org.zanaafrica.surveys.model.Participant;
import org.zanaafrica.surveys.model.Survey;

public enum ParticipantOps
{
    INSTANCE; 

    ///////////////////////////////////////////////////////////////

    /**
     * Loads a participant from the store by its id
     * 
     * @param id the unique id of the participant
     * @return the participant
     * @author etaub
     * @since Oct 10, 2013
     */
    public Participant loadParticipant (Long id) {
        return ofy().load().type(Participant.class).id(id).now();
    }
    
    /**
     * Retrieves the participant who took a particular survey.
     * 
     * @param survey the survey to filter by
     * @return the participant associated with the given survey (or null)
     * @author etaub
     * @since Oct 10, 2013
     */
    public Participant getParticipantForSurvey (Survey survey) {
        return (survey == null || survey.getParticipantId() == null) ? null :
            ofy().load().type(Participant.class).id(survey.getParticipantId()).now();
    }

    /**
     * Retrieves a participant by his/her UIN, some unique identification string
     * 
     * @param uin the UIN
     * @return the participant that matches this UIN (or null)
     * @author etaub
     * @since Oct 10, 2013
     */
    public Participant getParticipantByUin (String uin) {
        return ofy().load().type(Participant.class).filter("uin", uin).first().now();
    }

    /**
     * Retrieves a list of participants by name
     * 
     * @param name the full name of the participant(s)
     * @return any participants who have the specified name (or an empty list)
     * @author etaub
     * @since Oct 10, 2013
     */
    public List<Participant> getParticipantsByName (String name) {
        return ofy().load().type(Participant.class).filter("name", name).list();
    }

    /**
     * Retrieves a list of participants by birthday
     * 
     * @param name the birthday date (with time set to 00:00:00)
     * @return any participants who have the specified birthday (or an empty list)
     * @author etaub
     * @since Oct 10, 2013
     */
    public List<Participant> getParticipantsByBirthday (Date birthday) {
        return ofy().load().type(Participant.class).filter("birthday", birthday).list();
    }

    /**
     * Retrieves a list of participants by phone number
     * 
     * @param name the phone number
     * @return any participants who have the specified phone number (or an empty list)
     * @author etaub
     * @since Oct 10, 2013
     */
    public List<Participant> getParticipantsByPhoneNumber (String phoneNumber) {
        return ofy().load().type(Participant.class).filter("phoneNumber", phoneNumber).list();
    }
    
    /**
     * Save a new participant to the store, or update an existing participant record that
     * has been modified
     * 
     * @param participant the participant to store
     * @return the id of the participant saved
     * @author etaub
     * @since Oct 10, 2013
     */
    public Long saveParticipant (Participant participant) {
        ofy().save().entity(participant).now();
        return participant.getId();
    }
    
    /**
     * Delete a participant from the store. This method will not delete any surveys that this
     * participant has taken.
     * 
     * @param id
     * @author etaub
     * @since Oct 10, 2013
     */
    public void deleteParticipant (Long id) {
        ofy().delete().type(Participant.class).id(id).now();
    }
}
