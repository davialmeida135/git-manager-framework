package com.projectmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.servlet.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {
    
    @SuppressWarnings({ "removal", "deprecation" })
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable().authorizeRequests()
        .requestMatchers("/home/**","/repository/**","/authenticated/**","/user/**") // Paginas  que precisam de autenticação
        .authenticated()
        .requestMatchers("/**").permitAll() //Paginas que não precisam de autenticação
        .and()
        .oauth2Login();
        return http.build();
    }
}
