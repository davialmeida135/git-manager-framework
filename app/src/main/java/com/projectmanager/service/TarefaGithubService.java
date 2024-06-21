package com.projectmanager.service;

import org.springframework.stereotype.Service;

import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.TarefaGitHub;
import com.projectmanager.exceptions.BusinessException;

@Service("TarefaService")
public class TarefaGithubService extends TarefaServiceAbs{

    @Override
    public Tarefa validadeCustomTarefa(Tarefa tarefa) throws BusinessException {
        return null;
    }

    @Override
    public Tarefa instantiateTarefa() {
        Tarefa newTarefa = new TarefaGitHub();
        return newTarefa;
    }
    
}
