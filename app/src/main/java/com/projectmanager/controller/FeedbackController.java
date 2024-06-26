package com.projectmanager.controller;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.projectmanager.entities.Feedback;
import com.projectmanager.entities.Usuario;
import com.projectmanager.forms.FeedbackForm;
import com.projectmanager.service.FeedbackService;
import com.projectmanager.service.GitService;
import com.projectmanager.config.Global;

@Controller
@RequestMapping("/user/{user_id}/repositories/{repo_name}/feedback")
public class FeedbackController {

    @Autowired
    @Qualifier(Global.GitClass)
    private GitService gitService; // Injete o serviço que obtém os repositórios do GitHub4
    @Autowired
    private OAuth2AuthorizedClientService oauth2AuthorizedClientService;
    @Autowired
    FeedbackService feedbackService;

    @GetMapping("")
    public String getFeedback(OAuth2AuthenticationToken authenticationToken, Model model,
            @PathVariable("repo_name") String repoName, @PathVariable("user_id") String user_id) {
        String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);
        try {
            Iterable<Feedback> feedbacks = feedbackService.getFeedbacksUsuarioProjeto(accessToken, repoName);

            Set<Usuario> collaborators = gitService.getRepositoryCollaborators(accessToken, repoName);
            model.addAttribute("user", feedbacks);
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("collaborators", collaborators);
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Erro ao obter feedbacks do projeto: " + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        }

        return "feedback";
    }

    @PostMapping("")
    public String createFeedback(OAuth2AuthenticationToken authenticationToken, Model model,
            @PathVariable("repo_name") String repoName, @ModelAttribute FeedbackForm novoFeedback,
            @PathVariable("user_id") String userId) {
        try {
            String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);
            System.out.println(novoFeedback.getMensagem());
            System.out.println(novoFeedback.getCollaborators());
            feedbackService.save(repoName, accessToken, novoFeedback);
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Erro ao criar feedback: " + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        }
        String redirect = "redirect:/user/" + userId + "/repositories/" + repoName + "/feedback";
        return redirect;
    }
}
