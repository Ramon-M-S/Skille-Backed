package com.sloth.OnlyStudent.repository;

import com.sloth.OnlyStudent.entities.Simulation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationRepository extends JpaRepository<Simulation, Long> {
    
    // Método para buscar simulações pelo nome (útil para a barra de pesquisa)
    Page<Simulation> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Método para verificar se já existe uma simulação com o mesmo nome
    boolean existsByName(String name);
}