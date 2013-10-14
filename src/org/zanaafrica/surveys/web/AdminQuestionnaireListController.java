package org.zanaafrica.surveys.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zanaafrica.surveys.model.Questionnaire;
import org.zanaafrica.surveys.store.QuestionnaireOps;

@Controller
@RequestMapping("/admin")
public class AdminQuestionnaireListController
{

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getList(final ModelMap model)
    {
        final List<Questionnaire> startedQuestionnaires 
            = QuestionnaireOps.INSTANCE.getQuestionnairesByStatus(Questionnaire.QuestionnaireStatus.Started);
        model.addAttribute("noStartedQuestionnaires", startedQuestionnaires.isEmpty());
        model.addAttribute("startedQuestionnaires", startedQuestionnaires);
        final List<Questionnaire> activeQuestionnaires 
            = QuestionnaireOps.INSTANCE.getQuestionnairesByStatus(Questionnaire.QuestionnaireStatus.Active);
        model.addAttribute("noActiveQuestionnaires", activeQuestionnaires.isEmpty());
        model.addAttribute("activeQuestionnaires", activeQuestionnaires);
        List<Questionnaire> retiredQuestionnaires 
            = QuestionnaireOps.INSTANCE.getQuestionnairesByStatus(Questionnaire.QuestionnaireStatus.Retired);
        model.addAttribute("noRetiredQuestionnaires", retiredQuestionnaires.isEmpty());
        model.addAttribute("retiredQuestionnaires", retiredQuestionnaires);
        
        return "admin/index";
    }
    
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String getNewQuestionnaire(final ModelMap map)
    {
        return "admin/new";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getEditQuestionnaire(final @PathVariable String id, final ModelMap map)
    {
        return "admin/edit";
    }
    
}