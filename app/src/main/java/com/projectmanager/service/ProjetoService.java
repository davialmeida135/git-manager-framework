package com.projectmanager.service;

import java.io.IOException;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;

import com.projectmanager.entities.Projeto;

public interface ProjetoService {
    public Iterable<Projeto> findAll();
    public Projeto find(int id);
    public Projeto save(GHMyself loggedUser, String repoName)throws IOException;
    public void delete(int id);
    public boolean exist(int id);
}
