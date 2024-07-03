package com.projectmanager.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.TarefaTipoB;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.forms.TarefaForm;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;

@Service("TarefaTipoBService")
public class TarefaTipoBService extends TarefaServiceAbs {

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
        return new TarefaTipoB();
    }

    @Override
    public Tarefa formToTarefa(TarefaForm tarefaForm, String repoName, String accessToken)
            throws BusinessException, IOException {
        TarefaTipoB newTarefa = new TarefaTipoB();
        newTarefa.setTitulo(tarefaForm.getTitulo());
        newTarefa.setDescricao(tarefaForm.getDescricao());
        newTarefa.setPrazo(tarefaForm.getPrazo());

        UsuarioModel loggedUser = gitService.getUsuarioModel(accessToken); // Objeto do usuario
        gitService.validateUser(loggedUser, String.valueOf(loggedUser.getId()));
        RepositoryModel repo = gitService.getRepository(accessToken, repoName);
        newTarefa.setId_criador((int) loggedUser.getId());
        newTarefa.setId_projeto((int) repo.getId());
        newTarefa.setSupervisorResponsavel(tarefaForm.getSupervisorResponsavel());
        newTarefa.setPrioridade(tarefaForm.getPrioridade());
        newTarefa.setTempoEstimado(tarefaForm.getTempoEstimado());

        return newTarefa;
    }

}
