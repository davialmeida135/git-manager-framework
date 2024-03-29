package com.projectmanager.model;

import lombok.Data;

@Data
public class UserModel {
    private String username;
    private long id;
    private String token;
    private String email;
    private String firstName;

    public UserModel(String username, long id, String token, String email, String firstName) {
        this.username = username;
        this.id = id;
        this.token = token;
        this.email = email;
        this.firstName = firstName;
    }

    
}
