package com.se.letscook.models;

import com.google.gson.annotations.SerializedName;

public class MethodArgumentNotValidError {
    @SerializedName(value = "type")
    private String type;

    @SerializedName(value = "status")
    private int status;

    @SerializedName(value = "errors")
    private String[] errors;

    public MethodArgumentNotValidError(String type, int status, String[] errors){
        this.type = type;
        this.status = status;
        this.errors = errors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }
}
