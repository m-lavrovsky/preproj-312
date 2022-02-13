package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repo.UserRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Set;

import ru.kata.spring.boot_security.demo.model.UserOperation;

@Controller
public class LoggedController {
    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @GetMapping(value="/user")
    public ModelAndView userPage(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername (auth.getName());
        modelAndView.addObject("userdata",user);

        Set<Role> roles = user.getRoles();
        if (!roles.isEmpty()) {
            for (Role role : roles) {
                System.out.println("Юзер "+user.getName()+" имеет права: "+role.getName());
            }
        }
        modelAndView.setViewName("user");
        return modelAndView;
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("allUsers", userService.getAllUsers());
        return "admin";
        //return "redirect:/admin";
    }

    // обработчик CRUD
    @PostMapping("/admin")
    public String processUserOperation(ModelMap model, @ModelAttribute("userop") UserOperation uop) {
        if (uop.getAction().equals("deleteuser")) {
            try {
                userService.deleteUser(uop.getUser().getId());
                model.addAttribute("DeleteDone","Пользователь "+uop.getUser().getUsername()+
                                    " ("+uop.getUser().getName()+" "+uop.getUser().getLastname()+") удалён");
            }
            catch (RuntimeException e) {
                model.addAttribute("UserAdded","Не получилось удалить юзера "+uop.getUser().getUsername()+", почему - не знаю");
            }
        }
        if (uop.getAction().equals("adduser")) {
            if ((uop.getUser().getPassword().equals(uop.getUser().getPasswordConfirm())) && (!uop.getUser().getPassword().equals(""))) {
                try {
                    userService.addUserMultiRole(uop.getUser(),uop.getRolesAsSet());
                    model.addAttribute("UserAdded","Пользователь "+uop.getUser().getUsername()+ " добавлен");
                }
                catch (RuntimeException e) {
                    model.addAttribute("UserAdded","Не получилось добавить пользователя, почему - не знаю");
                }
            } else {
                model.addAttribute("UserAdded","Не получилось добавить пользователя: пароли не совпадают и/или пустые");
            }
        }
        if (uop.getAction().equals("edituser")) {
            if (userService.editUser(uop.getUser(),uop.getRolesAsSet())) {
                model.addAttribute("UserEdited","Пользователь с id = "+uop.getUser().getId()+
                                    " (текущий юзернейм "+uop.getUser().getUsername()+") исправлен");
            } else {
                model.addAttribute("UserEdited","Не получилось исправить пользоателя id = "+uop.getUser().getId()+
                                " (текущий юзернейм "+uop.getUser().getUsername()+"), почему не знаю");
            }
        }

        model.addAttribute("allUsers", userService.getAllUsers());
        return "/admin";
    }

    // страница редактирования пользователя
    @GetMapping(value = "/admin/edit", produces = "text/html; charset=utf-8")
    public String userEditPage(Model model, @RequestParam(defaultValue = "-1", required = false) long uid) {
        try {
            if (userRepository.existsById(uid)) {
                User user = userService.findUserById(uid);
                user.setPassword("");
                UserOperation uop = new UserOperation(user,"edituser");
                model.addAttribute("descr","Редактирование пользователя");
                model.addAttribute("userop", uop);
                return "/admin/edit";
            }
        }
        catch (IllegalStateException e) {
            model.addAttribute("descr","Ничего не получится: Id = '"+uid+"' юзера для редактирования некорректен");
            return "redirect:/admin/wrong-id";
        }
        return "redirect:/admin/wrong-id";
    }

    // страница добавления нового пользователя
    @GetMapping(value = "/admin/add", produces = "text/html; charset=utf-8")
    public String userAddPage(Model model) {
        User user = new User();
        user.setId(-1L);
        UserOperation uop = new UserOperation(user,"adduser");
        model.addAttribute("descr","Добавление нового пользователя");
        model.addAttribute("userop", uop);
        return "/admin/edit";
    }

    // страница удаления пользователя
    @GetMapping(value = "/admin/delete")
    public String userDeletePage(Model model, @RequestParam(required = true, defaultValue = "0") long uid) {
        if (userRepository.existsById(uid)) {
            User user = userRepository.findById(uid).get();
            UserOperation uop = new UserOperation(user,"deleteuser");
            model.addAttribute("userop",uop);
            //model.addAttribute("user",user);
            return "/admin/delete";
        }
        else {
            model.addAttribute("descr","Ничего не получится: Id = "+uid+" юзера для удаления некорректен");
            return "redirect:/admin/wrong-id";
        }
    }

    /*@GetMapping("/admin/gt/{userId}")
    public String  gtUser(@PathVariable("userId") Long userId, Model model) {
        model.addAttribute("allUsers", userService.usergtList(userId));
        return "admin";
    }*/
}
