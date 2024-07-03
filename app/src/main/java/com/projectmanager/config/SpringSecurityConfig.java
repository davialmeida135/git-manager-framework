package com.projectmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @SuppressWarnings({ "removal"})
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().disable().csrf().disable().authorizeHttpRequests()
                .requestMatchers("/home/**", "/repository/**", "/authenticated/**", "/user/**", "/projects/**",
                        "/repositories/**") // Pages that require authentication
                .authenticated()
                .requestMatchers("/**").permitAll() // Pages that do not require authentication
                .and()
                .oauth2Login()
                .and()
                .logout()
                .logoutUrl("/logout") // Custom logout URL
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Clear any existing security context
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
