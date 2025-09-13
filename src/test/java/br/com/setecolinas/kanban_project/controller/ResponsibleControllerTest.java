package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.dto.ResponsibleRequestDTO;
import br.com.setecolinas.kanban_project.dto.ResponsibleResponseDTO;
import br.com.setecolinas.kanban_project.service.ResponsibleService;
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
@DisplayName("Testes para ResponsibleController (sem @WebMvcTest)")
class ResponsibleControllerUnitaryTest {

    @Mock
    private ResponsibleService service;

    private ResponsibleController controller;

    private ResponsibleRequestDTO requestDTO;
    private ResponsibleResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        controller = new ResponsibleController(service);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        requestDTO = new ResponsibleRequestDTO("Nome Teste", "email@teste.com", "Função", 1L);
        responseDTO = new ResponsibleResponseDTO(1L, "Nome Teste", "email@teste.com", "Função", 1L);
    }

    @Test
    @DisplayName("Deve criar um responsável e retornar 201 Created")
    void create_shouldReturn201Created() {
        when(service.create(any(ResponsibleRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ResponsibleResponseDTO> response = controller.create(requestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(service, times(1)).create(any(ResponsibleRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar um responsável por ID com sucesso")
    void findById_shouldReturnResponsible() {
        when(service.findById(1L)).thenReturn(responseDTO);

        ResponseEntity<ResponsibleResponseDTO> response = controller.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar página de responsáveis com ou sem busca")
    void findAll_shouldReturnPageOfResponsibles() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ResponsibleResponseDTO> page = new PageImpl<>(List.of(responseDTO), pageable, 1);
        when(service.findAll(eq(null), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<ResponsibleResponseDTO>> response = controller.findAll(null, pageable);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(responseDTO, response.getBody().getContent().getFirst());
        verify(service, times(1)).findAll(eq(null), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar um responsável com sucesso")
    void update_shouldReturnUpdatedResponsible() {
        ResponsibleRequestDTO updateDto = new ResponsibleRequestDTO("Nome Atualizado", "novo@teste.com", "Nova Função", 1L);
        ResponsibleResponseDTO updatedResponse = new ResponsibleResponseDTO(1L, "Nome Atualizado", "novo@teste.com", "Nova Função", 1L);
        when(service.update(1L, updateDto)).thenReturn(updatedResponse);

        ResponseEntity<ResponsibleResponseDTO> response = controller.update(1L, updateDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedResponse, response.getBody());
        verify(service, times(1)).update(1L, updateDto);
    }

    @Test
    @DisplayName("Deve deletar um responsável e retornar 204 No Content")
    void delete_shouldReturn204NoContent() {
        doNothing().when(service).delete(1L);

        ResponseEntity<Void> response = controller.delete(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service, times(1)).delete(1L);
    }
}