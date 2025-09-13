package br.com.setecolinas.kanban_project.controller;

import br.com.setecolinas.kanban_project.dto.ProjectRequestDTO;
import br.com.setecolinas.kanban_project.dto.ProjectResponseDTO;
import br.com.setecolinas.kanban_project.model.ProjectStatus;
import br.com.setecolinas.kanban_project.service.ProjectService;
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ProjectController (sem @WebMvcTest)")
class ProjectControllerUnitaryTest {

    @Mock
    private ProjectService service;

    private ProjectController controller;

    private ProjectRequestDTO requestDTO;
    private ProjectResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        controller = new ProjectController(service);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        requestDTO = new ProjectRequestDTO("Projeto Teste", null, null,
                null, null, Set.of(1L));
        responseDTO = new ProjectResponseDTO(1L, "Projeto Teste", null, null,
                null, null, null, null,
                1.0, Collections.emptySet());
    }

    @Test
    @DisplayName("Deve criar um projeto e retornar 201 Created")
    void create_shouldReturn201Created() {
        when(service.create(any(ProjectRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<ProjectResponseDTO> response = controller.create(requestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(service, times(1)).create(any(ProjectRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar um projeto por ID com sucesso")
    void findById_shouldReturnProject() {
        when(service.findById(1L)).thenReturn(responseDTO);

        ResponseEntity<ProjectResponseDTO> response = controller.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar página de projetos")
    void findAll_shouldReturnPageOfProjects() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProjectResponseDTO> page = new PageImpl<>(List.of(responseDTO), pageable, 1);
        when(service.findAll(pageable)).thenReturn(page);

        ResponseEntity<Page<ProjectResponseDTO>> response = controller.findAll(pageable);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(responseDTO, response.getBody().getContent().getFirst());
        verify(service, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar um projeto com sucesso")
    void update_shouldReturnUpdatedProject() {
        ProjectRequestDTO updateDto = new ProjectRequestDTO("Projeto Atualizado", null,
                null, null, null, Set.of(1L));
        ProjectResponseDTO updatedResponse = new ProjectResponseDTO(1L, "Projeto Atualizado",
                null, null, null, null, null, null,
                1.0, Collections.emptySet());
        when(service.update(1L, updateDto)).thenReturn(updatedResponse);

        ResponseEntity<ProjectResponseDTO> response = controller.update(1L, updateDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedResponse, response.getBody());
        verify(service, times(1)).update(1L, updateDto);
    }

    @Test
    @DisplayName("Deve transitar o status de um projeto com sucesso")
    void transition_shouldReturnUpdatedProject() {
        ProjectResponseDTO transitionedResponse = new ProjectResponseDTO(1L, "Projeto Teste", null,
                null, null, null, null, null,
                1.0, Collections.emptySet());
        when(service.transition(1L, ProjectStatus.EM_ANDAMENTO)).thenReturn(transitionedResponse);

        ResponseEntity<ProjectResponseDTO> response = controller.transition(1L, ProjectStatus.EM_ANDAMENTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transitionedResponse, response.getBody());
        verify(service, times(1)).transition(1L, ProjectStatus.EM_ANDAMENTO);
    }

    @Test
    @DisplayName("Deve deletar um projeto e retornar 204 No Content")
    void delete_shouldReturn204NoContent() {
        doNothing().when(service).delete(1L);

        ResponseEntity<Void> response = controller.delete(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service, times(1)).delete(1L);
    }
}