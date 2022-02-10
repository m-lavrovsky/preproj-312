package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repo.RoleRepository;
import ru.kata.spring.boot_security.demo.repo.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public User findUserByIdOrNull(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(null);
    }

    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        Iterable<User> iter = userRepository.findAll();
        for (User user : iter) {
            result.add(user);
        }
        return result;
        //return userRepository.findAll();
    }

    public boolean editUser(Long id, User user) {
        User userFromDB = findUserByIdOrNull(id);
        if (userFromDB != null) {
            user.setRoles(userFromDB.getRoles());
            if (!user.getPassword().equals(user.getPasswordConfirm())) {
                return false;
            } else {
                if ((!user.getPassword().equals(""))) {
                    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                } else {
                    user.setPassword(userFromDB.getPassword());
                }
            }
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean addUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            return false;
        }
        user.setRoles(Collections.singleton(new Role(2L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean addUser(User user, String role) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            return false;
        }
        if (role.equals("admin")) {
            user.setRoles(Collections.singleton(new Role(1L)));
        } else if (role.equals("user")) {
            user.setRoles(Collections.singleton(new Role(2L)));
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    @Transactional
    public void initRoles() {
        //em.createNativeQuery("INSERT INTO roles values ('1','ROLE_ADMIN')").executeUpdate();
        em.persist(new Role(1L,"ROLE_ADMIN"));
        em.persist(new Role(2L,"ROLE_USER"));
    }

    /*public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }*/
}
