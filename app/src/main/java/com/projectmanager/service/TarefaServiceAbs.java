package com.projectmanager.service;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import java.util.NoSuchElementException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import com.projectmanager.config.Global;
import com.projectmanager.entities.Colaborador;
import com.projectmanager.entities.Tarefa;

import com.projectmanager.entities.Usuario;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.forms.TarefaForm;
import com.projectmanager.model.IssueModel;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;
import com.projectmanager.repositories.TarefaRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public abstract class TarefaServiceAbs{

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
    @Qualifier(Global.GitClass)
    private GitService gitService;

    public abstract Tarefa validateCustomTarefa(Tarefa tarefa) throws BusinessException;
    
    public abstract Tarefa instantiateTarefa();

    public abstract Tarefa formToTarefa(TarefaForm tarefaForm, String repoName, String accessToken)
    throws BusinessException, IOException;
    
    public Iterable<? extends Tarefa> findAll() {
        return tarefaRepository.findAll();
    }
 
    public Tarefa find(int id) throws NoSuchElementException{
        return tarefaRepository.findById(id).get();
    }

    public Tarefa save(TarefaForm tarefaForm, String repoName, String accessToken, String user_id) 
    throws IOException,BusinessException,DateTimeParseException,PermissionDeniedDataAccessException {
        
        Colaborador colaborador = new Colaborador();
        Tarefa newTarefa = formToTarefa(tarefaForm, repoName, accessToken);
        validate(newTarefa);
        newTarefa = tarefaRepository.save( newTarefa);
        colaborador.setTarefa_id(newTarefa.getId());
        for (Usuario user : gitService.getRepositoryCollaborators(accessToken, repoName)) {
            if(tarefaForm.getCollaborators().contains(user.getUsername())){
                colaborador.setUsuario_id((int) user.getId());
                colaboradorService.save(colaborador);
            };
        }
                
        return newTarefa;
    }
    
    
    public Tarefa save(IssueModel issue, int repoId){
        Tarefa newTarefa = instantiateTarefa();
        newTarefa.setTitulo(issue.getTitulo());
        newTarefa.setDescricao(issue.getDescricao());
        newTarefa.setId_criador(issue.getId_criador());
        newTarefa.setId_projeto(repoId);
        newTarefa.setData_criacao(issue.getData_criacao());
        newTarefa.setPrazo(issue.getPrazo());

        tarefaRepository.save(newTarefa);
    
        return newTarefa;
    }

    
    public void delete(int id) {
        colaboradorService.deleteColaboradoresTarefa(id);
        comentarioService.deleteComentariosTarefa(id);
        tarefaRepository.deleteById(id);
    }

    
    public Collection<Tarefa> getTaskByProject(int projetoid) {
        ArrayList<Tarefa> tarefas = new ArrayList<>();
        
        for(Tarefa tarefa : findAll()){
			if(tarefa.getId_projeto() == projetoid){
                tarefas.add(tarefa);
            }
		}

        return tarefas;
    }

    
    public Tarefa edit(TarefaForm tarefaForm, int tarefaId, String repoName, String accessToken) throws BusinessException, IOException {

        

        colaboradorService.deleteColaboradoresTarefa(tarefaId);
        tarefaRepository.deleteById(tarefaId);

        Tarefa tarefa = formToTarefa(tarefaForm, repoName, accessToken);

        tarefa = tarefaRepository.save(tarefa);

        Colaborador colaborador = new Colaborador();
        
        colaborador.setTarefa_id(tarefa.getId());
           
        try {
            for (Usuario user : gitService.getRepositoryCollaborators(accessToken, repoName)) {
                if(tarefaForm.getCollaborators().contains(user.getUsername())){
                    colaborador.setUsuario_id((int) user.getId());
                    colaboradorService.save(colaborador);
                };
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tarefa;
    }

    
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

    public Tarefa validate(Tarefa tarefa) throws BusinessException {
        validateBaseTarefa(tarefa);
        validateCustomTarefa(tarefa);
        return tarefa;
    }

    public Tarefa validateBaseTarefa(Tarefa tarefa)throws BusinessException{
        if(tarefa.getTitulo() == null || tarefa.getTitulo().isEmpty()){
            throw new BusinessException("O título da tarefa não pode ser vazio.");
        }
        if(tarefa.getTitulo().length() < 4){
            throw new BusinessException("O título da tarefa deve ter pelo menos 4 caracteres.");
        }

        return tarefa;
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