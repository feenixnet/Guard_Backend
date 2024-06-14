package com.guard.admin.payload.dto;

import lombok.Data;

@Data
public class ChatUser {
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private Integer id;

    public ChatUser() {

    }
}
