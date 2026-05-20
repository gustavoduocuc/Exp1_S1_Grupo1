package com.minimarket.security.config;

import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. MITIGACIOOIN DE AMENAZAS: Protección CSRF configurada para API REST
                // En lugar de deshabilitarla, usamos un repositorio de tokens en cookies.
                .csrf(csrf -> csrf.csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse()))
                
                // 2. ESTRATEGIA DE AUTORIZACION: Basada en Roles y Métodos HTTP
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso público solo para ver el catálogo de productos (GET)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/productos/**").permitAll()
                        
                        // Solo el rol ADMIN puede crear, modificar o eliminar productos
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                        
                        // Los roles ADMIN y EMPLEADO pueden gestionar las ventas
                        .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "EMPLEADO")
                        
                        // Cualquier otra ruta no especificada requiere estar autenticado
                        .anyRequest().authenticated()
                )
                // 3. ESTRATEGIA DE AUTENTICACIOnN: Formulario de login personalizado y soporte para autenticación básica
                .formLogin(form -> form
                        .defaultSuccessUrl("/api/productos", true) 
                )
                .httpBasic(org.springframework.security.config.Customizer.withDefaults()); // Soporte para pruebas con Postman

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Configuracion de encriptación de contraseñas
    }
}
