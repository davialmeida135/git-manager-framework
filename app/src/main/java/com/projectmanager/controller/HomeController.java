package com.projectmanager.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projectmanager.entities.Usuario;
import com.projectmanager.model.UsuarioModel;
import com.projectmanager.service.GitService;

import com.projectmanager.service.UsuarioService;

@Controller
public class HomeController {

    @Autowired
    @Qualifier("GithubService2")
    private GitService gitService; // Injete o serviço que obtém os repositórios do GitHub

    @Autowired
    private OAuth2AuthorizedClientService oauth2AuthorizedClientService; // Injete o serviço de cliente autorizado
                                                                         // OAuth2

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String getIndex(OAuth2AuthenticationToken authenticationToken) {
        if (gitService.isAuthenticated(authenticationToken)) {
            return "redirect:/home";
        }
        return "index";
    }

    @GetMapping("/home")
    public String getHome(Model model, OAuth2AuthenticationToken authenticationToken) throws IOException {
        if (!gitService.isAuthenticated(authenticationToken)) {
            return "redirect:/";
        }
        return processAuthenticatedUser(model, authenticationToken);
    }

    @GetMapping("/projects")
    public String getProjects(Model model, OAuth2AuthenticationToken authenticationToken) {
        if (!gitService.isAuthenticated(authenticationToken)) {
            return "redirect:/";
        }

        // TODO: alterar a rota
        try {
            String userId = gitService.getUserId(authenticationToken,oauth2AuthorizedClientService);
            return "redirect:/user/" + userId + "/projects";
        } catch (IOException e) {
            model.addAttribute(e.getMessage());
            return "error";
        }
        
    }

    @GetMapping("/repositories")
    public String getRepositories(Model model, OAuth2AuthenticationToken authenticationToken) {
        if (!gitService.isAuthenticated(authenticationToken)) {
            return "redirect:/";
        }
       
        try {
            String userId = gitService.getUserId(authenticationToken,oauth2AuthorizedClientService);
            return "redirect:/user/" + userId + "/repositories";
        } catch (IOException e) {
            model.addAttribute(e.getMessage());
            return "error";
        }
        
    }

    @GetMapping("/sobre")
    public String getSobre(Model model, OAuth2AuthenticationToken authenticationToken) {
        if (gitService.isAuthenticated(authenticationToken)) {
            // Se o usuário estiver autenticado, obtenha suas informações e adicione ao
            // modelo
            String accessToken = gitService.getAccessToken(authenticationToken,
                    oauth2AuthorizedClientService);
            
            try {
                UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken);
                model.addAttribute("user", loggedUser);
            } catch (IOException e) {
                model.addAttribute(e.getMessage());
                return "error";
            }
            
        }

        return "sobre";
    }

    private String processAuthenticatedUser(Model model, OAuth2AuthenticationToken authenticationToken){
        String accessToken = gitService.getAccessToken(authenticationToken,  oauth2AuthorizedClientService);
        try {
            Usuario usuario = gitService.getUsuario(accessToken);
            usuarioService.save(usuario);

        return "redirect:/user/" + Integer.toString(usuario.getId());
        }
        catch (IOException e) {
            model.addAttribute(e.getMessage());
            return "error";
        }
    }
}