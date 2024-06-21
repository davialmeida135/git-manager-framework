package com.projectmanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projectmanager.entities.TarefaGitHub;

@Repository
public interface TarefaGithubRepository extends JpaRepository<TarefaGitHub, Integer> {
    // Additional query methods for TarefaGitHub if needed
}