package com.projectmanager.forms;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TarefaForm {
    @NotEmpty(message = "Title is required")
    private String titulo;

    @NotEmpty(message = "Description is required")
    private String descricao;

    @NotNull(message = "Prazo is required")
    private String prazo;

    private List<String> collaborators;

    // getters and setters for all fields
}