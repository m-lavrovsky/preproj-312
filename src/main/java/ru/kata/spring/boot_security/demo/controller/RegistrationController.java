package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repo.UserRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());
        //userService.initRoles();
        return "register";
    }

    @PostMapping("/register")
    public String addUser(@ModelAttribute("userForm") @Valid User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if ((userForm != null) && (userRepository.findByUsername(userForm.getUsername()) !=  null)) { /* (!userService.saveUser(userForm))*/
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "register";
        }
        System.out.println(userForm.getPassword());
        System.out.println(userForm.getPasswordConfirm());
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "register";
        }
        if ((userForm != null) && (userService.addUser(userForm))) {
            model.addAttribute("successMessage", "Пользователь успешно зарегистрирован, можно авторизоаться");
            System.out.println("юзер зареган");
        } else {
            model.addAttribute("successMessage", "Не удалось зарегистрировать пользователя в БД почему-то");
            System.out.println("юзер не зареган");
        }

        return "register";
    }
}
