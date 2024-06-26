package com.projectmanager.controller;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projectmanager.config.Global;
import com.projectmanager.entities.Projeto;
import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.Usuario;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.forms.TarefaForm;
import com.projectmanager.model.ComentarioModel;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;
import com.projectmanager.service.ComentarioService;
import com.projectmanager.service.GitService;

import com.projectmanager.service.ProjetoService;
import com.projectmanager.service.TarefaServiceAbs;


@Controller
@RequestMapping("/user/{user_id}/repositories/{repo_name}/tasks")
public class TarefaController {

    @Autowired
    @Qualifier(Global.GitClass)
    private GitService gitService; // Injete o serviço que obtém os repositórios do GitHub

    @Autowired
    private OAuth2AuthorizedClientService oauth2AuthorizedClientService;

    @Autowired
    TarefaServiceAbs tarefaService;

    @Autowired
    ComentarioService comentarioService;

    @Autowired
    ProjetoService projetoService;

    @GetMapping("")
    public String getUserTarefas(Model model, @PathVariable("repo_name") String repoName,
            @PathVariable("user_id") String user_id, OAuth2AuthenticationToken authenticationToken,
            @RequestParam(value = "error", required = false) String errorMessage) {
        String accessToken = gitService.getAccessToken(authenticationToken,  oauth2AuthorizedClientService);

        model.addAttribute("error", errorMessage);
        model.addAttribute("user", user_id);
            
        try {
            Map<Tarefa, Projeto> tarefaProjetoMap = new HashMap<>();
            UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken); // Objeto do usuario
            gitService.validateUser(loggedUser, user_id);
            RepositoryModel repo = gitService.getRepository(accessToken, repoName);
            int repoId = (int) repo.getId();
            Collection<Tarefa> tasks = tarefaService.getTaskByProject(repoId);
            model.addAttribute("tarefas", tasks);
            for (Tarefa tarefa : tasks) {
                Projeto projeto = projetoService.find(tarefa.getId_projeto());
                tarefaProjetoMap.put(tarefa, projeto);
            }
            model.addAttribute("tarefaProjetoMap", tarefaProjetoMap);
            RepositoryModel repository = gitService.getRepository(accessToken, repoName);// Objeto do repositório
            model.addAttribute("repository", repository);
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Falha ao conectar ao serviço de repositório." + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        } catch (BusinessException e){
            model.addAttribute("errorMessage", "Erro ao buscar as tarefas. Verifique se possui permissão para acessar este repositório." + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        }
        return "tarefas";
    }

    @GetMapping("/{tarefa_id}")
    public String getTarefa(@PathVariable("user_id") String user_id, @PathVariable("repo_name") String repoName,
            @PathVariable int tarefa_id, OAuth2AuthenticationToken authenticationToken, Model model) {
        try {
            Tarefa tarefaEscolhida = tarefaService.find(tarefa_id);
            model.addAttribute("tarefa", tarefaEscolhida);
            model.addAttribute("repoName", repoName);
            model.addAttribute("user", user_id);

            List<String> collaboratorUsernames = tarefaService.getCollaboratorsUsernames(tarefaEscolhida);
            model.addAttribute("usernames", collaboratorUsernames);

            String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);
            RepositoryModel repository = gitService.getRepository(accessToken, repoName);
            Set<Usuario> collaborators = gitService.getRepositoryCollaborators(accessToken, repoName);
            model.addAttribute("repository", repository);
            model.addAttribute("collaborators", collaborators);

            return "tarefa";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao obter a tarefa." + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        }

    }

    @PostMapping("/new")
    public String createTarefa(OAuth2AuthenticationToken authenticationToken, @ModelAttribute TarefaForm novaTarefa,
            @PathVariable("repo_name") String repoName, @PathVariable("user_id") String user_id,
            Model model) {
        String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);
        try {
            tarefaService.save(novaTarefa, repoName, accessToken, user_id);
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Falha ao conectar ao serviço de repositório." + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        } catch (DateTimeParseException e) {
            model.addAttribute("errorMessage", "Formato de data e hora inválido. Verifique o formato da data informada." + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        } catch (NumberFormatException e) {
            model.addAttribute("errorMessage", "Valor numérico inválido. Verifique os dados inseridos." + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        } catch (BusinessException e) {
            String redirect = "redirect:/user/" + user_id + "/repositories/" + repoName + "/tasks?error="
                    + e.getMessage();
            return redirect;
        } catch (PermissionDeniedDataAccessException e) {
            model.addAttribute("errorMessage", "Acesso negado. Você não possui permissão para criar tarefas neste repositório.." + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        }

        String redirect = "redirect:/user/" + user_id + "/repositories/" + repoName + "/tasks";
        return redirect;
    }

    @GetMapping("/{tarefa_id}/comments")
    public String getComentarios(OAuth2AuthenticationToken authenticationToken,Model model, @PathVariable("tarefa_id") String tarefaId, @PathVariable("user_id") String user_id) {

        Collection<ComentarioModel> comentarios = comentarioService.getComentarioTarefa(Integer.parseInt(tarefaId));
        model.addAttribute("comentarios", comentarios);
        model.addAttribute("user", user_id);

        return "comentarios";
    }

    @PostMapping("/{tarefa_id}/comments")
    public String createComentario(@PathVariable("repo_name") String repoName, @PathVariable("user_id") String userId,
            @PathVariable("tarefa_id") String tarefaIdStr, @RequestParam String message) {
        // Criar comentario dentro da tarefa
        System.out.println("Estado comentario");

        comentarioService.save(Integer.parseInt(tarefaIdStr), userId, message);

        System.out.println(message);

        String redirect = "redirect:/user/" + userId + "/repositories/" + repoName + "/tasks/" + tarefaIdStr
                + "/comments";
        return redirect;
    }

    @PostMapping("/{tarefa_id}/edit")
    public String postMethodName(OAuth2AuthenticationToken authenticationToken, @ModelAttribute TarefaForm novaTarefa,
            @PathVariable("repo_name") String repoName, @PathVariable("tarefa_id") String tarefaId,
            @PathVariable("user_id") String user_id, Model model) throws IOException {

        String accessToken = gitService.getAccessToken(authenticationToken, oauth2AuthorizedClientService);
        try {
            int id = Integer.parseInt(tarefaId);

            UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken); // Objeto do usuario
            gitService.validateUser(loggedUser, user_id);
            

            tarefaService.edit(novaTarefa, id, repoName, accessToken);
        } catch (NumberFormatException e) {
            model.addAttribute("errorMessage", "Valor numérico inválido." + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", "Erro ao editar a tarefa. Verifique os dados informados." + e.getMessage());
            model.addAttribute("errorDetails", "Detalhes técnicos: " + e.toString());
            return "error";
        }

        return "redirect:/user/" + user_id + "/repositories/" + repoName + "/tasks";
    }

}