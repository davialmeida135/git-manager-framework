package com.projectmanager.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import com.projectmanager.entities.Usuario;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.model.IssueModel;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;
import com.projectmanager.config.Global;

@Service(Global.GitClass)
public class GitlabService implements GitService {

    // TODO SUBSTITUIR
    private static final String GITLAB_URL = "https://gitlab.example.com";
    private static final String API_TOKEN = "your_api_token_here";

    private GitLabApi gitLabApi;

    public GitlabService() {
        // Inicializa a instância do GitLabApi com a URL do GitLab
        GitLabApi gitLabApi = new GitLabApi(GITLAB_URL, API_TOKEN);

    }

    @Override
    public UsuarioModel getUsuarioModel(String accessToken) throws IOException {
        // Implementar a para UsuarioModel
        try {
            User user = gitLabApi.getUserApi().getCurrentUser();
        } catch (GitLabApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        throw new UnsupportedOperationException("Unimplemented method 'getUsuarioModel'");
    }

    @Override
    public Usuario getUsuario(String accessToken) throws IOException {
        // Implementar a para Usuario
        try {
            User user = gitLabApi.getUserApi().getCurrentUser();
        } catch (GitLabApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        throw new UnsupportedOperationException("Unimplemented method 'getUsuario'");
    }

    @Override
    public RepositoryModel getRepository(String accessToken, String repoName) throws IOException {
        List<RepositoryModel> repositories = getRepositories(accessToken);
        for (RepositoryModel repo : repositories) {
            if (repo.getName().equals(repoName)) {
                return repo;
            }
        }
        throw new IllegalArgumentException("Unimplemented method");
    }

    @Override
    public List<RepositoryModel> getRepositories(String accessToken) throws IOException {
        throw new IllegalArgumentException("Unimplemented method");
    }

    @Override
    public Long getCollaboratorId(String accessToken, String collaboratorName, RepositoryModel repo)
            throws IOException {
        throw new IllegalArgumentException("Unimplemented method");
    }

    @Override
    public void saveIssuesAsTarefas(String accessToken, String repoName, TarefaServiceAbs tarefaService)
            throws IOException {
        throw new IllegalArgumentException("Unimplemented method");
    }

    @Override
    public String getAccessToken(OAuth2AuthenticationToken authenticationToken,
            OAuth2AuthorizedClientService oauth2AuthorizedClientService) {
        throw new IllegalArgumentException("Unimplemented method");
    }
                

    @Override
    public String getUserId(OAuth2AuthenticationToken authenticationToken,
                            OAuth2AuthorizedClientService oauth2AuthorizedClientService) throws IOException {
        throw new IllegalArgumentException("Unimplemented method");
    }

    @Override
    public boolean isAuthenticated(OAuth2AuthenticationToken authenticationToken) {
        throw new IllegalArgumentException("Unimplemented method");
    }

    @Override
    public boolean validateUser(UsuarioModel loggedUser, String userId) throws BusinessException {
        throw new IllegalArgumentException("Unimplemented method");
    }

    @Override
    public Set<Usuario> getRepositoryCollaborators(String accessToken, String repoName) throws IOException {
        throw new IllegalArgumentException("Unimplemented method");
    }

    @Override
    public Set<IssueModel> getRepositoryIssues(String accessToken, String repoName) throws IOException {
        throw new IllegalArgumentException("Unimplemented method");
    }
}
