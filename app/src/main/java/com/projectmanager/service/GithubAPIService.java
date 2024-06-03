package com.projectmanager.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import com.projectmanager.entities.Usuario;
import com.projectmanager.model.RepositoryModel;
import com.projectmanager.model.UsuarioModel;



@Service
public class GithubAPIService {

    @Autowired
    UsuarioService usuarioService = new UsuarioServiceImpl();

    @Autowired
    public GithubAPIService() {}

    
    public GHMyself getUser(String accessToken) throws IOException {  
            GitHub github = new GitHubBuilder().withOAuthToken(accessToken).build();
            GHMyself user = github.getMyself();
            return user;        
    }
    public UsuarioModel getUserModel(String accessToken) throws IOException {

        GHMyself user = getUser(accessToken);
        UsuarioModel newUser = new UsuarioModel(user.getLogin(), user.getId(), accessToken,user.getEmail(),user.getName());

        return newUser;
    }
    
    public Collection<GHRepository> getRepositories(GHMyself user) throws IOException {
        Collection<GHRepository> repositories = user.getRepositories().values();
        return repositories;
    }
    public GHRepository getRepository(GHMyself user, String repoName) throws IOException {     
        Collection<GHRepository> repositories = getRepositories(user);
        for (GHRepository repo: repositories){
            if(repo.getName().equals(repoName)){
                return repo;
            }
            //System.out.println();
        }
        return null;
    }

    //Gera repositoryModel a partir de um GHRepository 
    public RepositoryModel getRepositoryModel(GHMyself user, String repoName) throws IOException {
        
        Collection<GHRepository> repositories = getRepositories(user);
        RepositoryModel newRepo = new RepositoryModel();

        for (GHRepository oldRepo: repositories){
            if(oldRepo.getName().equals(repoName)){
                newRepo.setName(oldRepo.getName());
                newRepo.setId(oldRepo.getId());
                newRepo.setDescription(oldRepo.getDescription());
                newRepo.setLanguage(oldRepo.getLanguage());
                newRepo.setOwner(oldRepo.getOwnerName());
                newRepo.setUrl(oldRepo.getUrl().toString());
                newRepo.setBranches(oldRepo.getBranches().keySet());
                //newRepo.setCollaborators(oldRepo.getCollaboratorNames());
                
            }
            //System.out.println();
        }
        return newRepo;
    }

    public Long getCollaboratorId(String collaboratorName, GHRepository repo) throws IOException{
        for (GHUser pessoa:  repo.getCollaborators()){
            if(pessoa.getLogin().equals(collaboratorName)){
                return pessoa.getId();
            }
        }
        return null;
    }

    public void saveIssuesAsTarefas(GHRepository repo,TarefaService tarefaService) throws IOException {
        List<GHIssue> issues = repo.getIssues(GHIssueState.OPEN);
        for (GHIssue issue : issues) {
            System.out.println(issue.getTitle()); 
            tarefaService.save(issue,repo);
        }
    }

    public Usuario getUsuario(String accessToken) throws IOException {
        GHMyself loggedUser = getUser(accessToken);

        long user_id = loggedUser.getId();
        Usuario usuario = new Usuario();
        usuario.setId((int) user_id);
        usuario.setName(loggedUser.getName());
        usuario.setUsername(loggedUser.getLogin());

        return usuario;
    }

    //Retorna o AccessToken do usuário a partir do token de autenticação do oauth
    public String getAccessToken(OAuth2AuthenticationToken authenticationToken, String clientRegistrationId,OAuth2AuthorizedClientService oauth2AuthorizedClientService){
        OAuth2AuthorizedClient client = oauth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId, authenticationToken.getName());

        if (client != null) {
            OAuth2AccessToken accessToken = client.getAccessToken();
            if (accessToken != null) {
                return accessToken.getTokenValue();
            } else {
                System.out.println("Access token is missing for client: " + clientRegistrationId);
                throw new RuntimeException("Access token is missing for client"); 
            } 
        }
        else {
            
            System.out.println("Client not found for clientRegistrationId: " + clientRegistrationId);
            throw new RuntimeException("Client missing"); 
        }
    }
    
    public String getUserId(OAuth2AuthenticationToken authenticationToken, OAuth2AuthorizedClientService oauth2AuthorizedClientService) throws IOException {
        if (isAuthenticated(authenticationToken)) {
                String accessToken = getAccessToken(authenticationToken, "github",
                        oauth2AuthorizedClientService);
                GHMyself loggedUser = getUser(accessToken);
                return String.valueOf(loggedUser.getId());
        }
        return null; // Retornar null ou algum valor padrão se o usuário não estiver autenticado
    }

    public boolean isAuthenticated(OAuth2AuthenticationToken authenticationToken){
        return authenticationToken != null && authenticationToken.isAuthenticated();
    }

    public boolean validateUser(GHUser loggedUser, String user_id) throws PermissionDeniedDataAccessException {
        if (!user_id.equals(Long.toString(loggedUser.getId()))) {
            throw new PermissionDeniedDataAccessException("Tentativa de acesso de repositório pertencente a outro usuário", null);
        }
        return true;
    }
    
}
