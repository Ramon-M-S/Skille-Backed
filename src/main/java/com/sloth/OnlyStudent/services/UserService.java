package com.sloth.OnlyStudent.services;

import com.sloth.OnlyStudent.entities.DTO.UserUpdateDTO;
import com.sloth.OnlyStudent.entities.User;
import com.sloth.OnlyStudent.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o ID: " + id));
    }

    @Transactional
    public User update(Long id, UserUpdateDTO data) {
        User user = findById(id);
        
        // Atualiza apenas os campos que foram fornecidos
        if (data.name() != null && !data.name().isEmpty()) {
            user.setName(data.name());
        }
        if (data.telephone() != null && !data.telephone().isEmpty()) {
            user.setTelephone(data.telephone());
        }
        
        // O repository.save() é chamado automaticamente pelo @Transactional
        return user;
    }
}
