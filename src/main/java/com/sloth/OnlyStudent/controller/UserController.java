package com.sloth.OnlyStudent.controller;

import com.sloth.OnlyStudent.entities.DTO.UserUpdateDTO;
import com.sloth.OnlyStudent.entities.User;
import com.sloth.OnlyStudent.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users") // Rota base para usuários
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Rota: GET /users/{id} - Corresponde a `getUser(id)` do frontend
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // Por segurança, você pode querer verificar se o usuário logado
        // é o mesmo do ID solicitado ou se é um admin.
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
    
    // Rota: PUT /users/{id} - Corresponde a `updateUser(id, userData)`
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO data) {
        User updatedUser = userService.update(id, data);
        return ResponseEntity.ok(updatedUser);
    }
}