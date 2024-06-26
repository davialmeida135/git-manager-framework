package com.projectmanager.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.ui.Model; // Importe a classe Model

import com.projectmanager.config.Global;
import com.projectmanager.entities.Cronograma;
import com.projectmanager.entities.ScheduledActivity;
import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.Usuario;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;
import com.projectmanager.service.ColaboradorService;
import com.projectmanager.service.CronogramaService;
import com.projectmanager.service.GitService;

import com.projectmanager.service.ProjetoService;
import com.projectmanager.service.TarefaServiceAbs;

@Controller
@RequestMapping("/user/{user_id}/repositories")
public class RepositoryController {

    @Autowired
    @Qualifier(Global.GitClass)
    private GitService gitService; // Injete o serviço que obtém os repositórios do GitHub

    @Autowired
    private OAuth2AuthorizedClientService oauth2AuthorizedClientService;

    @Autowired
    ProjetoService projetoService;
    @Autowired
    TarefaServiceAbs tarefaService;
    @Autowired
    CronogramaService cronogramaService;
    @Autowired
    ColaboradorService colaboradorService;

    @GetMapping("")
    public String getUserRepositories(@PathVariable("user_id") String user_id,
            OAuth2AuthenticationToken authenticationToken, Model model) {
        String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);

        // Obter todos os repositórios do usuário no GitHub
        try {
            UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken);

            // Verifica se o usuário logado está acessando a própria página
            if (!user_id.equals(Long.toString(loggedUser.getId()))) {
                model.addAttribute("errorMessage", "Usuário inválido.");
                return "error";
            }
            Collection<RepositoryModel> repositories = gitService.getRepositories(accessToken);
            model.addAttribute("repositories", repositories);
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Erro ao obter os repositórios do usuário: " + e.getMessage());
            model.addAttribute("errorDetails" + e.toString());
            return "error";
        }
        model.addAttribute("objeto_da_lista", "Repositories");
        model.addAttribute("user", user_id);
        return "repositories";
    }

    // Página principal do repositório
    @GetMapping("/{repo_name}")
    public String getRepository(@PathVariable("user_id") String user_id, @PathVariable("repo_name") String repoName,
            OAuth2AuthenticationToken authenticationToken, Model model) {
        String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);

        try {
            UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken); // Objeto do usuario
            gitService.validateUser(loggedUser, user_id);
            projetoService.save(accessToken, repoName);
            model.addAttribute("user", loggedUser);

            RepositoryModel repo = gitService.getRepository(accessToken, repoName);// Objeto do repositório
            Set<Usuario> collab = gitService.getRepositoryCollaborators(accessToken, repoName);
            model.addAttribute("collaborators", collab);
            model.addAttribute("repository", repo);

        } catch (IOException e) {
            model.addAttribute("errorMessage", "Erro ao obter repositório: " + e.getMessage());
            model.addAttribute("errorDetails" + e.toString());
            return "error";
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", "Erro ao validar usuário: " + e.getMessage());
            model.addAttribute("errorDetails" + e.toString());
            return "error";
        }

        return "project";
    }

    @GetMapping("/{repo_name}/cronograma")
    public String getRepositoryCronograma(@PathVariable("user_id") String user_id,
            @PathVariable("repo_name") String repoName,
            OAuth2AuthenticationToken authenticationToken, Model model) {
        String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);

        try {
            UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken); // Objeto do usuario
            gitService.validateUser(loggedUser, user_id);
            RepositoryModel repo = gitService.getRepository(accessToken, repoName);

            // Criação de um cronograma para o projeto
            Collection<Cronograma> cronogramas = cronogramaService.getCronogramasProjeto((int) repo.getId());
            Collection<Tarefa> tarefas = tarefaService.getTaskByProject((int) repo.getId());

            Collection<ScheduledActivity> schedule = new ArrayList<ScheduledActivity>();
            schedule.addAll(cronogramas);
            schedule.addAll(tarefas);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            List<ScheduledActivity> sortedSchedule = schedule.stream()
                    .sorted(Comparator.comparing(s -> LocalDate.parse(s.getPrazo(), formatter)))
                    .collect(Collectors.toList());
            // TODO levar criação do cronograma para o service

            model.addAttribute("schedule", sortedSchedule);
            model.addAttribute("repository", repo);
            model.addAttribute("user", user_id);

        } catch (IOException e) {
            model.addAttribute("errorMessage", "Erro ao obter cronograma: " + e.getMessage());
            model.addAttribute("errorDetails" + e.toString());
            return "error";
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", "Erro ao validar usuário: " + e.getMessage());
            model.addAttribute("errorDetails" + e.toString());
            return "error";
        }
        return "cronograma";
    }

    @PostMapping("/{repo_name}/cronograma/new")
    public String createNewTask(@PathVariable("user_id") String user_id, @PathVariable("repo_name") String repoName,
            OAuth2AuthenticationToken authenticationToken, Model model, @ModelAttribute Cronograma newCronograma) {

        String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);
        System.out.println("fase1");
        try {
            UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken); // Objeto do usuario
            gitService.validateUser(loggedUser, user_id);
            RepositoryModel repo = gitService.getRepository(accessToken, repoName);
            System.out.println("fase2");
            newCronograma.setProjeto_id((int) repo.getId());

            cronogramaService.save(newCronograma);
            System.out.println("fase3");
            return "redirect:/user/" + user_id + "/repositories/" + repoName + "/cronograma";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Erro ao criar nova tarefa no cronograma: " + e.getMessage());
            model.addAttribute("errorDetails" + e.toString());
            return "error";
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", "Erro ao validar usuário: " + e.getMessage());
            model.addAttribute("errorDetails" + e.toString());
            return "error";
        }
    }

}