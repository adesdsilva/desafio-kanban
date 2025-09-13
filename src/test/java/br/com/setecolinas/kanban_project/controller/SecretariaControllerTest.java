package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.dto.SecretariaRequestDTO;
import br.com.setecolinas.kanban_project.dto.SecretariaResponseDTO;
import br.com.setecolinas.kanban_project.service.SecretariaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para SecretariaController (sem @WebMvcTest)")
class SecretariaControllerUnitaryTest {

    @Mock
    private SecretariaService service;

    private SecretariaController controller;

    private SecretariaRequestDTO requestDTO;
    private SecretariaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        controller = new SecretariaController(service);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        requestDTO = new SecretariaRequestDTO("Secretaria Teste", "Descrição da Secretaria");
        responseDTO = new SecretariaResponseDTO(1L, "Secretaria Teste", "Descrição da Secretaria");
    }

    @Test
    @DisplayName("Deve criar uma secretaria e retornar 201 Created")
    void create_shouldReturn201Created() {
        when(service.create(any(SecretariaRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<SecretariaResponseDTO> response = controller.create(requestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(service, times(1)).create(any(SecretariaRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar uma secretaria por ID com sucesso")
    void findById_shouldReturnSecretaria() {
        when(service.findById(1L)).thenReturn(responseDTO);

        ResponseEntity<SecretariaResponseDTO> response = controller.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar página de secretarias")
    void findAll_shouldReturnPageOfSecretarias() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SecretariaResponseDTO> page = new PageImpl<>(List.of(responseDTO), pageable, 1);
        when(service.findAll(pageable)).thenReturn(page);

        ResponseEntity<Page<SecretariaResponseDTO>> response = controller.findAll(pageable);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(responseDTO, response.getBody().getContent().getFirst());
        verify(service, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar uma secretaria com sucesso")
    void update_shouldReturnUpdatedSecretaria() {
        SecretariaRequestDTO updateDto = new SecretariaRequestDTO("Atualizada", "Nova Descrição");
        SecretariaResponseDTO updatedResponse = new SecretariaResponseDTO(1L, "Atualizada", "Nova Descrição");
        when(service.update(1L, updateDto)).thenReturn(updatedResponse);

        ResponseEntity<SecretariaResponseDTO> response = controller.update(1L, updateDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedResponse, response.getBody());
        verify(service, times(1)).update(1L, updateDto);
    }

    @Test
    @DisplayName("Deve deletar uma secretaria e retornar 204 No Content")
    void delete_shouldReturn204NoContent() {
        doNothing().when(service).delete(1L);

        ResponseEntity<Void> response = controller.delete(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service, times(1)).delete(1L);
    }
}