package com.utkanos.sweater;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class RegistrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getRegPage() throws Exception{
        this.mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(content().string(containsString("Add new user")))
                .andExpect(xpath("/html/body/div[1]/form/div[3]/div").exists())
                .andExpect(status().isOk());
    }

    @Test
    public void tryToReg() throws Exception {
        //если возникает ошибка обрыва запроса, значит какие-то параметры не были переданы контроллеру
        this.mockMvc.perform(post("/registration").with(csrf()).param("g-recaptcha-response", "400"))
                .andDo(print())
                .andExpect(xpath("//*[@id=\"captcha_alert\"]").exists());
    }

}
