package com.projectmanager.entities;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ScheduledActivity {
    private String titulo;
    private String descricao;
    private String prazo;

    public abstract boolean isGithubTarefa();
    public abstract boolean isGitlabTarefa();
    
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public String getPrazo() {
        return prazo;
    }
    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }
    
    
    
}