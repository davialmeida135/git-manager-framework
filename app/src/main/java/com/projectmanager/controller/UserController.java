package com.projectmanager.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.projectmanager.config.Global;
import com.projectmanager.entities.Projeto;
import com.projectmanager.entities.Tarefa;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.service.ColaboradorService;
import com.projectmanager.service.GitService;

import com.projectmanager.service.ProjetoService;
import com.projectmanager.service.TarefaServiceAbs;

@Controller
public class UserController {

    @Autowired
    @Qualifier(Global.GitClass)
    private GitService gitService; // Injete o serviço que obtém os repositórios do GitHub

    @Autowired
    private OAuth2AuthorizedClientService oauth2AuthorizedClientService;

    @Autowired
    ColaboradorService colaboradorService;

    @Autowired
    ProjetoService projetoService;

    @Autowired
    TarefaServiceAbs tarefaService;

    @GetMapping("/user/{user_id}")
    public String getUserPage(@PathVariable("user_id") String userId, OAuth2AuthenticationToken authenticationToken,
            Model model) {
        String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);

        try {
            int userIdInt = Integer.parseInt(userId);
            model.addAttribute("user", userId);
            Iterable<Tarefa> tasks_list_byUser = colaboradorService.findTasksByIDUser(userIdInt, tarefaService);

            List<Tarefa> tasks = new ArrayList<>();
            tasks_list_byUser.forEach(tasks::add);

            tasks = colaboradorService.sortTasks(tasks);

            Map<Tarefa, Projeto> tarefaProjetoMap = new HashMap<>();
            for (Tarefa tarefa : tasks) {
                Projeto projeto = projetoService.find(tarefa.getId_projeto());
                tarefaProjetoMap.put(tarefa, projeto);
            }

            model.addAttribute("tarefaProjetoMap", tarefaProjetoMap);
            model.addAttribute("tarefas", tasks);

            List<RepositoryModel> repositories = gitService.getRepositories(accessToken);
            Collection<RepositoryModel> projects = (Collection<RepositoryModel>) projetoService
                    .findTop3ByOrderByDataCriacaoDesc(repositories);

            model.addAttribute("repositories", projects);
        } catch (NumberFormatException e) {
            model.addAttribute("errorMessage", "Erro ao transformar o Id do usuário. " + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Erro ao buscar os repositórios do usuário. " + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        }

        return "user";
    }

    @GetMapping("/logout")
    public String logout(OAuth2AuthenticationToken authenticationToken) {
        if (authenticationToken != null) {
            gitService.removeAccessToken(authenticationToken, oauth2AuthorizedClientService);
            oauth2AuthorizedClientService.removeAuthorizedClient("eab0701da41462780733", authenticationToken.getName());
        }

        return "redirect:/";
    }
}
