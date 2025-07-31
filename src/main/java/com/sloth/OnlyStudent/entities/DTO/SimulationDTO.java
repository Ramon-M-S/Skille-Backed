package com.sloth.OnlyStudent.entities.DTO;

//Usado para receber os dados ao criar ou atualizar uma simulação
public record SimulationDTO(
 String name,
 String description,
 String image,
 String productId,
 String environmentId,
 String configId,
 String keyApi
) {}