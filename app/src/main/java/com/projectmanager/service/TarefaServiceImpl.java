package com.projectmanager.service;

import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.TarefaGitHub;
import com.projectmanager.exceptions.BusinessException;

public class TarefaServiceImpl extends TarefaServiceAbs{

    @Override
    public Tarefa validadeCustomTarefa(Tarefa tarefa) throws BusinessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validadeCustomTarefa'");
    }

    @Override
    public Tarefa instantiateTarefa() {
        Tarefa newTarefa = new TarefaGitHub();
        return newTarefa;
    }
    
}
