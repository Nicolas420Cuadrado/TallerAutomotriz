package com.usta.taller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/acceso-denegado").permitAll()
                        .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET,
                                "/modelos/nuevo", "/modelos/*/editar",
                                "/vehiculos/nuevo", "/vehiculos/*/editar",
                                "/repuestos/nuevo", "/repuestos/*/editar").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST,
                                "/modelos/**", "/vehiculos/**", "/repuestos/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"));

        return http.build();
    }
}
