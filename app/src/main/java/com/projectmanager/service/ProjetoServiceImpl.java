package com.projectmanager.service;

import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.projectmanager.config.Global;
import com.projectmanager.entities.Projeto;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.repositories.ProjetoRepository;

@Service("ProjetoService")
public class ProjetoServiceImpl implements ProjetoService {
    @Autowired
    @Qualifier(Global.GitClass)
    private GitService gitService; // Injete o serviço que obtém os repositórios do GitHub

    @Autowired
    ProjetoRepository projetoRepository;
    @Autowired
    TarefaServiceAbs tarefaService;
    @Autowired
    CronogramaService cronogramaService;

    @Override
    public Iterable<Projeto> findAll() {
        return projetoRepository.findAll();
    }

    @Override
    public Projeto find(int id) {
        return projetoRepository.findById(id).get();
    }

    @Override
    public Projeto save(String accessToken, String repoName) throws IOException {
        Projeto projeto = new Projeto();

        RepositoryModel repo = gitService.getRepository(accessToken, repoName);

        if (!exist((int) repo.getId())) {

            System.out.println("Criando projeto");
            projeto.setId((int) repo.getId());
            projeto.setNome(repo.getName());
            projeto.setDescricao(repo.getDescription());
            projeto.setData_inicio(repo.getCreatedAt().toString());
            

            gitService.saveIssuesAsTarefas(accessToken,repoName, tarefaService);

            return projetoRepository.save(projeto);

        }

        return null;
    }

    @Override
    public void delete(int id) {
        cronogramaService.deleteCronogramasProjeto(id);
        projetoRepository.deleteById(id);
    }

    @Override
    public boolean exist(int id) {
        return projetoRepository.existsById(id);
    }

    @Override
    public Iterable<RepositoryModel> findProjectByUserRepositories(Iterable<Projeto> projects,
            Collection<RepositoryModel> repositories) {
        Collection<RepositoryModel> result = new ArrayList<>();

        for (Projeto projeto : projects) {
            for (RepositoryModel repo : repositories) {
                if (repo.getName().equals(projeto.getNome())) {
                    result.add(repo);
                    break;
                }
            }
        }

        return result;
    }

    private Iterable<Projeto> orderByDate() {
        List<Projeto> orderedProjects = (List<Projeto>) findAll();

        for (Projeto projeto : orderedProjects) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);                
                LocalDateTime dataInicio = LocalDateTime.parse(projeto.getData_inicio(), formatter);
                projeto.setDataInicioDate(dataInicio);
            } catch (DateTimeParseException e) {
                System.out.println("Erro ao analisar a data de início do projeto: " + e.getMessage());
            }
        }

        Collections.sort(orderedProjects,
                (projeto1, projeto2) -> projeto2.getDataInicioDate().compareTo(projeto1.getDataInicioDate()));

        return orderedProjects;
    }

    @Override
    public List<RepositoryModel> findTop3ByOrderByDataCriacaoDesc(Collection<RepositoryModel> repositories) {
        List<RepositoryModel> orderedProjects = (List<RepositoryModel>) findProjectByUserRepositories(orderByDate(),
                repositories);

        List<RepositoryModel> top3Projects = orderedProjects.subList(0, Math.min(3, orderedProjects.size()));

        return top3Projects;
    }
    ////
    public Collection<RepositoryModel> getMatchingProjects(String accessToken) throws IOException {
          
        List<RepositoryModel> projects = new ArrayList<>();
        List<RepositoryModel> repositories = gitService.getRepositories(accessToken);
        for (Projeto projeto : findAll()) {
            for (RepositoryModel repo : repositories) {
                if (repo.getName().equals(projeto.getNome())) {
                    projects.add(repo);
                    break;
                }
            }
        }
        return projects;
    }

}
