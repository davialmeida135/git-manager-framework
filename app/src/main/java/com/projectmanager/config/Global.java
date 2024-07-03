package com.projectmanager.config;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;

public class Global {
    public static final String gitClass = "GitlabService"; 
    // Caso queira mudar para github, substitua GitlabService por GithubService (sempre em maiúsculo)
}
