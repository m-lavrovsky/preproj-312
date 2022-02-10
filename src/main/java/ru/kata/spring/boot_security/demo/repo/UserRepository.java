package ru.kata.spring.boot_security.demo.repo;

import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);

}