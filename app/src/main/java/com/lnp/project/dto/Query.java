package com.lnp.project.dto;

public class Query {
    private String userName;
    private String Phone;

    private String email;

    private String Query;

    private String Address;

    public Query(String userName, String phone, String email, String query, String address) {
        this.userName = userName;
        Phone = phone;
        this.email = email;
        Query = query;
        Address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getQuery() {
        return Query;
    }

    public void setQuery(String query) {
        Query = query;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
