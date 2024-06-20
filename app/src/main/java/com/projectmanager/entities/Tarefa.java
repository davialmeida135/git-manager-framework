package com.projectmanager.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class Tarefa extends ScheduledActivity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int id_criador;
    private String data_criacao;
    private boolean completa;
    private int id_projeto;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "colaborador",
              joinColumns = @JoinColumn(name = "tarefa_id"),
              inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private List<Usuario> usuarios;

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_criador() {
        return this.id_criador;
    }

    public void setId_criador(int id_criador) {
        this.id_criador = id_criador;
    }

    public String getData_criacao() {
        return this.data_criacao;
    }

    public void setData_criacao(String data_criacao) {
        this.data_criacao = data_criacao;
    }

    public Boolean getCompleta() {
        return this.completa;
    }

    public void setCompleta(Boolean completa) {
        this.completa = completa;
    }

    public int getId_projeto() {
        return this.id_projeto;
    }

    public void setId_projeto(int id_projeto) {
        this.id_projeto = id_projeto;
    }
}
