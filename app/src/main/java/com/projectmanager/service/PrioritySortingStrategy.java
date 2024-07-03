package com.projectmanager.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.TarefaTipoB;

@Service("PrioritySortingStrategy")
public class PrioritySortingStrategy implements TaskSortingStrategy {

    @Override
    public List<Tarefa> sort(List<Tarefa> tasks) {
        List<TarefaTipoB> typeB_tasks = sortByPrioridade(tarefaToTarefaTipoB(tasks));
        tasks = tarefaTipoBToTarefa(typeB_tasks);

        return tasks;
    }

    private List<TarefaTipoB> tarefaToTarefaTipoB(List<Tarefa> tasks) {
        List<TarefaTipoB> typeB_tasks = tasks.stream()
                .map(tarefa -> (TarefaTipoB) tarefa)
                .collect(Collectors.toList());

        return typeB_tasks;
    }

    private List<Tarefa> tarefaTipoBToTarefa(List<TarefaTipoB> typeB_tasks) {
        return typeB_tasks.stream()
                .map(typeB_task -> (Tarefa) typeB_task)
                .collect(Collectors.toList());
    }

    private List<TarefaTipoB> sortByPrioridade(List<TarefaTipoB> tasks) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Function<TarefaTipoB, LocalDate> prazoConverter = t -> LocalDate.parse(t.getPrazo(), formatter);

        Comparator<TarefaTipoB> comparator = Comparator
                .comparing(prazoConverter)
                .thenComparing(Comparator.comparingInt(TarefaTipoB::getPrioridadeValor).reversed());

        // Filtra e ordena a lista
        List<TarefaTipoB> sortedTasks = tasks.stream()
                .filter(s -> LocalDate.parse(s.getPrazo(), formatter).compareTo(today) >= 0) // Filtra tarefas com
                                                                                             // prazos hoje ou no futuro
                .sorted(comparator)
                .toList(); // Coleta a lista ordenada

        return sortedTasks;
    }
}
