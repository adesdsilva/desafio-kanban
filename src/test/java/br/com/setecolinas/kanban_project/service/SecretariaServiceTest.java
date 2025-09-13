package br.com.setecolinas.kanban_project.service;

import br.com.setecolinas.kanban_project.dto.SecretariaRequestDTO;
import br.com.setecolinas.kanban_project.dto.SecretariaResponseDTO;
import br.com.setecolinas.kanban_project.exceptions.NotFoundException;
import br.com.setecolinas.kanban_project.model.Secretaria;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para SecretariaService")
class SecretariaServiceTest {

    @Mock
    private SecretariaRepository repo;

    @InjectMocks
    private SecretariaService service;

    private Secretaria secretaria;
    private SecretariaRequestDTO requestDTO;
    private SecretariaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        secretaria = new Secretaria("Secretaria de Educação", "Responsável pela educação");
        secretaria.setNome("Secretaria de Educação");
        secretaria.setDescricao("Responsável pela educação");
        secretaria.setId(1L);
        requestDTO = new SecretariaRequestDTO("Secretaria de Educação", "Responsável pela educação");
        responseDTO = new SecretariaResponseDTO(1L, "Secretaria de Educação", "Responsável pela educação");
    }

    @Test
    @DisplayName("Deve criar uma nova secretaria com sucesso")
    void create_shouldCreateSecretaria() {
        when(repo.save(any(Secretaria.class))).thenReturn(secretaria);

        SecretariaResponseDTO result = service.create(requestDTO);

        assertNotNull(result);
        assertEquals(responseDTO.id(), result.id());
        assertEquals(responseDTO.nome(), result.nome());
        verify(repo, times(1)).save(any(Secretaria.class));
    }

    @Test
    @DisplayName("Deve encontrar secretaria por ID com sucesso")
    void findById_shouldFindSecretaria() {
        when(repo.findById(1L)).thenReturn(Optional.of(secretaria));

        SecretariaResponseDTO result = service.findById(1L);

        assertNotNull(result);
        assertEquals(responseDTO.id(), result.id());
        verify(repo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar encontrar secretaria que não existe")
    void findById_shouldThrowNotFoundException() {
        when(repo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findById(99L));
        verify(repo, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve retornar todas as secretarias paginadas")
    void findAll_shouldReturnPageOfSecretarias() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Secretaria> page = new PageImpl<>(List.of(secretaria), pageable, 1);
        when(repo.findAll(pageable)).thenReturn(page);

        Page<SecretariaResponseDTO> result = service.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(responseDTO.nome(), result.getContent().get(0).nome());
        verify(repo, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar uma secretaria existente")
    void update_shouldUpdateSecretaria() {
        SecretariaRequestDTO updateDto = new SecretariaRequestDTO("Secretaria de Cultura", "Nova descrição");
        Secretaria updatedSecretaria = new Secretaria("Secretaria de Cultura", "Nova descrição");
        updatedSecretaria.setId(1L);

        when(repo.findById(1L)).thenReturn(Optional.of(secretaria));
        when(repo.save(any(Secretaria.class))).thenReturn(updatedSecretaria);

        SecretariaResponseDTO result = service.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("Secretaria de Cultura", result.nome());
        assertEquals("Nova descrição", result.descricao());
        verify(repo, times(1)).findById(1L);
        verify(repo, times(1)).save(any(Secretaria.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar atualizar secretaria que não existe")
    void update_shouldThrowNotFoundException() {
        SecretariaRequestDTO updateDto = new SecretariaRequestDTO("Secretaria de Cultura", "Nova descrição");
        when(repo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.update(99L, updateDto));
        verify(repo, times(1)).findById(99L);
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar uma secretaria com sucesso")
    void delete_shouldDeleteSecretaria() {
        when(repo.existsById(1L)).thenReturn(true);
        doNothing().when(repo).deleteById(1L);

        assertDoesNotThrow(() -> service.delete(1L));
        verify(repo, times(1)).existsById(1L);
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao tentar deletar secretaria que não existe")
    void delete_shouldThrowNotFoundException() {
        when(repo.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.delete(99L));
        verify(repo, times(1)).existsById(99L);
        verify(repo, never()).deleteById(any());
    }
}
