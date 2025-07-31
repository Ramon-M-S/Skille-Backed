package com.sloth.OnlyStudent.entities.DTO;

//Usado para enviar os dados detalhados de uma simulação para o frontend
public record SimulationConfigsDTO(
 Long id,
 String productId,
 String keyApi
 // A keyApi geralmente não é exposta por segurança
) {}
