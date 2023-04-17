package com.example.fixit;

public class User
{
    private String name;
    private String email;
    private String password;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    private String contact;
    private Address address;

    public User() {
    }

    public User(String name, String email, String password)
    {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = null;

    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String contact) {
        this.password = contact;
    }
}
