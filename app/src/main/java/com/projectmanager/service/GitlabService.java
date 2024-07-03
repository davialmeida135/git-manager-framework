package com.projectmanager.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Constants.TokenType;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Owner;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import com.projectmanager.entities.Usuario;
import com.projectmanager.exceptions.BusinessException;
import com.projectmanager.model.IssueModel;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;

// @Primary
@Service("gitlabService")
public class GitlabService implements GitService {

    private GitLabApi gitLabApi;

    public GitlabService() {
    }

    // @BeforeAll
    private void initializeGitLabApi(String accessToken) {
        String gitLabUrl = "https://gitlab.com";
        this.gitLabApi = new GitLabApi(gitLabUrl, TokenType.OAUTH2_ACCESS, accessToken);
        System.out.println("API inicializada com o access token: " + accessToken);
    }

    private void ensureGitLabApiInitialized(String accessToken) throws GitLabApiException {
        if (gitLabApi == null) {
            initializeGitLabApi(accessToken);
        }
    }

    public GitlabService(String gitlabHostUrl, String accessToken) {
        String gitLabUrl = "https://gitlab.com";
        this.gitLabApi = new GitLabApi(gitLabUrl, TokenType.OAUTH2_ACCESS, accessToken);
    }

    @Override
    public UsuarioModel getUsuarioModel(String accessToken) throws IOException {
        try {
            ensureGitLabApiInitialized(accessToken);

            String authToken = gitLabApi.getAuthToken();
            if (authToken != null && !authToken.isEmpty()) {
                User gitLabUser = gitLabApi.getUserApi().getCurrentUser();

                UsuarioModel newUser = new UsuarioModel(
                        gitLabUser.getUsername(),
                        gitLabUser.getId().longValue(),
                        accessToken,
                        gitLabUser.getEmail(),
                        gitLabUser.getName());

                return newUser;
            } else {
                System.out.println("Erro: token de autenticação inválido.");
                return null;
            }
        } catch (GitLabApiException e) {
            System.out.println("Erro ao obter informações do usuário do GitLab: " + e.getMessage());
            throw new IOException("Erro ao obter informações do usuário do GitLab", e);
        }
    }

    @Override
    public Usuario getUsuario(String accessToken) throws IOException {
        try {
            ensureGitLabApiInitialized(accessToken);

            String authToken = gitLabApi.getAuthToken();
            if (authToken != null && !authToken.isEmpty()) {
                // System.out.println("AQUII1 " + gitLabApi.getAuthToken() +
                // gitLabApi.getSecretToken());
                // System.out.println(gitLabApi.getProjectApi().getOwnedProjects());

                User gitLabUser = gitLabApi.getUserApi().getCurrentUser();

                // Chama a API para obter informações detalhadas do usuário pelo ID
                // Long userId = gitLabUser.getId();
                // User detailedUser = gitLabApi.getUserApi().getUser(userId);

                // Cria um objeto Usuario com os dados obtidos
                Usuario usuario = new Usuario();
                usuario.setId(gitLabUser.getId().intValue());
                usuario.setName(gitLabUser.getName());
                usuario.setUsername(gitLabUser.getUsername());
                System.out.println(usuario.toString());

                return usuario;
            } else {
                System.out.println("Erro: token de autenticação inválido.");
                return null;
            }
        } catch (GitLabApiException e) {
            System.out.println("Erro ao obter informações do usuário do GitLab: " + e.getMessage());
            throw new IOException("Erro ao obter informações do usuário do GitLab", e);
        }
    }

    @Override
    public RepositoryModel getRepository(String accessToken, String repoName) throws IOException {
        try {
            // Garante que a API do GitLab está inicializada
            ensureGitLabApiInitialized(accessToken);

            // Obtém o token de autenticação do GitLab
            String authToken = gitLabApi.getAuthToken();

            // Verifica se o token de autenticação é válido
            if (authToken == null || authToken.isEmpty()) {
                System.out.println("Erro: token de autenticação inválido.");
                return null; // Retorna null se o token não for válido
            }

            // Obtém a lista de projetos do GitLab do usuário autenticado
            List<Project> gitLabProjects = getOwnedProjects();

            // Itera sobre os projetos para encontrar o repositório com o nome
            // correspondente
            for (Project gitLabProject : gitLabProjects) {
                // Substitui espaços por hifens no nome do projeto para comparação
                String nomeComEspacos = gitLabProject.getName();
                String nomeSemEspacos = nomeComEspacos.replaceAll("\\s+", "-");

                // Compara o nome do projeto sem espaços com o nome fornecido
                if (nomeSemEspacos.equals(repoName)) {
                    // Cria um novo objeto RepositoryModel para representar o repositório encontrado
                    RepositoryModel newRepo = new RepositoryModel();
                    newRepo.setName(nomeSemEspacos);
                    newRepo.setId(gitLabProject.getId().longValue());
                    newRepo.setDescription(gitLabProject.getDescription());
                    newRepo.setLanguage(gitLabProject.getDefaultBranch());
                    newRepo.setOwner(getOwnerUsername(gitLabProject));
                    newRepo.setUrl(gitLabProject.getWebUrl());

                    try {
                        // Obtém os branches do projeto
                        newRepo.setBranches(getBranches(gitLabProject.getId()));
                    } catch (GitLabApiException e) {
                        // Em caso de erro ao obter os branches, define como conjunto vazio
                        System.out.println("Erro ao obter branches para o projeto " + gitLabProject.getName() + ": "
                                + e.getMessage());
                        newRepo.setBranches(Collections.emptySet());
                    }

                    newRepo.setCreatedAt(gitLabProject.getCreatedAt().toString());
                    return newRepo; // Retorna o RepositoryModel do repositório encontrado
                }
            }

            // Lança uma exceção se o repositório com o nome especificado não for encontrado
            throw new IllegalArgumentException("Repositório não encontrado: " + repoName);
        } catch (GitLabApiException e) {
            // Trata exceções específicas do GitLab e converte em IOException para
            // compatibilidade
            String errorMessage = "Erro ao obter o repositório do GitLab: " + e.getMessage();
            System.out.println(errorMessage);
            throw new IOException(errorMessage, e);
        }
    }

    @Override
    public List<RepositoryModel> getRepositories(String accessToken) throws IOException {
        try {
            ensureGitLabApiInitialized(accessToken);

            String authToken = gitLabApi.getAuthToken();
            if (authToken == null || authToken.isEmpty()) {
                System.out.println("Erro: token de autenticação inválido.");
                return Collections.emptyList();
            }

            List<Project> gitLabProjects = getOwnedProjects();
            List<RepositoryModel> repositoryModels = buildRepositoryModels(gitLabProjects);

            return repositoryModels;
        } catch (GitLabApiException e) {
            String errorMessage = "Erro ao obter os repositórios do GitLab: " + e.getMessage();
            System.out.println(errorMessage);
            throw new IOException(errorMessage, e);
        }
    }

    private List<Project> getOwnedProjects() throws GitLabApiException {
        return gitLabApi.getProjectApi().getOwnedProjects();
    }

    private List<RepositoryModel> buildRepositoryModels(List<Project> gitLabProjects) {
        List<RepositoryModel> repositoryModels = new ArrayList<>();

        for (Project gitLabProject : gitLabProjects) {
            RepositoryModel newRepo = new RepositoryModel();
            String nomeComEspacos = gitLabProject.getName();
            String nomeSemEspacos = nomeComEspacos.replaceAll("\\s+", "-");
            newRepo.setName(nomeSemEspacos);
            newRepo.setId(gitLabProject.getId().longValue());
            newRepo.setDescription(gitLabProject.getDescription());
            newRepo.setLanguage(gitLabProject.getDefaultBranch());
            newRepo.setOwner(getOwnerUsername(gitLabProject));
            newRepo.setUrl(gitLabProject.getWebUrl());
            try {
                newRepo.setBranches(getBranches(gitLabProject.getId()));
            } catch (GitLabApiException e) {
                System.out.println(
                        "Erro ao obter branches para o projeto " + gitLabProject.getName() + ": " + e.getMessage());
                newRepo.setBranches(Collections.emptySet()); // Definir como conjunto vazio em caso de erro
            }
            newRepo.setCreatedAt(gitLabProject.getCreatedAt().toString());

            repositoryModels.add(newRepo);
        }

        return repositoryModels;
    }

    private String getOwnerUsername(Project gitLabProject) {
        Owner owner = gitLabProject.getOwner();
        return (owner != null) ? owner.getUsername() : "Unknown Owner";
    }

    public Set<String> getBranches(Long projectId) throws GitLabApiException {
        List<Branch> branches = gitLabApi.getRepositoryApi().getBranches(projectId);
        return branches.stream()
                .map(Branch::getName)
                .collect(Collectors.toSet());
    }

    @Override
    public Long getCollaboratorId(String accessToken, String collaboratorName, RepositoryModel repo)
            throws IOException {
        try {
            ensureGitLabApiInitialized(accessToken);

            String authToken = gitLabApi.getAuthToken();
            if (authToken != null && !authToken.isEmpty()) {
                List<Project> gitLabProjects = gitLabApi.getProjectApi().getOwnedProjects();

                Project foundRepo = null;
                for (Project gitLabProject : gitLabProjects) {
                    if (gitLabProject.getName().equals(repo.getName())) {
                        foundRepo = gitLabProject;
                        break;
                    }
                }
                if (foundRepo == null) {
                    throw new IllegalArgumentException("Repositório não encontrado");
                }

                List<Member> collaborators = gitLabApi.getProjectApi().getAllMembers(foundRepo.getId());
                for (Member collaborator : collaborators) {
                    if (collaborator.getUsername().equals(collaboratorName)) {
                        return collaborator.getId().longValue();
                    }
                }
                throw new IllegalArgumentException("Colaborador não encontrado");
            } else {
                System.out.println("Erro: token de autenticação inválido.");
                return null;
            }
        } catch (GitLabApiException e) {
            System.out.println("Erro ao obter o ID do colaborador do GitLab: " + e.getMessage());
            throw new IOException("Erro ao obter o ID do colaborador do GitLab", e);
        }
    }

    @Override
    public Set<IssueModel> getRepositoryIssues(String accessToken, String repoName) throws IOException {
        System.out.println("Unimplemented method 'getRepositoryIssues'");
        return null;
        // throw new UnsupportedOperationException("Unimplemented method
        // 'getRepositoryIssues'");
    }

    @Override
    public Set<Usuario> getRepositoryCollaborators(String accessToken, String repoName) throws IOException {
        try {
            ensureGitLabApiInitialized(accessToken);

            String authToken = gitLabApi.getAuthToken();
            if (authToken != null && !authToken.isEmpty()) {
                List<Project> gitLabProjects = gitLabApi.getProjectApi().getOwnedProjects();
                Set<Usuario> collaborators = new HashSet<>();

                for (Project gitLabProject : gitLabProjects) {
                    String nomeComEspacos = gitLabProject.getName();
                    String nomeSemEspacos = nomeComEspacos.replaceAll("\\s+", "-");
                    if (nomeSemEspacos.equals(repoName)) {
                        List<Member> gitLabCollaborators = gitLabApi.getProjectApi()
                                .getAllMembers(gitLabProject.getId());

                        for (Member collaborator : gitLabCollaborators) {
                            Usuario usuario = new Usuario();
                            usuario.setId(collaborator.getId().intValue());
                            usuario.setName(collaborator.getName());
                            usuario.setUsername(collaborator.getUsername());
                            collaborators.add(usuario);
                        }

                        return collaborators;
                    }
                }

                throw new IllegalArgumentException("Repositório não encontrado");
            } else {
                System.out.println("Erro: token de autenticação inválido.");
                return Collections.emptySet();
            }
        } catch (GitLabApiException e) {
            System.out.println("Erro ao obter os colaboradores do repositório do GitLab: " + e.getMessage());
            throw new IOException("Erro ao obter os colaboradores do repositório do GitLab", e);
        }
    }

    @Override
    public void saveIssuesAsTarefas(String accessToken, String repoName, TarefaServiceAbs tarefaService)
            throws IOException {
        System.out.println("Unimplemented method 'saveIssuesAsTarefas'");
        // throw new UnsupportedOperationException("Unimplemented method
        // 'saveIssuesAsTarefas'");
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
        if (isAuthenticated(authenticationToken)) {
            String accessToken = getAccessToken(authenticationToken, oauth2AuthorizedClientService);
            UsuarioModel loggedUser = getUsuarioModel(accessToken);
            return String.valueOf(loggedUser.getId());
        }
        throw new PermissionDeniedDataAccessException("Usuário não autenticado", new RuntimeException("Detalhes do erro"));
    }

    @Override
    public boolean isAuthenticated(OAuth2AuthenticationToken authenticationToken) {
        return authenticationToken != null && authenticationToken.isAuthenticated();
    }

    @Override
    public boolean validateUser(UsuarioModel loggedUser, String userId) throws BusinessException {
        if (!userId.equals(Long.toString(loggedUser.getId()))) {
            throw new BusinessException("Tentativa de acesso de repositório pertencente a outro usuário");
        }
        return true;
    }

    @Override
    public void removeAccessToken(OAuth2AuthenticationToken authenticationToken,
            OAuth2AuthorizedClientService oauth2AuthorizedClientService) {
        String clientRegistrationId = authenticationToken.getAuthorizedClientRegistrationId();

        // Remove o cliente autorizado da sessão OAuth2
        oauth2AuthorizedClientService.removeAuthorizedClient(clientRegistrationId, authenticationToken.getName());
    }

}