package com.projectmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.servlet.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @SuppressWarnings({ "removal", "deprecation" })
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable().authorizeRequests()
                .requestMatchers("/home/**", "/repository/**", "/authenticated/**", "/user/**", "/projects/**",
                        "/user/**",
                        "/repositories/**") // Paginas que precisam
                // de autenticação
                .authenticated()
                .requestMatchers("/**").permitAll() // Paginas que não precisam de autenticação
                .and()
                .oauth2Login()
                .and()
                .logout()
                .logoutUrl("/logout") // URL personalizada para o logout
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Limpa qualquer contexto de segurança existente
                    SecurityContextHolder.clearContext();
                    request.getSession().removeAttribute("user_id");
                    response.sendRedirect("/");
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/accessDenied");
        return http.build();
    }
}
