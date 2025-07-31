package com.sloth.OnlyStudent.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "simulations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Simulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String image; // URL ou caminho para a imagem
    
    // IDs do Unity Cloud
    private String productId; 
    private String environmentId;
    private String configId;
    
    private String keyApi;

    // Construtor para criar uma nova simulação sem ID
    public Simulation(String name, String description, String image, String productId, String environmentId, String configId, String keyApi) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.productId = productId;
        this.environmentId = environmentId;
        this.configId = configId;
        this.keyApi = keyApi;
    }
}
