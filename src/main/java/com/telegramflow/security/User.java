package com.telegramflow.security;

public interface User {

    Integer getId();

    void setId(Integer id);

    String getUsername();

    void setUsername(String username);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    Role getRole();

    void setRole(Role role);

    String getPhone();

    void setPhone(String phone);

}
