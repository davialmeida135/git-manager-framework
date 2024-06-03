package com.projectmanager.model;

import java.util.HashSet;
import java.util.Set;

import com.projectmanager.entities.Usuario;

import lombok.Data;

@Data

public class RepositoryModel {
    private long id;
    private String owner;
    private String name;
    private String description;
    private String url;
    private String language;
    private Set<String> branches;
    private Set<Usuario> collaborators;
    private String createdAt;
    
    public RepositoryModel() {
        this.collaborators = new HashSet<Usuario>();
    }
    public RepositoryModel(long id, String owner, String name, String description, String url, String language,
            Set<String> branches, Set<Usuario> collaborators) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.url = url;
        this.language = language;
        this.branches = branches;
        if(collaborators != null){
            this.collaborators = collaborators;
        }
        else{
            this.collaborators = new HashSet<Usuario>();
        }
        
    }

    public void addCollaborator(Usuario collaborator) {
        this.collaborators.add(collaborator);
    }

}
