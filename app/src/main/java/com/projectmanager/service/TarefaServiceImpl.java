package com.projectmanager.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPersonSet;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import com.projectmanager.entities.Colaborador;
import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.Usuario;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.forms.TarefaForm;
import com.projectmanager.repositories.TarefaRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service("TarefaService")
public class TarefaServiceImpl implements TarefaService{

    @Autowired
    TarefaRepository tarefaRepository;

    @Autowired
    private TaskSortingStrategy sortingStrategy;

    @Autowired
    ColaboradorService colaboradorService;
    @Autowired
    ComentarioService comentarioService;
    @Autowired
    CronogramaService cronogramaService;
    @Autowired
    GithubAPIService githubService;

    @Override
    public Iterable<Tarefa> findAll() {
        return tarefaRepository.findAll();
    }

    @Override
    public Tarefa find(int id) throws NoSuchElementException{
        return tarefaRepository.findById(id).get();
    }

    @Override
    public Tarefa save(TarefaForm tarefaForm, String repoName, String accessToken, String user_id) 
    throws IOException,BusinessException,DateTimeParseException,PermissionDeniedDataAccessException {
        
        Colaborador colaborador = new Colaborador();
        Tarefa newTarefa = new Tarefa();
        newTarefa.setTitulo(tarefaForm.getTitulo());
        newTarefa.setDescricao(tarefaForm.getDescricao());
        newTarefa.setPrazo(tarefaForm.getPrazo());
             
        GHMyself loggedUser = githubService.getUser(accessToken); // Objeto do usuario
        githubService.validateUser(loggedUser, user_id);
        GHRepository repo = githubService.getRepository(loggedUser, repoName);
        newTarefa.setId_criador((int) loggedUser.getId());
        newTarefa.setId_projeto((int) repo.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inputDate = LocalDate.parse(tarefaForm.getPrazo(), formatter);
        LocalDate currentDate = LocalDate.now();
        if (inputDate.isBefore(currentDate)) {
            throw new BusinessException("Não é possível selecionar um prazo anterior ao dia atual.");
        }
        newTarefa = tarefaRepository.save(newTarefa);
        colaborador.setTarefa_id(newTarefa.getId());
        for (GHUser user : repo.getCollaborators()) {
            if(tarefaForm.getCollaborators().contains(user.getLogin())){
                colaborador.setUsuario_id((int) user.getId());
                colaboradorService.save(colaborador);
            };
        }
                
        return newTarefa;
    }
    
    @Override
    public Tarefa save(GHIssue issue, GHRepository repo) throws IOException{
        Tarefa newTarefa = new Tarefa();
        newTarefa.setTitulo(issue.getTitle());
        newTarefa.setDescricao(issue.getBody());
        newTarefa.setId_criador((int)issue.getUser().getId());
        newTarefa.setId_projeto((int) repo.getId());
        newTarefa.setData_criacao(issue.getCreatedAt().toString());
        newTarefa.setPrazo("1111-11-11");

        tarefaRepository.save(newTarefa);

        Colaborador novoColaborador = new Colaborador();
        novoColaborador.setTarefa_id(newTarefa.getId());
        
        for (GHUser collaborator : issue.getAssignees()) {   
            novoColaborador.setUsuario_id((int) collaborator.getId());
            colaboradorService.save(novoColaborador);      
        }
    
        return newTarefa;
    }

    @Override
    public void delete(int id) {
        colaboradorService.deleteColaboradoresTarefa(id);
        comentarioService.deleteComentariosTarefa(id);
        tarefaRepository.deleteById(id);
    }

    @Override
    public Collection<Tarefa> getTaskByProject(int projetoid) {
        ArrayList<Tarefa> tarefas = new ArrayList<>();
        
        for(Tarefa tarefa : findAll()){
			if(tarefa.getId_projeto() == projetoid){
                tarefas.add(tarefa);
            }
		}

        return tarefas;
    }

    @Override
    public Tarefa edit(TarefaForm tarefaForm, int tarefaId, GHRepository repo) {

        Tarefa tarefa = find(tarefaId);

        colaboradorService.deleteColaboradoresTarefa(tarefaId);
        tarefaRepository.deleteById(tarefaId);

        tarefa.setTitulo(tarefaForm.getTitulo());
        tarefa.setDescricao(tarefaForm.getDescricao());
        tarefa.setPrazo(tarefaForm.getPrazo());

        tarefa = tarefaRepository.save(tarefa);

        Colaborador colaborador = new Colaborador();
        
        colaborador.setTarefa_id(tarefa.getId());
        
        try {
            for (GHUser user : repo.getCollaborators()) {
                if(tarefaForm.getCollaborators().contains(user.getLogin())){
                    colaborador.setUsuario_id((int) user.getId());
                    colaboradorService.save(colaborador);
                };
            }
        } catch (IOException e) {
            return tarefa;
        }

        return tarefa;
    }

    @Override
    public TarefaForm getFormTarefa(int id) {
        Tarefa tarefa = find(id);

        TarefaForm tarefaForm = new TarefaForm();

        tarefaForm.setTitulo(tarefa.getTitulo());
        tarefaForm.setDescricao(tarefa.getDescricao());
        tarefaForm.setPrazo(tarefa.getPrazo());

        for (Usuario user : tarefa.getUsuarios()) {
            tarefaForm.getCollaborators().add(user.getUsername());
        }

        return tarefaForm;
    }

    public List<String> getCollaboratorsUsernames(Tarefa tarefa) {
        List<String> usernames = new ArrayList<>();
        for (Usuario usuario : tarefa.getUsuarios()) {
            usernames.add(usuario.getUsername());
        }
        return usernames;
    }

    public void setSortingStrategy(TaskSortingStrategy sortingStrategy) {
        this.sortingStrategy = sortingStrategy;
    }

    public void sortTasks(Collection<Tarefa> tasks) {
        if (sortingStrategy != null) {
            sortingStrategy.sort(tasks);
        } else {
            throw new IllegalStateException("Sorting strategy not initialized");
        }
    }

    public Collection<Tarefa> getSortedTasksByCriteria(int projectId) {
        Collection<Tarefa> tasks = getTaskByProject(projectId); // Exemplo de método para buscar tarefas
        sortTasks(tasks); // Usar a estratégia de ordenação injetada
        return tasks;
    }
    
}
