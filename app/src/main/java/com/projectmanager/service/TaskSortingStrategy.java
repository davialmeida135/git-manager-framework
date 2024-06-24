package com.projectmanager.service;

import com.projectmanager.entities.Tarefa;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

@Service("SortingStrategy")
public interface TaskSortingStrategy {
    List<Tarefa> sort(List<Tarefa> tasks);
}
