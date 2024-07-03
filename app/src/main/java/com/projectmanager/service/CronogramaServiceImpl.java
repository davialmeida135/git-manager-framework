package com.projectmanager.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import com.projectmanager.entities.Cronograma;
import com.projectmanager.entities.ScheduledActivity;
import com.projectmanager.entities.Tarefa;
import com.projectmanager.repositories.CronogramaRepository;

@Service("cronogramaService")
public class CronogramaServiceImpl implements CronogramaService {

    @Autowired
    CronogramaRepository cronogramaRepository;

    @Override
    public Iterable<Cronograma> findAll() {
        return cronogramaRepository.findAll();
    }

    @Override
    public Cronograma find(int id) {
        return cronogramaRepository.findById(id).get();
    }

    @Override
    public Cronograma save(Cronograma newCronograma) {
        return cronogramaRepository.save(newCronograma);
    }

    @Override
    public void delete(int id) {
        cronogramaRepository.deleteById(id);
    }

    @Override
    public void deleteCronogramasProjeto(int idProjeto) {
        for (Cronograma cronograma : findAll()) {
            if (cronograma.getProjeto_id() == idProjeto) {
                delete(cronograma.getId());
            }
        }
    }

    @Override
    public Collection<Cronograma> getCronogramasProjeto(int idProjeto) {
        ArrayList<Cronograma> cronogramas = new ArrayList<>();

        for (Cronograma cronograma : findAll()) {
            if (cronograma.getProjeto_id() == idProjeto) {
                cronogramas.add(cronograma);
            }
        }

        return cronogramas;
    }

    @Override
    public Collection<ScheduledActivity> buildScheduleForRepository(int repositoryId, TarefaServiceAbs tarefaService) {
        try {
            Collection<Cronograma> cronogramas = getCronogramasProjeto(repositoryId);
            Collection<Tarefa> tarefas = tarefaService.getTaskByProject(repositoryId);

            Collection<ScheduledActivity> schedule = new ArrayList<ScheduledActivity>();
            schedule.addAll(cronogramas);
            schedule.addAll(tarefas);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            List<ScheduledActivity> sortedSchedule = schedule.stream()
                    .sorted(Comparator.comparing(s -> LocalDate.parse(s.getPrazo(), formatter)))
                    .collect(Collectors.toList());

            return sortedSchedule;
        } catch (PermissionDeniedDataAccessException e) {
            throw new PermissionDeniedDataAccessException("Erro ao acessar dados do banco", new RuntimeException("Detalhes do erro"));
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado ao construir o cronograma", e);
        }
    }

}
