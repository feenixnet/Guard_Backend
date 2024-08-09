package com.guard.admin.payload.response;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String error;
    private T data;

    public ApiResponse() { this.error = "";}

    public ApiResponse(String error, T data){
        this.error = error;
        this.data = data;
    }

    public ApiResponse(String error) {
        this.error = error;
    }

    public ApiResponse(T data) {
        this.data = data;
        this.error = "";
    }
}