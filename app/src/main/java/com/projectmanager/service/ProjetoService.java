package com.projectmanager.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;


import com.projectmanager.entities.Projeto;
import com.projectmanager.model.RepositoryModel;

public interface ProjetoService {
    public Iterable<Projeto> findAll();
    public Projeto find(int id);
    public Projeto save(String accessToken, String repoName) throws IOException;
    public void delete(int id);
    public boolean exist(int id);
    public Iterable<RepositoryModel> findProjectByUserRepositories(Iterable<Projeto> projects, Collection<RepositoryModel> repositories);
    public List<RepositoryModel> findTop3ByOrderByDataCriacaoDesc(Collection<RepositoryModel> repositories);
    public Collection<RepositoryModel> getMatchingProjects(String accessToken) throws IOException ;
    }
