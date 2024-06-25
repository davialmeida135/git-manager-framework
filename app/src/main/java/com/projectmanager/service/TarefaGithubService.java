package com.projectmanager.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projectmanager.config.Global;
import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.TarefaGitHub;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.forms.TarefaForm;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;

@Service("TarefaService")
public class TarefaGithubService extends TarefaServiceAbs {
    @Autowired
    @Qualifier(Global.GitClass)
    private GitService gitService;

    @Override
    public Tarefa validateCustomTarefa(Tarefa tarefa) throws BusinessException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate inputDate = LocalDate.parse(tarefa.getPrazo(), formatter);
        LocalDate currentDate = LocalDate.now();
        if (inputDate.isBefore(currentDate)) {
            throw new BusinessException("Não é possível selecionar um prazo anterior ao dia atual.");
        }
        return tarefa;
    }

    @Override
    public Tarefa instantiateTarefa() {
        Tarefa newTarefa = new TarefaGitHub();
        return newTarefa;
    }

    @Override
    public Tarefa formToTarefa(TarefaForm tarefaForm, String repoName, String accessToken)
            throws BusinessException, IOException {
        TarefaGitHub newTarefa = new TarefaGitHub();
        newTarefa.setTitulo(tarefaForm.getTitulo());
        newTarefa.setDescricao(tarefaForm.getDescricao());
        newTarefa.setPrazo(tarefaForm.getPrazo());

        UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken); // Objeto do usuario
        gitService.validateUser(loggedUser, String.valueOf(loggedUser.getId()));
        RepositoryModel repo = gitService.getRepository(accessToken, repoName);
        newTarefa.setId_criador((int) loggedUser.getId());
        newTarefa.setId_projeto((int) repo.getId());
        newTarefa.setConhecimentos(tarefaForm.getConhecimentos());
        newTarefa.setReferencias(tarefaForm.getReferencias());

        // newTarefa.setConhecimentos("SQL, Spring");
        // newTarefa.setReferencias("Livro bem legal");

        return newTarefa;
    }

}
