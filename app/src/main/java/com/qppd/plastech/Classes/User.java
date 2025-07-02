package com.qppd.plastech.Classes;

public class User {

    private String email;
    private String name;
    private String contact;
    private String password;
    private int status;
    private String created_at;

    public User(String email, String name, String contact, String password, int status, String created_at) {
        this.email = email;
        this.name = name;
        this.contact = contact;
        this.password = password;
        this.status = status;
        this.created_at = created_at;
    }

    public User(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

