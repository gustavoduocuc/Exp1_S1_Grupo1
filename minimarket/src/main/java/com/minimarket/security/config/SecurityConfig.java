package com.minimarket.security.config;

import com.minimarket.security.audit.SecurityAuditHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final SecurityAuditHandler securityAuditHandler;

    public SecurityConfig(SecurityAuditHandler securityAuditHandler) {
        this.securityAuditHandler = securityAuditHandler;
    }

    /**
     * Configura la cadena de filtros de seguridad: CSRF, autorización por roles,
     * manejo de excepciones, autenticación por formulario y HTTP Basic.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF deshabilitado: API REST stateless con HTTP Basic, sin sesiones de navegador
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Autorización basada en roles y métodos HTTP
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas sin autenticación
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**").permitAll()

                        // CLIENTE y ADMIN pueden gestionar el carrito de compras
                        .requestMatchers("/api/carrito/**").hasAnyRole("CLIENTE", "ADMIN")

                        // EMPLEADO, GERENTE y ADMIN pueden registrar y consultar ventas
                        .requestMatchers("/api/ventas/**", "/api/detalle-ventas/**")
                            .hasAnyRole("EMPLEADO", "GERENTE", "ADMIN")

                        // Inventario: lectura para personal operativo; escritura solo para GERENTE y ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/inventario/**")
                            .hasAnyRole("EMPLEADO", "GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/inventario/**")
                            .hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/inventario/**")
                            .hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventario/**")
                            .hasAnyRole("GERENTE", "ADMIN")

                        // Catálogo: solo GERENTE y ADMIN pueden crear, modificar o eliminar productos
                        .requestMatchers(HttpMethod.POST, "/api/productos/**")
                            .hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**")
                            .hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**")
                            .hasAnyRole("GERENTE", "ADMIN")

                        // Categorías: gestión exclusiva de GERENTE y ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/categorias/**")
                            .hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categorias/**")
                            .hasAnyRole("GERENTE", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/**")
                            .hasAnyRole("GERENTE", "ADMIN")

                        // Administración de usuarios: solo ADMIN
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // Cualquier otra ruta requiere estar autenticado
                        .anyRequest().authenticated()
                )

                // 3. Auditoría: registra intentos no autenticados (401) y accesos denegados (403)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityAuditHandler)
                        .accessDeniedHandler(securityAuditHandler)
                )

                // 4. Autenticación: formulario web (navegador) y HTTP Basic (Postman/curl)
                .formLogin(form -> form.defaultSuccessUrl("/api/productos", true))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /** Expone el AuthenticationManager para validar credenciales de usuario y contraseña. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /** Codifica contraseñas con BCrypt antes de persistirlas en base de datos. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
