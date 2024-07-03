package com.projectmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projectmanager.entities.TarefaTipoA;

@Repository
public interface TarefaGithubRepository extends JpaRepository<TarefaTipoA, Integer> {
    // Additional query methods for TarefaGitHub if needed
}