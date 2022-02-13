package ru.kata.spring.boot_security.demo.model;

import java.util.*;

public class UserOperation {
    private User user;
    private String action;
    private ArrayList<String> roles;

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

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public Set<Role> getRolesAsSet() {
        Set<Role> newRoles = new HashSet<>();
        if (this.getRoles().contains("ROLE_ADMIN")) {
            //roles.add(new Role(1L));
            newRoles.add(new Role(1L,"ROLE_ADMIN","админ"));
        }
        if (this.getRoles().contains("ROLE_USER")) {
            //roles.add(new Role(2L));
            newRoles.add(new Role(2L,"ROLE_USER","пользователь"));
        }
        return newRoles;
    }


}
