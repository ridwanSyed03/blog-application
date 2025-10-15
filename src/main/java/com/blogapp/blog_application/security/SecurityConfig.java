package com.blogapp.blog_application.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(configuer->
                configuer
                        .requestMatchers("/comment/edit/{id}","/comment/delete/{id}").hasRole("AUTHOR")
                        .requestMatchers("/newpost","/update/{id}","/delete/{id}").hasAnyRole("AUTHOR","ADMIN")
                        .requestMatchers("/login","/register","/","/post/{id}","/post/{id}/comment","/comment/save").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/", true)
                                .failureUrl("/login?error=true")
                                .permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .logout(logout->logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
