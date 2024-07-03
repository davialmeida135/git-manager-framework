package com.projectmanager.forms;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class FeedbackForm {
    @NotEmpty(message = "Message is required")
    private String mensagem;

    @NotEmpty(message = "Target is required")
    private List<String> collaborators;

    // getters and setters for all fields
}