package com.utkanos.sweater.config;

import com.utkanos.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

@Configuration
@EnableWebSecurity
//чтобы работала фишка с запретом неадминов переходить на урлы для редактирования ролей
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //Запрашиваем авторизацию
                .authorizeRequests()
                        //предоставляем полный доступ на эти адреса (/greeting у нас нет, просто пример, что можно несколько адресов)
                        //**-включает вложенные адреса, *-только один сегмент адреса
                        .antMatchers("/", "/greeting", "/registration", "/static/**", "/registration/activation/*").permitAll()
                        //Предоставляем следующие адреса только админу (/** - все урлы, после /, даже вложенные)
                        .antMatchers("/users", "/users/**").hasAuthority("ADMIN")
                        //для всех остальныйх (any) требуем авторизацию
                        .anyRequest().authenticated()
                        .and()
                .formLogin()
                        //устанавливаем mapping по которому будет доступна эта форма логин.
                        .loginPage("/login")
                        //разрешаем всем ходить по этому mapping'у
                        .permitAll()
                        .and()
                //Позволит сохранять вход польхователя, если аккаунт перезапущен или сервер перезапущен
                .rememberMe()
                        .alwaysRemember(true)
                        .and()
                .logout()
                        //разрешаем всем ходить по этой форме
                        .permitAll()
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL)));

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                //кодирование пароля
                .passwordEncoder(passwordEncoder);
    }
}
