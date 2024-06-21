package com.projectmanager.service;

import com.projectmanager.entities.Colaborador;
import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.Usuario;

public interface ColaboradorService {
    public Iterable<Colaborador> findAll();
    public Colaborador save(Colaborador colaborador);
    public Iterable<Tarefa> findTasksByIDUser(int id, TarefaServiceAbs tarefaService);
    public Iterable<Usuario> findCollaboratorsByIDTask(int id);
    public void deleteColaboradoresTarefa(int id);
}
