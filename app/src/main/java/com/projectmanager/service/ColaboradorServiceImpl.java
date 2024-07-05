package com.projectmanager.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectmanager.entities.Colaborador;
import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.Usuario;
import com.projectmanager.repositories.ColaboradorRepository;

@Service("colaboradorService")
public class ColaboradorServiceImpl implements ColaboradorService {

    @Autowired
    ColaboradorRepository colaboradorRepository;

    @Autowired
    TaskSortingStrategy sortingStrategy;

    @Autowired
    UsuarioService usuarioService;

    @Override
    public Iterable<Colaborador> findAll() {
        return colaboradorRepository.findAll();
    }

    @Override
    public Colaborador save(Colaborador colaborador) {
        return colaboradorRepository.save(colaborador);
    }

    @Override
    public Iterable<Tarefa> findTasksByIDUser(int id, TarefaServiceAbs tarefaService) {
        ArrayList<Tarefa> tarefas = new ArrayList<>();

        for (Colaborador colaborador : findAll()) {
            if (colaborador.getUsuario_id() == id) {
                tarefas.add(tarefaService.find(colaborador.getTarefa_id()));
            }
        }

        return tarefas;

    }

    @Override
    public Iterable<Usuario> findCollaboratorsByIDTask(int id) {
        ArrayList<Usuario> usuarios = new ArrayList<>();

        for (Colaborador colaborador : findAll()) {
            if (colaborador.getTarefa_id() == id) {
                usuarios.add(usuarioService.find(colaborador.getUsuario_id()));
            }
        }

        return usuarios;
    }

    @Override
    public void deleteColaboradoresTarefa(int idTarefa) {
        for (Colaborador colaborador : findAll()) {
            if (colaborador.getTarefa_id() == idTarefa) {
                colaboradorRepository.delete(colaborador);
            }
        }
    }

    public void setSortingStrategy(TaskSortingStrategy sortingStrategy) {
        this.sortingStrategy = sortingStrategy;
    }

    @Override
    public List<Tarefa> sortTasks(List<Tarefa> tasks) {
        if (sortingStrategy != null) {
            return sortingStrategy.sort(tasks);
        } else {
            throw new IllegalStateException("Sorting strategy not initialized");
        }
    }

    public Iterable<Tarefa> getSortedTasksByCriteria(int projectId,TarefaServiceAbs tarefaService) {
        Iterable<Tarefa> iterable = findTasksByIDUser(projectId, tarefaService); // Assuming this returns an Iterable<Tarefa>
        List<Tarefa> tasks = new ArrayList<>();
        iterable.forEach(tasks::add);
        setSortingStrategy(new SortByGitHubStrategy());
        tasks = sortTasks(tasks); // Usar a estratégia de ordenação injetada
        return tasks;
    }

}
