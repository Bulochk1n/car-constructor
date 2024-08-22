package com.car_constructor.car_constructor.config;

import com.car_constructor.car_constructor.services.MyUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF, если не требуется
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/new-user", "/ws/**").permitAll()  // Доступ к регистрации пользователей для всех
                        .requestMatchers("/chat", "/chat-with-admin/").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/cars/add", "/cars/{id}/remove",
                                "/cars/orders","/cars/orders/{id}","/cars/orders/{id}/remove","/cars/orders/{id}/complete").hasRole("ADMIN") // Только для админов
                        .requestMatchers("/cars/{id}/make-order").hasRole("USER")
                        .requestMatchers("/css/**", "/js/**", "/pictures/**").permitAll()  // Доступ к статике для всех
                        .requestMatchers("/", "/cars", "/cars/{id}").permitAll()  // Главная страница и просмотр машин доступны всем
                        .anyRequest().authenticated()  // Требование аутентификации для остальных запросов
                )
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll  // Используем стандартную форму логина Spring Security и даем доступ всем
                )
                .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
