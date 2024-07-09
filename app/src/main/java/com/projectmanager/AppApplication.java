package com.projectmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.projectmanager.config.Global;
import com.projectmanager.service.GitService;

@SpringBootApplication
public class AppApplication implements CommandLineRunner {
    @Autowired
    @Qualifier(Global.gitClass)
    GitService gitService;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    // Teste para verificar integração com o BD
    @Override
    public void run(String... args) throws Exception {
        
    }
}
