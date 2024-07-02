package com.projectmanager.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.TarefaGitlab;

@Service("PrioritySortingStrategy")
public class PrioritySortingStrategy implements TaskSortingStrategy {

    @Override
    public List<Tarefa> sort(List<Tarefa> tasks) {
        List<TarefaGitlab> gitlabTasks = sortByPrioridade(tarefaToTarefaGitlab(tasks));
        tasks = tarefaGitlabToTarefa(gitlabTasks);

        return tasks;
    }

    private List<TarefaGitlab> tarefaToTarefaGitlab(List<Tarefa> tasks) {
        List<TarefaGitlab> gitlabTasks = tasks.stream()
                .map(tarefa -> (TarefaGitlab) tarefa) // Faz o casting de Tarefa para TarefaGitlab
                .collect(Collectors.toList());

        return gitlabTasks;
    }

    private List<Tarefa> tarefaGitlabToTarefa(List<TarefaGitlab> gitlabTasks) {
        return gitlabTasks.stream()
                .map(tarefaGitlab -> (Tarefa) tarefaGitlab) // Faz o casting de TarefaGitlab para Tarefa
                .collect(Collectors.toList());
    }

    private List<TarefaGitlab> sortByPrioridade(List<TarefaGitlab> tasks) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Function<TarefaGitlab, LocalDate> prazoConverter = t -> LocalDate.parse(t.getPrazo(), formatter);

        Comparator<TarefaGitlab> comparator = Comparator
                .comparing(prazoConverter)
                .thenComparing(Comparator.comparingInt(TarefaGitlab::getPriority).reversed());

        // Filtra e ordena a lista
        List<TarefaGitlab> sortedTasks = tasks.stream()
                .filter(s -> LocalDate.parse(s.getPrazo(), formatter).compareTo(today) >= 0) // Filtra tarefas com
                                                                                             // prazos hoje ou no futuro
                .sorted(comparator)
                .toList(); // Coleta a lista ordenada

        return sortedTasks;
    }
}
