package com.projectmanager.service;

import java.util.Collection;

import com.projectmanager.entities.Cronograma;
import com.projectmanager.entities.ScheduledActivity;


public interface CronogramaService {
    public Iterable<Cronograma> findAll();
    public Cronograma find(int id);
    public Cronograma save(Cronograma newCronograma);
    public void delete(int id);
    public void deleteCronogramasProjeto(int idProjeto);
    public Collection<Cronograma> getCronogramasProjeto(int idProjeto);
    public Collection<ScheduledActivity> buildScheduleForRepository(int repositoryId, TarefaServiceAbs tarefaService);
}
