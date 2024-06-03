package com.projectmanager.controller;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.service.FeedbackService;
import com.projectmanager.service.GithubAPIService;
import org.kohsuke.github.GHMyself;

@Controller
@RequestMapping("/user/{user_id}/repositories/{repo_name}/feedback")
public class FeedbackController {
    @Autowired
    private OAuth2AuthorizedClientService oauth2AuthorizedClientService;
    @Autowired
    FeedbackService feedbackService;
    @Autowired
    GithubAPIService githubService;
    
    @GetMapping("")
    public String getFeedback(OAuth2AuthenticationToken authenticationToken, Model model,@PathVariable("repo_name") String repoName) {
        String accessToken = githubService.getAccessToken(authenticationToken, "github", oauth2AuthorizedClientService);
        try {
            GHMyself user = githubService.getUser(accessToken);
            Iterable<Feedback> feedbacks = feedbackService.getFeedbacksUsuarioProjeto(accessToken,repoName);
            RepositoryModel repositoryModel = githubService.getRepositoryModel(user, repoName);
            Set<Usuario> collaborators = repositoryModel.getCollaborators();
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("collaborators", collaborators);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "feedback";
    }

    @PostMapping("")
    public String createFeedback(OAuth2AuthenticationToken authenticationToken, Model model,
     @PathVariable("repo_name") String repoName,@ModelAttribute FeedbackForm novoFeedback,
     @PathVariable("user_id") String userId) {
        try {
            String accessToken = githubService.getAccessToken(authenticationToken, "github", oauth2AuthorizedClientService);
            System.out.println(novoFeedback.getMensagem());
            System.out.println(novoFeedback.getCollaborators());
            feedbackService.save(repoName, accessToken , novoFeedback);
        }
        catch (IOException e){
            e.printStackTrace();
            model.addAttribute(e.getMessage());
            return "error";
        }
        String redirect = "redirect:/user/" + userId + "/repositories/" + repoName + "/feedback";
        return redirect;
    }
}
