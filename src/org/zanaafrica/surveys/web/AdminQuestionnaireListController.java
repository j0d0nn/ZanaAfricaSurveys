package org.zanaafrica.surveys.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.zanaafrica.surveys.model.Questionnaire;
import org.zanaafrica.surveys.model.Text;

import static org.zanaafrica.surveys.model.Questionnaire.QuestionnaireStatus;

import org.zanaafrica.surveys.store.QuestionnaireOps;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Controller class for handling requests to /admin/**.  
 * @author jodonnell
 */
@Controller
@RequestMapping("/admin")
public class AdminQuestionnaireListController
{
    private Logger m_logger = Logger.getLogger(this.getClass().getName());

    /**
     * Handler for the questionnaire list page, which has a bunch of links to do other things.
     * @param model The model map context.
     * @return JSP link.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getList(final ModelMap model)
    {
        final List<Questionnaire> startedQuestionnaires 
            = QuestionnaireOps.INSTANCE.getQuestionnairesByStatus(QuestionnaireStatus.Started);
        model.addAttribute("noStartedQuestionnaires", startedQuestionnaires.isEmpty());
        model.addAttribute("startedQuestionnaires", startedQuestionnaires);
        final List<Questionnaire> activeQuestionnaires 
            = QuestionnaireOps.INSTANCE.getQuestionnairesByStatus(QuestionnaireStatus.Active);
        model.addAttribute("noActiveQuestionnaires", activeQuestionnaires.isEmpty());
        model.addAttribute("activeQuestionnaires", activeQuestionnaires);
        List<Questionnaire> retiredQuestionnaires 
            = QuestionnaireOps.INSTANCE.getQuestionnairesByStatus(QuestionnaireStatus.Retired);
        model.addAttribute("noRetiredQuestionnaires", retiredQuestionnaires.isEmpty());
        model.addAttribute("retiredQuestionnaires", retiredQuestionnaires);
        
        return "admin/index";
    }
    
    /**
     * Handler for the new questionnaire page.
     * @param model The model map context.
     * @return JSP link.
     */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String getNewQuestionnaire(final ModelMap map)
    {
        final ArrayList<Questionnaire> questionnaires = new ArrayList<>();
        questionnaires.addAll(
                QuestionnaireOps.INSTANCE.getQuestionnairesByStatus(QuestionnaireStatus.Started));
        questionnaires.addAll(
                QuestionnaireOps.INSTANCE.getQuestionnairesByStatus(QuestionnaireStatus.Active));
        map.addAttribute("nextQuestionnaires", questionnaires);
        return "admin/new";
    }
    
    /**
     * Handler for the questionnaire edit page.
     * @param id ID of the questionnaire to edit.
     * @param model The model map context.
     * @return JSP link.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getEditQuestionnaire(final @PathVariable String id, final ModelMap map)
    {
        //TODO
        return "admin/edit";
    }
    
    /**
     * Handler for the questionnaire save action, which is received by POST.  Users will be redirected back to the list page after
     * the action is complete.
     * @param title Questionnaire title.
     * @param description Questionnaire description.
     * @param next ID of the next questionnaire part.
     * @return Redirect instruction.
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String postSaveQuestionnaire(
            @RequestParam(required = true)  final String title,
            @RequestParam(required = false) final String description,
            @RequestParam(required = false) final String next
            )
    {
        final Text titleText = new Text(title);
        final Text descriptionText = new Text(description);
        final User user = UserServiceFactory.getUserService().getCurrentUser();
        Questionnaire questionnaire = new Questionnaire(titleText, descriptionText, user);
        QuestionnaireOps.INSTANCE.saveQuestionnaire(questionnaire);
        return this._redirectToAdminHome();
    }
    
    /**
     * Handler for the questionnaire activate action, which is received by POST.  Users will be redirected back to the list page after
     * the action is complete.
     * @param id Questionnaire ID.
     * @return Redirect instruction.
     */
    @RequestMapping(value = "/activate/{id}", method = RequestMethod.POST)
    public String postActivateQuestionnaire(@PathVariable final String id)
    {
        return this._alterQuestionnaireStatus(id, QuestionnaireStatus.Active);
    }
    
    /**
     * Handler for the questionnaire retire action, which is received by POST.  Users will be redirected back to the list page after
     * the action is complete.
     * @param id Questionnaire ID.
     * @return Redirect instruction.
     */
    @RequestMapping(value = "/retire/{id}", method = RequestMethod.POST)
    public String postRetireQuestionnaire(@PathVariable final String id)
    {
        return this._alterQuestionnaireStatus(id, QuestionnaireStatus.Retired);
    }

    /**
     * Loads a questionnaire by ID, updates its status, and saves.  Returns a redirect action.
     * @param sId The ID.
     * @param status The status to set.
     * @return Redirect instruction to the admin list page.
     */
    private String _alterQuestionnaireStatus(final String sId, final QuestionnaireStatus status)
    {
        try
        {
            final long id = Long.parseLong(sId); 
            Questionnaire questionnaire = QuestionnaireOps.INSTANCE.loadQuestionnaire(id);
            if (null == questionnaire)
            {
                m_logger.warning(String.format("Activate command issued with invalid ID: %s", sId));                
            }
            else
            {
                questionnaire.setStatus(status);
                QuestionnaireOps.INSTANCE.saveQuestionnaire(questionnaire);
            }
        }
        catch (NumberFormatException e)
        {
            m_logger.warning(String.format("Activate command issued with invalid ID: %s", sId));
        }
        return this._redirectToAdminHome();
        
    }
    
    private String _redirectToAdminHome()
    {
        return "redirect:/admin/";
    }
}