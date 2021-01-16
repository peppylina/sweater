package com.utkanos.sweater;


import com.utkanos.sweater.controllers.NewsController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//указываем окружение, в котором будет запускаться тесты
@RunWith(SpringRunner.class)
//показываем, где будут тесты
@SpringBootTest
//создает фейковое окружение, не подгружая настоящую программу
@AutoConfigureMockMvc
//указываем б***ь какие проперти выбирать, извините за мат, потратил 2 часа, чтобы понять в чем дело
@TestPropertySource("/application-test.properties")
//указываем, какой скрипт надо выполнить до и после каждого теста. Можно указывать над конкретным тестом
//здесь скрипты создания пользователей и сразу же создание постов от них. Порядок выполнения запросов важен!
@Sql(value = {"/loginTest/create-user-before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/loginTest/create-user-after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${hostname}")
    private String hostname;

    @Autowired
    private NewsController newsController;

    @Test
    public void rootTest() throws Exception {
        //проверяем, получилось ли заинжектить контескт
        //assertThat(newsController).isNotNull();
        //пытаемся отправить гет запрос
        this.mockMvc.perform(get("/"))
                //выводим логи запроса
                .andDo(print())
                //ожидаем, что статус будет ок
                .andExpect(status().isOk())
                //ожидаем, что контент, присланный с сервера будет содержать подстроку substring
                .andExpect(content().string(containsString("Welcome")))
                .andExpect(content().string(containsString("unknown")))
                .andExpect(content().string(containsString("Sign In")));
    }

    @Test
    public  void  accessDeniedTest() throws Exception {
        this.mockMvc.perform(get("/news"))
                .andDo(print())
                //говорим, что ужидаем статус 3хх - ошибка, неавторизованным пользователям нельзя сюда
                .andExpect(status().is3xxRedirection())
                //почему-то порт не указывается!
                .andExpect(redirectedUrl(String.format("http://%s/login", "localhost")));
    }

    @Test
    public void correctLoginTest() throws Exception {
        //this.mockMvc.perform(formLogin().user("dima").password("1"));
        this.mockMvc.perform(formLogin().user("admin").password("1"))
                    .andDo(print())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(status().is3xxRedirection());

    }

    @Test
    public void badCredentials() throws Exception {
        this.mockMvc.perform(formLogin().user("kek").password("1234"))
                .andExpect(status().is3xxRedirection());
        this.mockMvc.perform(post("/login").param("user", "kek"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
