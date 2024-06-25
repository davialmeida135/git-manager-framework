package com.projectmanager.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("GITHUB_TASK")
public class TarefaGitHub extends Tarefa{
    String conhecimentos;
    
    String referencias;
    public String getReferencias() {
        return referencias;
    }
    public void setReferencias(String referencias) {
        this.referencias = referencias;
    }
    
    public String getConhecimentos() {
        return conhecimentos;
    }
    public void setConhecimentos(String conhecimentos) {
        this.conhecimentos = conhecimentos;
    }

    @Override
    public boolean isGithubTarefa() {
        return true;
    }
}
