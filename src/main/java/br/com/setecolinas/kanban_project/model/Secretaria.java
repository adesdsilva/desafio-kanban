package br.com.setecolinas.kanban_project.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "secretaria")
public class Secretaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column
    private String descricao;

    @OneToMany(mappedBy = "secretaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Responsible> responsaveis = new HashSet<>();

    public Secretaria() {
    }

    public Secretaria(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Responsible> getResponsaveis() {
        return responsaveis;
    }

    public void setResponsaveis(Set<Responsible> responsaveis) {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Secretaria that = (Secretaria) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome) && Objects.equals(descricao, that.descricao) && Objects.equals(responsaveis, that.responsaveis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, descricao, responsaveis);
    }

    @Override
    public String toString() {
        return "Secretaria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", responsaveis=" + responsaveis +
                '}';
    }
}
