package com.projectmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projectmanager.entities.Tarefa;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Integer> {
    // Additional query methods can be defined here if needed
}