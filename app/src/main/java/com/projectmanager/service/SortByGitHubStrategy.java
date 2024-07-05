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
public class SortByGitHubStrategy implements TaskSortingStrategy {

    @Override
    public List<Tarefa> sort(List<Tarefa> tasks) {
        LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    List<Tarefa> sortedSchedule = tasks.stream()
        .filter(s -> LocalDate.parse(s.getPrazo(), formatter).compareTo(today) >= 0) // Filter tasks with deadlines today or in the future
        .sorted(Comparator.comparing(s -> LocalDate.parse(s.getPrazo(), formatter)))
        .limit(3) // Limit to top 3 closest dates
        .collect(Collectors.toList());

    System.out.println("Top 3 Upcoming SortedSchedule: " + sortedSchedule);

    return sortedSchedule;
    }
}
