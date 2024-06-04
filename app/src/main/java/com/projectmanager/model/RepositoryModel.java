package com.projectmanager.model;

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
    //private Set<Usuario> collaborators;
    private String createdAt;
    //private Set<IssueModel> issues;
    
    public RepositoryModel() {
        
    }
    

    public RepositoryModel(long id, String owner, String name, String description, String url, String language,
            Set<String> branches, Set<Usuario> collaborators, String createdAt, Set<IssueModel> issues) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.url = url;
        this.language = language;
        this.branches = branches;
       
        this.createdAt = createdAt; 
    
    }
/* 
    public void addCollaborator(Usuario collaborator) {
        this.collaborators.add(collaborator);
    }

    public void addIssue(IssueModel issue) {
        this.issues.add(issue);
    }
*/
}
