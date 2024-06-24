package com.projectmanager.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.projectmanager.entities.ScheduledActivity;
import com.projectmanager.entities.Tarefa;

@Service("SortingStrategy")
public class SortByGithubStrategy implements TaskSortingStrategy {

    @Override
    public List<Tarefa> sort(List<Tarefa> tasks) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<Tarefa> sortedSchedule = tasks.stream()
        .sorted(Comparator.comparing(s -> LocalDate.parse(s.getPrazo(), formatter)))
        .collect(Collectors.toList());

        return sortedSchedule;
    }
}
