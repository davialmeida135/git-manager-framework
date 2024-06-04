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

@Service("TarefaService")
public class TarefaServiceImpl implements TarefaService{

    @Autowired
    TarefaRepository tarefaRepository;

    @Autowired
    ColaboradorService colaboradorService;
    @Autowired
    ComentarioService comentarioService;
    @Autowired
    CronogramaService cronogramaService;
    @Autowired
    @Qualifier("GithubService2")
    private GitService gitService;

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
        
        UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken); // Objeto do usuario
        gitService.validateUser(loggedUser, user_id);
        RepositoryModel repo = gitService.getRepository(accessToken, repoName);
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
        for (Usuario user : gitService.getRepositoryCollaborators(accessToken, repoName)) {
            if(tarefaForm.getCollaborators().contains(user.getUsername())){
                colaborador.setUsuario_id((int) user.getId());
                colaboradorService.save(colaborador);
            };
        }
                
        return newTarefa;
    }
    
    @Override
    public Tarefa save(IssueModel issue, int repoId){
        Tarefa newTarefa = new Tarefa();
        newTarefa.setTitulo(issue.getTitulo());
        newTarefa.setDescricao(issue.getDescricao());
        newTarefa.setId_criador(issue.getId_criador());
        newTarefa.setId_projeto(repoId);
        newTarefa.setData_criacao(issue.getData_criacao());
        newTarefa.setPrazo(issue.getPrazo());

        tarefaRepository.save(newTarefa);
    
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
    public Tarefa edit(TarefaForm tarefaForm, int tarefaId, String repoName, String accessToken) {

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

    
}
