package com.sloth.OnlyStudent.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sloth.OnlyStudent.entities.User;
import com.sloth.OnlyStudent.entities.DTO.AuthenticationDTO;
import com.sloth.OnlyStudent.entities.DTO.RegisterDTO;
import com.sloth.OnlyStudent.entities.DTO.ResponseDTO;
import com.sloth.OnlyStudent.infra.security.TokenService;
import com.sloth.OnlyStudent.repository.UserRepository;

@Service
public class AuthenticationService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private TokenService tokenService;

	/**
	 * Autentica um usuário e retorna um token de acesso.
	 * A lógica de especialidade foi removida.
	 */
	public ResponseEntity<?> authenticateUser(AuthenticationDTO body){
		try {
			User user = this.repository.findByEmail(body.email());
			
			// A verificação de senha é mantida
			if(user != null && passwordEncoder.matches(body.password(), user.getPassword())) {
				String token = this.tokenService.generateToken(user);
				
				// A lógica de 'especialidade' e 'role' foi removida da resposta.
				// Use o novo ResponseDTO simplificado.
				return ResponseEntity.ok(new ResponseDTO(user.getName(), user.getEmail(), user.getTelephone(), token, user.getId()));
			}
			
			// Mensagem de erro genérica por segurança
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha incorretos.");
			
		} catch(Exception e) {
			logger.error("Erro na autenticação para o email: " + body.email(), e);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha incorretos.");
		}
	}
	
	/**
	 * Cria um novo usuário.
	 * A lógica de 'role' foi removida, criando um 'User' genérico.
	 */
	public ResponseEntity<?> createUser(RegisterDTO body) {
		// Verifica se o usuário já existe
		if (repository.existsByEmail(body.email())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email já cadastrado.");
		}
		
		// A lógica 'switch' baseada em 'role' foi totalmente removida.
		// Agora, criamos sempre uma instância da classe base 'User'.
		// (Assumindo que sua classe User tem um construtor adequado)
		User newUser = new User(
			body.name(), 
			body.telephone(), 
			body.email(), 
			passwordEncoder.encode(body.password())
		);
		
		repository.save(newUser);

		// Retornar o token diretamente pode não ser o ideal, mas mantendo a lógica original
		String token = tokenService.generateToken(newUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(newUser.getName(), newUser.getEmail(), newUser.getTelephone(), token, newUser.getId()));
	}
}