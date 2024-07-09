package com.projectmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.projectmanager.service.GitService;


import com.projectmanager.service.TaskSortingStrategy;

import jakarta.annotation.PostConstruct;

@Configuration
public class AppConfig {

    @Value("${git.service.type}")
    private String gitServiceTypeString;

    private GitServiceType gitServiceType;

    @PostConstruct
    public void init() {
        gitServiceType = GitServiceType.valueOf(gitServiceTypeString.toUpperCase());
    }

    @Bean(name = "sortingStrategy")
    public TaskSortingStrategy createSortingStrategy() {
        switch (gitServiceType) {
            default:
                throw new IllegalStateException("Tipo de serviço Git não suportado: " + gitServiceType);
        }
    }
}
