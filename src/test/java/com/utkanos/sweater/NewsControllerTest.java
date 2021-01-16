package com.utkanos.sweater;

import com.utkanos.sweater.controllers.NewsController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//указываем окружение
@RunWith(SpringRunner.class)
//показываем что это за класс
@SpringBootTest
//подключаем мок объекты
@AutoConfigureMockMvc
//указываем файл с пропертями
@TestPropertySource("/application-test.properties")
//указываем авторизированного пользователя - передаем имя пользователя. Можно перед каждым тестом своего пользователя указывать
@WithUserDetails("admin")
//указываем, какой скрипт надо выполнить до и после каждого теста. Можно указывать над конкретным тестом
//здесь скрипты создания пользователей и сразу же создание постов от них. Порядок выполнения запросов важен!
@Sql(value = {"/newsControllerTest/create-user-before.sql", "/newsControllerTest/add-subs-before.sql", "/newsControllerTest/add-posts-before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/newsControllerTest/add-posts-after.sql", "/newsControllerTest/add-subs-after.sql", "/newsControllerTest/create-user-after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class NewsControllerTest {

    @Autowired
    private NewsController newsController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void newsPageTest() throws Exception {
        this.mockMvc.perform(get("/news"))
                .andDo(print())
                .andExpect(authenticated())
                //мы ожидаем, что в ответе на запрос придет файл xml-html в котором будет содержаться данная шлняга с именем admin
                .andExpect(xpath("//div[@id=\"navbarSupportedContent\"]/div").string("admin"));
    }

    @Test
    public void newsPageTestStupid() throws Exception {
        this.mockMvc.perform(get("/news"))
                .andDo(print())
                .andExpect(authenticated())
                //мы ожидаем, что в ответе на запрос придет файл xml-html в котором будет содержаться данная шлняга с именем admin
                .andExpect(content().string(containsString("admin")));
    }

    @Test
    //тест для авторизованных пользователей с подписками
    @WithUserDetails("admin")
    public void postListTest() throws Exception {
        this.mockMvc.perform(get("/news"))
                .andDo(print())
                .andExpect(authenticated())
                //ожидаем, что будет определенное число постов
                .andExpect(xpath("//*[@id=\"postCard\"]").nodeCount(4));
    }

    @Test
    //неактивный пользователь
    public void notActiveUsersPostsList() throws Exception {
        this.mockMvc.perform(formLogin("/news").user("alina").password("1"))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    //активный пользователь
    //вообще говоря, лучше не проверять логику спринга, потому что она скорее всего работает, а проверить ее сложно
    public void activeUsersPostsList() throws Exception {
        this.mockMvc.perform(formLogin("/news").user("dima").password("1"))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithUserDetails("dima")
    public  void userWithoutSubscriptions() throws Exception {
        this.mockMvc.perform(get("/news"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"posts_list\"]/div").nodeCount(0));
    }

    @Test
    //тест для фильтров
    @WithUserDetails("alina")
    public void postListFilterTest() throws Exception {
        this.mockMvc.perform(get("/news").param("textFilter", "text"))
                .andDo(print())
                .andExpect(authenticated())
                //ожидаем, что будет определенное число постов
                .andExpect(xpath("//*[@id=\"postCard\"]").nodeCount(2))
                .andExpect(xpath("//div[@id=\"postBody2\"]").exists());
        this.mockMvc.perform(get("/news").param("tagFilter", "my-tag"))
                .andDo(print())
                .andExpect(authenticated())
                //ожидаем, что будет определенное число постов
                .andExpect(xpath("//div[@id=\"postCard\"]").nodeCount(2))
                //ожидаем конкретные посты
                .andExpect(xpath("//div[@id=\"postBody1\"]").exists())
                .andExpect(xpath("//div[@id=\"postBody2\"]").exists());
    }

    @Test
    @WithUserDetails("admin")
    public void addPostToNews() throws Exception {
        MockMultipartHttpServletRequestBuilder multipart = multipart("/news");
        multipart.file("file", "123".getBytes());
        multipart.param("text", "some text");
        multipart.param("tag", "some tag");
        multipart.with(csrf());

        this.mockMvc.perform(multipart)
                .andDo(print())
                .andExpect(authenticated())
                //если бы у нас возвращалась страница, но у нас идет перенаправление
                .andExpect(xpath("//*[@id=\"postCard\"]").nodeCount(5))
                .andExpect(xpath("//*[@id=\"postBody10\"]").exists())
                .andExpect(xpath("//*[@id=\"postBody10\"]/h5").string("some tag"))
                .andExpect(xpath("//*[@id=\"postBody10\"]/p[2]").string("some text"));
    }

}
