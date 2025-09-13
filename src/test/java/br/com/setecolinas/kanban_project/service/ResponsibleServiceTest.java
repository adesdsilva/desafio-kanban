package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.ResponsibleRequestDTO;
import br.com.setecolinas.kanban_project.dto.ResponsibleResponseDTO;
import br.com.setecolinas.kanban_project.exceptions.NotFoundException;
import br.com.setecolinas.kanban_project.model.Responsible;
import br.com.setecolinas.kanban_project.model.Secretaria;
import br.com.setecolinas.kanban_project.repository.ResponsibleRepository;
import br.com.setecolinas.kanban_project.repository.SecretariaRepository;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ResponsibleService")
class ResponsibleServiceTest {

    @Mock
    private ResponsibleRepository repo;

    @Mock
    private SecretariaRepository secRepo;

    @InjectMocks
    private ResponsibleService service;

    private Responsible responsible;
    private Secretaria secretaria;
    private ResponsibleRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        secretaria = new Secretaria("Secretaria de Educação", "Responsável pela educação");
        secretaria.setId(1L);
        responsible = new Responsible("João da Silva", "joao.silva@example.com", "Analista");
        responsible.setId(1L);
        responsible.setSecretaria(secretaria);
        requestDTO = new ResponsibleRequestDTO("João da Silva", "joao.silva@example.com", "Analista", 1L);
    }

    @Test
    @DisplayName("Deve criar um novo responsável com secretaria")
    void create_shouldCreateResponsibleWithSecretaria() {
        when(secRepo.findById(1L)).thenReturn(Optional.of(secretaria));
        when(repo.save(any(Responsible.class))).thenReturn(responsible);

        ResponsibleResponseDTO result = service.create(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("joao.silva@example.com", result.email());
        assertEquals(1L, result.secId());
        verify(secRepo, times(1)).findById(1L);
        verify(repo, times(1)).save(any(Responsible.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar criar com secretaria inexistente")
    void create_shouldThrowNotFoundExceptionForNonExistentSecretaria() {
        when(secRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.create(new ResponsibleRequestDTO("João", "joao@email.com", "Teste", 99L)));
        verify(secRepo, times(1)).findById(99L);
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Deve encontrar responsável por ID")
    void findById_shouldFindResponsible() {
        when(repo.findById(1L)).thenReturn(Optional.of(responsible));

        ResponsibleResponseDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("joao.silva@example.com", result.email());
        verify(repo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar encontrar responsável que não existe")
    void findById_shouldThrowNotFoundException() {
        when(repo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99L));
        verify(repo, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve retornar página de responsáveis com sucesso")
    void findAll_shouldReturnPageOfResponsibles() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Responsible> page = new PageImpl<>(Collections.singletonList(responsible), pageable, 1);
        when(repo.findAll(pageable)).thenReturn(page);

        Page<ResponsibleResponseDTO> result = service.findAll(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("João da Silva", result.getContent().get(0).name());
        verify(repo, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar um responsável com sucesso")
    void update_shouldUpdateResponsible() {
        ResponsibleRequestDTO updateDto = new ResponsibleRequestDTO("João Atualizado", "novo.email@example.com", "Gerente", 1L);
        Responsible updatedResponsible = new Responsible("João Atualizado", "novo.email@example.com", "Gerente");
        updatedResponsible.setId(1L);
        updatedResponsible.setSecretaria(secretaria);

        when(repo.findById(1L)).thenReturn(Optional.of(responsible));
        when(secRepo.findById(1L)).thenReturn(Optional.of(secretaria));
        when(repo.save(any(Responsible.class))).thenReturn(updatedResponsible);

        ResponsibleResponseDTO result = service.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("João Atualizado", result.name());
        assertEquals("novo.email@example.com", result.email());
        verify(repo, times(1)).findById(1L);
        verify(secRepo, times(1)).findById(1L);
        verify(repo, times(1)).save(any(Responsible.class));
    }

    @Test
    @DisplayName("Deve deletar um responsável com sucesso")
    void delete_shouldDeleteResponsible() {
        when(repo.existsById(1L)).thenReturn(true);
        doNothing().when(repo).deleteById(1L);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(repo, times(1)).existsById(1L);
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar deletar responsável que não existe")
    void delete_shouldThrowNotFoundException() {
        when(repo.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.delete(99L));
        verify(repo, times(1)).existsById(99L);
        verify(repo, never()).deleteById(any());
    }
}
