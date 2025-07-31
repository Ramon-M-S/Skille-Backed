package com.sloth.OnlyStudent.entities.DTO;

//Usado para enviar os dados detalhados de uma simulação para o frontend
public record SimulationDetailsDTO(
 Long id,
 String name,
 String description,
 String image,
 String productId,
 String environmentId,
 String configId
 // A keyApi geralmente não é exposta por segurança
) {}
