package ru.kata.spring.boot_security.demo.model;

public class UserOperation {
    private User user;
    private String action;

    public UserOperation(User user, String action) {
        this.user = user;
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
