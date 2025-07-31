package com.sloth.OnlyStudent.infra.security;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sloth.OnlyStudent.entities.User;
import com.sloth.OnlyStudent.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Import não é mais necessário
// import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class SecurityFilter extends OncePerRequestFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

	@Autowired
	TokenService tokenService;
	
	@Autowired
	UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		var token = this.recoverToken(request);
		
		if(token != null){
			var email = tokenService.validateToken(token);
			User user = userRepository.findByEmail(email);

			if(user != null) {
				// Linha antiga que buscava a role do usuário. Foi removida.
				// var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRoles().getRole().toUpperCase()));
				
				// Agora, criamos a autenticação sem nenhuma autoridade (role).
				// O terceiro argumento é a lista de "authorities", que agora está vazia.
				var authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
				
				// Isso define o usuário como "logado" para esta requisição.
				SecurityContextHolder.getContext().setAuthentication(authentication);   
			} else {
				logger.info("Token válido, mas usuário não encontrado no banco de dados!");
			}
		}
		
		filterChain.doFilter(request, response);
	}
	
	private String recoverToken(HttpServletRequest request){
		var authHeader = request.getHeader("Authorization");
		if(authHeader == null) return null;
		return authHeader.replace("Bearer ", "");
	}
}