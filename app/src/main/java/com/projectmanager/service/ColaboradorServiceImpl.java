package com.projectmanager.service;

import java.util.ArrayList;

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
    public Iterable<Tarefa> findTasksByIDUser(int id, TarefaService tarefaService) {
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

}
