package com.projectmanager.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.aspectj.lang.annotation.Before;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.UserApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.User;

import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import com.projectmanager.config.Global;
import com.projectmanager.entities.Usuario;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.model.IssueModel;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;

@Primary
@Service(Global.GitClass)
public class GitlabService implements GitService {

    private GitLabApi gitLabApi;

    public GitlabService() {
    }

    // @BeforeAll
    public void initializeGitLabApi(String accessToken) {
        this.gitLabApi = new GitLabApi(GitLabApi.ApiVersion.V4, "https://gitlab.com", accessToken);
    }

    public GitlabService(String gitlabHostUrl, String personalAccessToken) {
        this.gitLabApi = new GitLabApi(GitLabApi.ApiVersion.V4, gitlabHostUrl, personalAccessToken);
    }

    @Override
    public UsuarioModel getUsuarioModel(String accessToken) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUsuarioModel'");
    }

    @Override
    public Usuario getUsuario(String accessToken) throws IOException {
        try {
            if (gitLabApi == null) {
                initializeGitLabApi(accessToken);
                System.out.println("Não era para entrar aqui");
            }
            
            if (!gitLabApi.getAuthToken().equals("")) {
                System.out.println("AQUII " + gitLabApi.getAuthToken());
                User gitLabUser = gitLabApi.getUserApi().getCurrentUser();
                System.out.println("AQUII");

                Long userId = gitLabUser.getId();
                System.out.println("AQUII");

                // Chama a API para obter informações detalhadas do usuário pelo ID
                User detailedUser = gitLabApi.getUserApi().getUser(userId);
                System.out.println("AQUII");

                // Cria um objeto Usuario com os dados obtidos
                Usuario usuario = new Usuario();
                System.out.println(detailedUser.getId().intValue());
                usuario.setId(detailedUser.getId().intValue());
                usuario.setName(detailedUser.getName());
                usuario.setUsername(detailedUser.getUsername());

                return usuario;
            } else {
                System.out.println("bora de se mata");
                return null;
            }
            // Adicione os demais campos desejados conforme necessário
        } catch (GitLabApiException e) {
            // Re-lança a exceção como IOException
            throw new IOException("Erro ao obter informações do usuário do GitLab", e);
        }
    }

    @Override
    public RepositoryModel getRepository(String accessToken, String repoName) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRepository'");
    }

    @Override
    public List<RepositoryModel> getRepositories(String accessToken) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRepositories'");
    }

    @Override
    public Long getCollaboratorId(String accessToken, String collaboratorName, RepositoryModel repo)
            throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCollaboratorId'");
    }

    @Override
    public Set<IssueModel> getRepositoryIssues(String accessToken, String repoName) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRepositoryIssues'");
    }

    @Override
    public Set<Usuario> getRepositoryCollaborators(String accessToken, String repoName) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRepositoryCollaborators'");
    }

    @Override
    public void saveIssuesAsTarefas(String accessToken, String repoName, TarefaServiceAbs tarefaService)
            throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveIssuesAsTarefas'");
    }

    @Override
    public String getAccessToken(OAuth2AuthenticationToken authenticationToken,
            OAuth2AuthorizedClientService oauth2AuthorizedClientService) {
        String clientRegistrationId = "gitlab";
        OAuth2AuthorizedClient client = oauth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId,
                authenticationToken.getName());

        if (client != null) {
            OAuth2AccessToken accessToken = client.getAccessToken();
            if (accessToken != null) {
                System.out.println("hey " + accessToken.getTokenValue());
                initializeGitLabApi(accessToken.getTokenValue());
                return accessToken.getTokenValue();
            } else {
                System.out.println("Access token is missing for client: " + clientRegistrationId);
                throw new RuntimeException("Access token is missing for client");
            }
        } else {
            System.out.println("Client not found for clientRegistrationId: " + clientRegistrationId);
            throw new RuntimeException("Client missing");
        }
    }

    @Override
    public String getUserId(OAuth2AuthenticationToken authenticationToken,
            OAuth2AuthorizedClientService oauth2AuthorizedClientService) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserId'");
    }

    @Override
    public boolean isAuthenticated(OAuth2AuthenticationToken authenticationToken) {
        return authenticationToken != null && authenticationToken.isAuthenticated();
    }

    @Override
    public boolean validateUser(UsuarioModel loggedUser, String userId) throws BusinessException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateUser'");
    }

    @Override
    public void removeAccessToken(OAuth2AuthenticationToken authenticationToken,
            OAuth2AuthorizedClientService oauth2AuthorizedClientService) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeAccessToken'");
    }

}