package com.utkanos.sweater;

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

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@Sql(value = {"/subsController/add_users_before.sql", "/subsController/add_subs_before.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/subsController/add_subs_after.sql", "/subsController/add_users_after.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class SubsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails("admin")
    public void getListOfSubscribers() throws Exception {
        this.mockMvc.perform(get("/subscribers").param("id", "1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"2\"]").exists())
                .andExpect(xpath("//div[@id=\"3\"]").exists())
                .andExpect(xpath("//div[@id=\"subContainer\"]").nodeCount(2));
    }

    @Test
    @WithUserDetails("alina")
    public void getListOfSubscribers2() throws Exception {
        this.mockMvc.perform(get("/subscribers").param("id", "2"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"1\"]").exists())
                .andExpect(xpath("//div[@id=\"3\"]").exists())
                .andExpect(xpath("//div[@id=\"4\"]").exists())
                .andExpect(xpath("//div[@id=\"5\"]").exists())
                .andExpect(xpath("//div[@id=\"subContainer\"]").nodeCount(4));
    }

    @Test
    @WithUserDetails("oleg")
    public void getListOfSubscribers3() throws Exception {
        this.mockMvc.perform(get("/subscribers").param("id", "4"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"1\"]").doesNotExist())
                .andExpect(xpath("//div[@id=\"subContainer\"]").nodeCount(0))
                .andExpect(content().string(containsString("No subscribers!")));
    }

    @Test
    @WithUserDetails("dima")
    public void getListOfSubscribers4() throws Exception {
        this.mockMvc.perform(get("/subscribers").param("id", "3"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"2\"]").exists())
                .andExpect(xpath("//div[@id=\"subContainer\"]").nodeCount(1));
    }

    @Test
    @WithUserDetails("admin")
    public void getListOfSubscriptions() throws Exception {
        this.mockMvc.perform(get("/subscriptions").param("id", "1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"2\"]").exists())
                .andExpect(xpath("//div[@id=\"subContainer\"]").nodeCount(1));
    }

    @Test
    @WithUserDetails("alina")
    public void getListOfSubscriptions2() throws Exception {
        this.mockMvc.perform(get("/subscriptions").param("id", "2"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"1\"]").exists())
                .andExpect(xpath("//div[@id=\"3\"]").exists())
                .andExpect(xpath("//div[@id=\"subContainer\"]").nodeCount(2));
    }

    @Test
    @WithUserDetails("alina")
    public void getListOfSubscriptions3() throws Exception {
        this.mockMvc.perform(get("/subscriptions").param("id", "3"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"1\"]").exists())
                .andExpect(xpath("//div[@id=\"2\"]").exists())
                .andExpect(xpath("//div[@id=\"subContainer\"]").nodeCount(2));
    }

    @Test
    @WithUserDetails("alina")
    public void getListOfSubscriptionWithoutParams() throws Exception {
        this.mockMvc.perform(get("/subscriptions"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"1\"]").exists())
                .andExpect(xpath("//div[@id=\"3\"]").exists())
                .andExpect(xpath("//div[@id=\"subContainer\"]").nodeCount(2));
    }

    @Test
    @WithUserDetails("admin")
    public void getListOfSubscribersWithoutParams() throws Exception {
        this.mockMvc.perform(get("/subscribers"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(xpath("//div[@id=\"2\"]").exists())
                .andExpect(xpath("//div[@id=\"3\"]").exists())
                .andExpect(xpath("//div[@id=\"subContainer\"]").nodeCount(2));
    }

}
