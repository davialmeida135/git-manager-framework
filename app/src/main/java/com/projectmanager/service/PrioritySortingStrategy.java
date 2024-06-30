package com.projectmanager.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projectmanager.entities.Tarefa;
import com.projectmanager.entities.TarefaGitlab;

@Service("PrioritySortingStrategy")
public class PrioritySortingStrategy implements TaskSortingStrategy {

    @Override
    public List<Tarefa> sort(List<Tarefa> tasks) {
        // TODO Auto-generated method stub
        System.out.println("Unimplemented method 'sort'");
        return tasks;
    }
}
