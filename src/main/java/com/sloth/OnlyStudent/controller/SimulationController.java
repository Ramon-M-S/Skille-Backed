package com.sloth.OnlyStudent.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate; // <-- 1. ADICIONE ESTE IMPORT
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.sloth.OnlyStudent.entities.Simulation;
import com.sloth.OnlyStudent.entities.DTO.SimulationConfigsDTO;
import com.sloth.OnlyStudent.entities.DTO.SimulationDTO;
import com.sloth.OnlyStudent.entities.DTO.SimulationDetailsDTO;
import com.sloth.OnlyStudent.repository.SimulationRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("simulations")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationRepository repository;
    private final RestTemplate restTemplate; // <-- 2. ADICIONE ESTE CAMPO FINAL

    @PostMapping
    @Transactional
    public ResponseEntity<?> createSimulation(@RequestBody @Valid SimulationDTO data, UriComponentsBuilder uriBuilder) {
        if (repository.existsByName(data.name())) {
            return ResponseEntity.badRequest().body("Já existe uma simulação com este nome.");
        }
        
        Simulation newSimulation = new Simulation(data.name(), data.description(), data.image(), data.productId(), data.environmentId(), data.configId(), data.keyApi());
        repository.save(newSimulation);

        var uri = uriBuilder.path("/simulations/{id}").buildAndExpand(newSimulation.getId()).toUri();

        return ResponseEntity.created(uri).body(new SimulationDetailsDTO(newSimulation.getId(), newSimulation.getName(), newSimulation.getDescription(), newSimulation.getImage(), newSimulation.getProductId(), newSimulation.getEnvironmentId(), newSimulation.getConfigId()));
    }

    @GetMapping
    public ResponseEntity<Page<SimulationDetailsDTO>> listSimulations(@RequestParam(defaultValue = "") String search, @PageableDefault(size = 10, sort = {"name"}) Pageable pageable) {
        Page<Simulation> simulationsPage = repository.findByNameContainingIgnoreCase(search, pageable);
        Page<SimulationDetailsDTO> dtoPage = simulationsPage.map(s -> new SimulationDetailsDTO(s.getId(), s.getName(), s.getDescription(), s.getImage(), s.getProductId(), s.getEnvironmentId(), s.getConfigId()));
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimulationDetailsDTO> getSimulationById(@PathVariable Long id) {
        return repository.findById(id)
                .map(s -> ResponseEntity.ok(new SimulationDetailsDTO(s.getId(), s.getName(), s.getDescription(), s.getImage(), s.getProductId(), s.getEnvironmentId(), s.getConfigId())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<SimulationDetailsDTO> updateSimulation(@PathVariable Long id, @RequestBody @Valid SimulationDTO data) {
    	
        return repository.findById(id)
                .map(simulation -> {
                    simulation.setName(data.name());
                    simulation.setDescription(data.description());
                    simulation.setImage(data.image());
                    simulation.setProductId(data.productId());
                    simulation.setEnvironmentId(data.environmentId());
                    simulation.setConfigId(data.configId());
                    // A VERIFICAÇÃO MAIS IMPORTANTE:
                    // Só atualiza a keyApi se uma nova chave foi enviada (não é nula nem vazia)
                    if (data.keyApi() != null && !data.keyApi().isBlank()) {
                        // Aqui você também pode adicionar lógica para criptografar a nova chave
                        simulation.setKeyApi(data.keyApi());
                    }
                    // Não precisa do repository.save() por causa do @Transactional
                    return ResponseEntity.ok(new SimulationDetailsDTO(simulation.getId(), simulation.getName(), simulation.getDescription(), simulation.getImage(), simulation.getProductId(), simulation.getEnvironmentId(), simulation.getConfigId()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteSimulation(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // Método auxiliar para criar uma resposta de erro JSON
    private JsonNode createErrorBody(String message) {
        // Você pode usar ObjectMapper aqui para criar um JsonNode de forma mais robusta
        // mas para simplicidade, vamos usar uma string. No frontend, você faria JSON.parse()
        // Uma abordagem melhor seria retornar um objeto de erro, como: Map.of("error", message)
        com.fasterxml.jackson.databind.node.ObjectNode errorNode = 
            new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode();
        errorNode.put("message", message);
        return errorNode;
    }
    
    @GetMapping("/{id}/configs")
    public ResponseEntity<JsonNode> getSimulationUnityConfigSimplified(@PathVariable Long id) {
        try {
            // 1. Busca a simulação local (sem alterações)
            Simulation simulation = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Simulação com ID " + id + " não encontrada."));
            
            String projectId = simulation.getProductId();
            String keyApi = simulation.getKeyApi();

            if (keyApi == null || keyApi.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(createErrorBody("A simulação não possui uma chave de API (keyApi) configurada."));
            }

            // 2. Prepara a chamada (sem alterações)
            String unityApiUrl = "https://services.api.unity.com/remote-config/v1/projects/" + projectId + "/configs";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + keyApi);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 3. Faz a requisição (sem alterações)
            ResponseEntity<JsonNode> unityResponse = restTemplate.exchange(
                unityApiUrl, HttpMethod.GET, entity, JsonNode.class
            );

            // 4. Extrai os dados (sem alterações)
            JsonNode responseBody = unityResponse.getBody();
            JsonNode desiredArray = responseBody.path("configs").path(0).path("value");
           
            // 5. Retorna com sucesso
            return ResponseEntity.ok(desiredArray);

        } catch (EntityNotFoundException e) {
            // ERRO 1: Simulação não encontrada no seu banco
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(createErrorBody(e.getMessage()));

        } catch (HttpClientErrorException e) {
            // ERRO 2: A API da Unity retornou um erro 4xx. AGORA VAMOS INVESTIGAR.
            String errorBody = e.getResponseBodyAsString();
            System.err.println("Erro da API da Unity: " + e.getStatusCode() + " | Corpo: " + errorBody);
            
            // ====================================================================
            // ===== LÓGICA DE VERIFICAÇÃO REFINADA =====
            //
            // Primeiro, verificamos se a mensagem de erro contém pistas sobre o projectId.
            // A API da Unity pode retornar mensagens como "Project not found" ou algo similar.
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                String userFriendlyMessage = "Falha na autorização com a API da Unity. Verifique se o ID do Projeto (projectId) está correto. ";
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(createErrorBody(userFriendlyMessage));
            }
            
            // Se a mensagem não fala sobre o projeto, então provavelmente é um problema de autenticação.
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                     .body(createErrorBody("A chave de API (keyApi) é inválida ou não tem permissão. Verifique as credenciais da Unity."));
            }
            // ====================================================================

            // Para qualquer outro erro 4xx inesperado
            return ResponseEntity.status(e.getStatusCode())
                                 .body(createErrorBody("Erro na requisição para a API da Unity: " + errorBody));
                                 
        } catch (RestClientException e) {
            // ERRO 3: Erro de conexão (CORRETO)
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                 .body(createErrorBody("Não foi possível conectar ao serviço da Unity. Tente novamente mais tarde."));

        } catch (Exception e) {
            // ERRO 4: Qualquer outro erro inesperado (CORRETO)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(createErrorBody("Ocorreu um erro interno no servidor."));
        }
    }
}