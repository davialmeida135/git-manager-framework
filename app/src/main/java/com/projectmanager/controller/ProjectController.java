package com.projectmanager.controller;

import java.io.IOException;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model; // Importe a classe Model

import com.projectmanager.config.Global;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.service.ColaboradorService;
import com.projectmanager.service.GitService;

import com.projectmanager.service.ProjetoService;
import com.projectmanager.service.TarefaServiceAbs;

@Controller
@RequestMapping("/user/{user_id}/projects")
public class ProjectController {

    @Autowired
    @Qualifier(Global.GitClass)
    private GitService gitService;

    @Autowired
    OAuth2AuthorizedClientService oauth2AuthorizedClientService;

    @Autowired
    ProjetoService projetoService;

    @Autowired
    TarefaServiceAbs tarefaService;

    @Autowired
    ColaboradorService colaboradorService;

    @GetMapping("")
    public String getUserProjects(@PathVariable("user_id") String userId,
            OAuth2AuthenticationToken authenticationToken,
            Model model) {
        String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);
        Collection<RepositoryModel> projects;
        try {
            projects = projetoService.getMatchingProjects(accessToken);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/home";
        }

        model.addAttribute("user", userId);
        model.addAttribute("objeto_da_lista", "Projects");
        model.addAttribute("repositories", projects);

        return "repositories";
    }
}