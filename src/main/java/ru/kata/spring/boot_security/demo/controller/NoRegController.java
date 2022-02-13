package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import java.util.*;

@Controller
public class NoRegController {

    @Autowired
    private UserRepository userRepository;

    private final UserService userService;

    @Autowired
    public NoRegController(UserService us) {
        this.userService = us;
        this.userService.initRoles();
        // тестовый админ: логин = admin, пароль = test
        this.userService.addUser(new User(0L, "admin", "Админ", "Админов", 10, "test"),"admin");
        // тестовый юзер: логин = user, пароль = test и т.д.
        this.userService.addUser(new User(0L, "user", "Пользователь", "Юзеров", 52, "test"),"user");
        this.userService.addUser(new User(0L, "harrypotter30", "Гарри", "Поттер", 30, "test"),"user");
        this.userService.addUser(new User(0L, "dartwader99", "Дарт", "Вейдер", 99, "test"),"user");

        Set<Role> roles5 = new HashSet<>();
        roles5.add(new Role(1L));
        roles5.add(new Role(2L));
        this.userService.addUserMultiRole(new User(0L, "bilbo111", "Бильбо", "Беггинс", 111, "test"),roles5);
    }

    @GetMapping(value = "/")
    public String loginPage(ModelMap model, @RequestParam(defaultValue = "", required = false) String regsuccess) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername (auth.getName());
        if (user != null) {
            System.out.println("Переход авторизованным на / не разрешён - редиректим на /user");
            return "redirect:/user";
        } else {
            System.out.println("Юзер не авторизован");
        }
        return "index";
    }
}