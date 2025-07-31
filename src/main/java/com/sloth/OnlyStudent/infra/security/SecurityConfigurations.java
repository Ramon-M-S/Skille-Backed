package com.sloth.OnlyStudent.infra.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Adiciona a configuração de CORS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
        		// --- 1. ROTAS PÚBLICAS ---
                // Qualquer pessoa, mesmo sem login, pode acessar estas rotas.
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/support/send").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // Para documentação da API

                // --- 2. ROTAS DE ADMIN ---
                // Apenas usuários com a role 'ADMIN' podem acessar.
                // Exemplo: apenas um admin pode deletar uma simulação.                .requestMatchers(HttpMethod.DELETE, "/simulations/**").hasRole("ADMIN")
                
                // --- 3. ROTAS AUTENTICADAS ---
                // Qualquer outra requisição não listada acima exige que o usuário
                // esteja apenas autenticado (logado), não importando a role.
                .anyRequest().authenticated()
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://onlystudy.netlify.app", "http://localhost:5173", "http://localhost:5174", "http://192.168.2.43:5173", "http://192.168.2.48:5173", "192.168.2.48:5173", "192.168.2.43:5173", "https://petsuthepro.github.io/")); // Adicione suas origens aqui
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Métodos permitidos
        configuration.setAllowedHeaders(List.of("*")); // Cabeçalhos permitidos
        configuration.setAllowCredentials(true); // Permite envio de cookies e credenciais

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    /**
     * Cria e expõe um Bean do tipo RestTemplate.
     * Agora, o Spring pode injetar este objeto em outras classes.
     * @return uma instância de RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}