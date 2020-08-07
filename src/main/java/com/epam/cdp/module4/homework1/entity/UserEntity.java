package com.epam.cdp.module4.homework1.entity;

import com.epam.cdp.module4.homework1.model.User;

public class UserEntity extends AbstractEntity implements User {

    private String name;
    private String email;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }
}
