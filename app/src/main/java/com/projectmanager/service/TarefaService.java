package com.projectmanager.service;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;

import com.projectmanager.entities.Tarefa;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.forms.TarefaForm;
import com.projectmanager.model.IssueModel;


public interface TarefaService {
    public Iterable<Tarefa> findAll();
    public Tarefa find(int id);
    public Tarefa save(TarefaForm tarefaForm, String repoName, String accessToken, String user_id) throws IOException,BusinessException,DateTimeParseException;
    public Tarefa save(IssueModel issue, int repoId); 
    public Tarefa edit(TarefaForm tarefaForm, int tarefaId, String repoName, String accessToken);
    public void delete(int id);
    public TarefaForm getFormTarefa(int id);
    public Collection<Tarefa> getTaskByProject(int projetoid);
    public List<String> getCollaboratorsUsernames(Tarefa tarefa);
}
