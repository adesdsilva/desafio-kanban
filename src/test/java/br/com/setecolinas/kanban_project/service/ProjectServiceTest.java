package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.ProjectRequestDTO;
import br.com.setecolinas.kanban_project.dto.ProjectResponseDTO;
import br.com.setecolinas.kanban_project.exceptions.BusinessException;
import br.com.setecolinas.kanban_project.exceptions.NotFoundException;
import br.com.setecolinas.kanban_project.model.Project;
import br.com.setecolinas.kanban_project.model.ProjectStatus;
import br.com.setecolinas.kanban_project.model.Responsible;
import br.com.setecolinas.kanban_project.repository.ProjectRepository;
import br.com.setecolinas.kanban_project.repository.ResponsibleRepository;
import br.com.setecolinas.kanban_project.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ProjectService")
class ProjectServiceTest {

    @Mock
    private ProjectRepository repo;

    @Mock
    private ResponsibleRepository respRepo;

    @InjectMocks
    private ProjectService service;

    private Project project;
    private ProjectRequestDTO requestDTO;
    private Responsible responsible;

    @BeforeEach
    void setUp() {
        responsible = new Responsible("João da Silva", "joao.silva@example.com", "Desenvolvedor");
        responsible.setId(1L);

        project = new Project("Novo Projeto");
        project.setId(1L);
        project.setPlannedStart(LocalDate.now().plusDays(10));
        project.setPlannedEnd(LocalDate.now().plusDays(20));

        // CORREÇÃO: Usar um HashSet para criar um conjunto mutável
        Set<Responsible> responsiblesSet = new HashSet<>();
        responsiblesSet.add(responsible);
        project.setResponsibles(responsiblesSet);

        requestDTO = new ProjectRequestDTO(
                "Novo Projeto",
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20),
                null,
                null,
                Set.of(1L)
        );
    }

    @Test
    @DisplayName("Deve criar um projeto com sucesso")
    void create_shouldCreateProject() {
        when(respRepo.findById(1L)).thenReturn(Optional.of(responsible));
        when(repo.save(any(Project.class))).thenReturn(project);

        ProjectResponseDTO result = service.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Novo Projeto", result.name());
        verify(respRepo, times(1)).findById(1L);
        verify(repo, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("Deve encontrar projeto por ID com sucesso")
    void findById_shouldFindProject() {
        when(repo.findById(1L)).thenReturn(Optional.of(project));

        ProjectResponseDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(repo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao encontrar projeto inexistente")
    void findById_shouldThrowNotFoundException() {
        when(repo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99L));
        verify(repo, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve retornar página de projetos")
    void findAll_shouldReturnPageOfProjects() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> page = new PageImpl<>(Collections.singletonList(project), pageable, 1);
        when(repo.findAll(pageable)).thenReturn(page);

        Page<ProjectResponseDTO> result = service.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Novo Projeto", result.getContent().get(0).name());
        verify(repo, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar um projeto com sucesso")
    void update_shouldUpdateProject() {
        ProjectRequestDTO updateDto = new ProjectRequestDTO("Projeto Atualizado", null, null, null, null, Set.of(1L));
        Project updatedProject = new Project("Projeto Atualizado");
        updatedProject.setId(1L);

        // CORREÇÃO: Usar um HashSet para o objeto mockado
        Set<Responsible> updatedResponsibles = new HashSet<>();
        updatedResponsibles.add(responsible);
        updatedProject.setResponsibles(updatedResponsibles);

        when(repo.findById(1L)).thenReturn(Optional.of(project));
        when(respRepo.findById(1L)).thenReturn(Optional.of(responsible));
        when(repo.save(any(Project.class))).thenReturn(updatedProject);

        ProjectResponseDTO result = service.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Projeto Atualizado", result.name());
        verify(repo, times(1)).findById(1L);
        verify(repo, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar projeto inexistente")
    void update_shouldThrowNotFoundException() {
        ProjectRequestDTO updateDto = new ProjectRequestDTO("Projeto Atualizado", null, null, null, null, Set.of(1L));
        when(repo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(99L, updateDto));
        verify(repo, times(1)).findById(99L);
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar um projeto com sucesso")
    void delete_shouldDeleteProject() {
        when(repo.existsById(1L)).thenReturn(true);
        doNothing().when(repo).deleteById(1L);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(repo, times(1)).existsById(1L);
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar deletar projeto que não existe")
    void delete_shouldThrowNotFoundException() {
        when(repo.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.delete(99L));
        verify(repo, times(1)).existsById(99L);
        verify(repo, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve transitar o status para CONCLUIDO e preencher actualEnd")
    void transition_shouldSetActualEndWhenCompleted() {
        project.setActualStart(LocalDate.now().minusDays(5));
        when(repo.findById(1L)).thenReturn(Optional.of(project));
        when(repo.save(any(Project.class))).thenReturn(project);

        service.transition(1L, ProjectStatus.CONCLUIDO);

        assertNotNull(project.getActualEnd());
        assertEquals(ProjectStatus.CONCLUIDO, project.getStatus());
        verify(repo, times(1)).findById(1L);
        verify(repo, times(1)).save(project);
    }
}
