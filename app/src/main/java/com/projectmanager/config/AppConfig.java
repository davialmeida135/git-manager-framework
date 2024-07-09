package com.projectmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.projectmanager.service.DateSortingStrategy;
import com.projectmanager.service.GitService;
import com.projectmanager.service.GithubService;
import com.projectmanager.service.GitlabService;

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

    @Bean(name = "dateSortingStrategy")
    public DateSortingStrategy dateSortingStrategy() {
        return new DateSortingStrategy();
    }

    @Bean("GitlabService")
    public GitService gitlabService() {
        return new GitlabService();
    }

    @Bean("GitHubService")
    public GitService gitHubService() {
        return new GithubService();
    }

    // private TaskSortingStrategy createSortingStrategy(String beanName) {
    //     String gitClass = Global.GitClass;

    //     if ("GithubService2".equals(gitClass)) {
    //         // Injeta a estratégia de ordenação por data para GitlabService
    //         return new DateSortingStrategy();
    //     } else if ("GitlabService".equals(gitClass)) {
    //         // Injeta a estratégia de ordenação por prioridade para GithubService2 (exemplo
    //         // fictício)
    //         return new PrioritySortingStrategy();
    //     } else {
    //         throw new IllegalStateException("Configuração de classe Git não suportada: " + gitClass);
    //     }
    // }

    @Bean(name = "sortingStrategy")
    public TaskSortingStrategy createSortingStrategy() {
        switch (gitServiceType) {
            default:
                throw new IllegalStateException("Tipo de serviço Git não suportado: " + gitServiceType);
        }
    }
}
