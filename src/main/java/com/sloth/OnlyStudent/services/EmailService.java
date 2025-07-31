package com.sloth.OnlyStudent.services;

import com.sloth.OnlyStudent.entities.DTO.SupportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendSupportEmail(SupportDTO supportData) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(supportData.email()); // O email de quem enviou
        message.setTo("seu-email-de-suporte@dominio.com"); // Para onde o email será enviado
        message.setSubject("Nova mensagem de suporte de: " + supportData.name());
        
        String emailBody = "Você recebeu uma nova mensagem de suporte.\n\n" +
                           "Nome: " + supportData.name() + "\n" +
                           "Email: " + supportData.email() + "\n\n" +
                           "Mensagem:\n" + supportData.descricao();
        
        message.setText(emailBody);
        mailSender.send(message);
    }
}
