package com.sloth.OnlyStudent.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sloth.OnlyStudent.entities.DTO.AuthenticationDTO;
import com.sloth.OnlyStudent.entities.DTO.RegisterDTO;
import com.sloth.OnlyStudent.infra.security.TokenService;
import com.sloth.OnlyStudent.repository.UserRepository;
import com.sloth.OnlyStudent.services.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5174/")
public class AuthenticationController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private TokenService tokenService;

	@Autowired
	private AuthenticationService authenticationService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthenticationDTO body){
		return authenticationService.authenticateUser(body);
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterDTO body) {
		return authenticationService.createUser(body);
	}

	// Endpoint agora acessível sem verificação de role específica
	@GetMapping("/engineer")
	public String getStudentResource() {
		return "Resource for Students";
	}

	// Endpoint agora acessível sem verificação de role específica
	@GetMapping("/common")
	public String getCommonResource() {
		return "Resource for All Users";
	}
	
}