package com.projectmanager.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
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
import com.projectmanager.config.Global;

@Service(Global.GitClass)
public class GithubService2 implements GitService {

    GHMyself getGhMyself(String accessToken) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(accessToken).build();
        return github.getMyself();
    }

    @Override
    public UsuarioModel getUsuarioModel(String accessToken) throws IOException {
        try {
            GHMyself user = getGhMyself(accessToken);
            UsuarioModel newUser = new UsuarioModel(user.getLogin(), user.getId(), accessToken, user.getEmail(),
                    user.getName());
            return newUser;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }

    }

    public Usuario getUsuario(String accessToken) throws IOException {
        GHMyself loggedUser = getGhMyself(accessToken);

        long user_id = loggedUser.getId();
        Usuario usuario = new Usuario();
        usuario.setId((int) user_id);
        usuario.setName(loggedUser.getName());
        usuario.setUsername(loggedUser.getLogin());

        return usuario;
    }

    @Override
    public RepositoryModel getRepository(String accessToken, String repoName) throws IOException {
        List<RepositoryModel> repositories = getRepositories(accessToken);
        for (RepositoryModel repo : repositories) {
            if (repo.getName().equals(repoName)) {
                return repo;
            }
        }
        throw new IllegalArgumentException("Repositório não encontrado");

    }

    @Override
    public List<RepositoryModel> getRepositories(String accessToken) throws IOException {
        GHMyself user = getGhMyself(accessToken);
        Collection<GHRepository> repositories = user.getRepositories().values();
        List<RepositoryModel> repositoryModels = new ArrayList<RepositoryModel>();
        for (GHRepository oldRepo : repositories) {
            RepositoryModel newRepo = new RepositoryModel();
            newRepo.setName(oldRepo.getName());
            newRepo.setId(oldRepo.getId());
            newRepo.setDescription(oldRepo.getDescription());
            newRepo.setLanguage(oldRepo.getLanguage());
            newRepo.setOwner(oldRepo.getOwnerName());
            newRepo.setUrl(oldRepo.getUrl().toString());
            newRepo.setBranches(oldRepo.getBranches().keySet());
            newRepo.setCreatedAt(oldRepo.getCreatedAt().toString());

            repositoryModels.add(newRepo);
        }
        return repositoryModels;
    }

    @Override
    public Long getCollaboratorId(String accessToken, String collaboratorName, RepositoryModel repo)
            throws IOException {
        GHMyself user = getGhMyself(accessToken);

        Collection<GHRepository> repositories = user.getRepositories().values();
        GHRepository foundRepo = null;
        for (GHRepository oldRepo : repositories) {
            if (oldRepo.getName().equals(repo.getName())) {
                foundRepo = oldRepo;
                break;
            }
        }
        if (foundRepo == null) {
            throw new IllegalArgumentException("Repositório não encontrado");
        }
        for (GHUser collaborator : foundRepo.getCollaborators()) {
            if (collaborator.getLogin().equals(collaboratorName)) {
                return collaborator.getId();
            }
        }
        throw new IllegalArgumentException("Colaborador não encontrado");

    }

    @Override
    public void saveIssuesAsTarefas(String accessToken, String repoName, TarefaServiceAbs tarefaService)
            throws IOException {
        Set<IssueModel> issues = getRepositoryIssues(accessToken, repoName);
        RepositoryModel model = getRepository(accessToken, repoName);
        for (IssueModel issue : issues) {
            tarefaService.save(issue, (int) model.getId());
        }
    }

    @Override
    public String getAccessToken(OAuth2AuthenticationToken authenticationToken,
            OAuth2AuthorizedClientService oauth2AuthorizedClientService) {

        String clientRegistrationId = "github";
        OAuth2AuthorizedClient client = oauth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId,
                authenticationToken.getName());

        if (client != null) {
            OAuth2AccessToken accessToken = client.getAccessToken();
            if (accessToken != null) {
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
            String accessToken = getAccessToken(authenticationToken,
                    oauth2AuthorizedClientService);
            UsuarioModel loggedUser = getUsuarioModel(accessToken);
            return String.valueOf(loggedUser.getId());
        }
        throw new PermissionDeniedDataAccessException("Usuário não autenticado", null);
        // return null; // Retornar null ou algum valor padrão se o usuário não estiver
        // autenticado
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

    public Set<Usuario> getRepositoryCollaborators(String accessToken, String repoName) throws IOException {
        GHMyself user = getGhMyself(accessToken);
        Collection<GHRepository> repositories = user.getRepositories().values();
        Set<Usuario> colaboradores = new java.util.HashSet<Usuario>();
        for (GHRepository repo : repositories) {
            if (repo.getName().equals(repoName)) {
                for (GHUser collaborator : repo.getCollaborators()) {
                    Usuario usuario = new Usuario();
                    usuario.setId((int) collaborator.getId());
                    usuario.setName(collaborator.getName());
                    usuario.setUsername(collaborator.getLogin());
                    colaboradores.add(usuario);
                }
                return colaboradores;
            }
        }
        return colaboradores;
    }

    @Override
    public Set<IssueModel> getRepositoryIssues(String accessToken, String repoName) throws IOException {
        GHMyself user = getGhMyself(accessToken);
        Collection<GHRepository> repositories = user.getRepositories().values();
        Set<IssueModel> issues = new java.util.HashSet<IssueModel>();
        for (GHRepository repo : repositories) {
            if (repo.getName().equals(repoName)) {

                for (GHIssue issue : repo.getIssues(GHIssueState.OPEN)) {
                    IssueModel issueModel = new IssueModel();
                    issueModel.setTitulo(issue.getTitle());
                    issueModel.setDescricao(issue.getBody());
                    issueModel.setId_criador((int) issue.getUser().getId());
                    issueModel.setId_projeto((int) repo.getId());
                    issueModel.setData_criacao(issue.getCreatedAt().toString());
                    issueModel.setPrazo("1111-11-11");
                    issues.add(issueModel);
                }
                return issues;
            }
        }
        return issues;
    }

    public void removeAccessToken(OAuth2AuthenticationToken authenticationToken, OAuth2AuthorizedClientService oauth2AuthorizedClientService) {
        String clientRegistrationId = "github"; // ou seu clientRegistrationId configurado

        // Remove o cliente autorizado da sessão OAuth2
        oauth2AuthorizedClientService.removeAuthorizedClient(clientRegistrationId, authenticationToken.getName());
    }
}
