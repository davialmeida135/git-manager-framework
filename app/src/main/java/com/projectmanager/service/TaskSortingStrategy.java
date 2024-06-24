package com.projectmanager.service;

import com.projectmanager.entities.Tarefa;

import java.util.Collection;
import java.util.List;

public interface TaskSortingStrategy {
    void sort(Iterable<Tarefa> tasks);
}
