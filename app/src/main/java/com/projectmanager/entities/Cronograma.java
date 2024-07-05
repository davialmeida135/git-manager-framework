package com.projectmanager.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cronograma")
public class Cronograma extends ScheduledActivity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int projeto_id;


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjeto_id() {
        return this.projeto_id;
    }

    public void setProjeto_id(int projeto_id) {
        this.projeto_id = projeto_id;
    }

    @Override
    public boolean isGithubTarefa() {
        return false;
    }
    @Override
    public boolean isGitlabTarefa() {
        return false;
    }
}
