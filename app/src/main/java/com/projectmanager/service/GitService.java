package com.projectmanager.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;


import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.projectmanager.entities.Usuario;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.model.IssueModel;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;

public interface GitService {
    UsuarioModel getUsuarioModel(String accessToken)throws IOException;
    Usuario getUsuario(String accessToken)throws IOException;
    RepositoryModel getRepository(String accessToken, String repoName)throws IOException;
    List<RepositoryModel> getRepositories(String accessToken)throws IOException;
    Long getCollaboratorId(String accessToken, String collaboratorName, RepositoryModel repo) throws IOException;
    Set<IssueModel> getRepositoryIssues(String accessToken, String repoName)throws IOException;
    Set<Usuario> getRepositoryCollaborators(String accessToken, String repoName) throws IOException;

    void saveIssuesAsTarefas(String accessToken, String repoName, TarefaServiceAbs tarefaService) throws IOException;
    String getAccessToken(OAuth2AuthenticationToken authenticationToken,
            OAuth2AuthorizedClientService oauth2AuthorizedClientService);
    String getUserId(OAuth2AuthenticationToken authenticationToken, OAuth2AuthorizedClientService oauth2AuthorizedClientService)throws IOException ;
    boolean isAuthenticated(OAuth2AuthenticationToken authenticationToken);
    boolean validateUser(UsuarioModel loggedUser, String userId)throws BusinessException;
    void removeAccessToken(OAuth2AuthenticationToken authenticationToken,
            OAuth2AuthorizedClientService oauth2AuthorizedClientService);
}
