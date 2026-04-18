package com.clinic.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/doctors.html",
                                "/doctor.html",
                                "/patient-dashboard.html",
                                "/doctor-dashboard.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/auth/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/doctors/me").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/doctors/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/appointments/available").permitAll()

                        .requestMatchers(HttpMethod.POST, "/slots").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/slots/me").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/slots/**").hasRole("DOCTOR")

                        .requestMatchers(HttpMethod.POST, "/appointments/book").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.GET, "/appointments/me").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.GET, "/patients/me").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.DELETE, "/appointments/**").hasRole("PATIENT")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}