package com.projectmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.projectmanager.config.Global;
import com.projectmanager.entities.Usuario;
import com.projectmanager.model.UsuarioModel;
import com.projectmanager.service.GitService;
import com.projectmanager.service.GitlabService;

@SpringBootApplication
public class AppApplication implements CommandLineRunner {
    @Autowired
    @Qualifier(Global.gitClass)
    GitService gitService;

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    // Teste para verificar integração com o BD
    @Override
    public void run(String... args) throws Exception {
        // System.out.println("TESTE AQUI:");
        // GitlabService gitlabService = new GitlabService();
        // gitlabService.getUsuario("glpat-Q8XDKFyEryzcU-8bczc5");
        // TesteGetUsuarioModel();
        // testGetAccessToken(context);
    }

    // private static void testGetAccessToken(ApplicationContext context) {
    // GitlabService gitlabService = context.getBean(GitlabService.class);

    // // Criação de um OAuth2AuthenticationToken de exemplo para teste
    // OAuth2AuthenticationToken authentication = createTestAuthenticationToken();

    // // Chamada do método getAccessToken
    // try {
    // String accessToken = gitlabService.getAccessToken(authentication);
    // System.out.println("Access Token: " + accessToken);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // private static OAuth2AuthenticationToken createTestAuthenticationToken() {
    // // Aqui você cria um token de autenticação fictício para fins de teste
    // // Esta é uma simulação, e em um ambiente real você deve obter um token
    // válido.
    // return new OAuth2AuthenticationToken(null, null, "gitlab");
    // }

    // // Método de teste para verificar a obtenção de dados do usuário no GitLab
    // public void TesteGetUsuarioModel() {
    // String accessToken = "glpat-Q8XDKFyEryzcU-8bczc5";
    // try {
    // UsuarioModel usuarioModel = gitlabService.getUsuarioModel(accessToken);
    // if (usuarioModel != null) {
    // System.out.println("Usuário obtido do GitLab:");
    // System.out.println("ID: " + usuarioModel.getId());
    // System.out.println("Nome: " + usuarioModel.getFirstName());
    // System.out.println("Username: " + usuarioModel.getUsername());
    // System.out.println("Email: " + usuarioModel.getEmail());
    // System.out.println("Token de Acesso: " + usuarioModel.getToken());
    // } else {
    // System.out.println("Não foi possível obter o usuário do GitLab.");
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // public void testeGetUsuario() {
    // String accessToken = "glpat-Q8XDKFyEryzcU-8bczc5";
    // try {
    // Usuario usuario = gitlabService.getUsuario(accessToken);
    // if (usuario != null) {
    // System.out.println("Usuário obtido do GitLab:");
    // System.out.println("ID: " + usuario.getId());
    // System.out.println("Nome: " + usuario.getName());
    // System.out.println("Username: " + usuario.getUsername());
    // } else {
    // System.out.println("Não foi possível obter o usuário do GitLab.");
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
}
