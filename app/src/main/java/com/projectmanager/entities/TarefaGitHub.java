package com.projectmanager.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GITHUB_TASK")
public class TarefaGitHub extends Tarefa{
    
}
