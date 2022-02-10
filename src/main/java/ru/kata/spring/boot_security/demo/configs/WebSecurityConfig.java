package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.kata.spring.boot_security.demo.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final SuccessUserHandler successUserHandler;

    @Autowired
    UserService userService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public WebSecurityConfig(SuccessUserHandler successUserHandler) {
        this.successUserHandler = successUserHandler;
    }

    @Override
    protected void configure(HttpSecurity httpSec) throws Exception {
        httpSec
                .authorizeRequests()
        //Доступ только для не зарегистрированных пользователей:
                //.antMatchers("/","/register").not().fullyAuthenticated()
                .antMatchers("/","/register").anonymous()
        // только авторизованные
                .antMatchers("/user*").fullyAuthenticated()
        //Доступ только для пользователей с ролью Администратор:
                .antMatchers("/admin*").hasRole("ADMIN")
                //.antMatchers("/user").hasRole("USER")
                //Доступ разрешен всем пользователей:
    //надо ли?  .antMatchers("/", "/index").permitAll()
                //.antMatchers("/").permitAll()

                //Все остальные страницы требуют аутентификации
    //надо ли?  .anyRequest().authenticated()
    //Настройка для входа в систему
                .and()
                .formLogin().successHandler(successUserHandler)
                .loginPage("/")
                //Перенарпавление на страницу профиля пользователя после успешного входа
                //.defaultSuccessUrl("/user")
                .permitAll()
    //logout
                .and()
                .logout()
                .logoutUrl("/dologout")
                .logoutSuccessUrl("/")
                .logoutRequestMatcher(new AntPathRequestMatcher("/dologout"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();
    }

    // аутентификация inMemory
    /*@Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder() // подходит для демо
                        .username("user")
                        .password("user")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }*/

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }
}