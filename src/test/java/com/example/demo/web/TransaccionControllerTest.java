package com.example.demo.web;


import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.example.demo.Demo;
import com.example.demo.models.CuentaModel;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Demo.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransaccionControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;


    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

    }

    @Test
    public void createOneAccount() throws Exception {
        List<CuentaModel> list = new ArrayList<CuentaModel>();
        CuentaModel acc = new CuentaModel();
        acc.setId(1);
        acc.setActive_card(true);
        acc.setAvailable_limit(100);
        list.add(acc);
        String data = "[{\"account\":{\"id\":1,\"active-card\":true,\"available-limit\":100}}]";
        
        mockMvc.perform(MockMvcRequestBuilders.post("/transaccion")
            .content(data)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$[0].account.id").value("1"))
            .andExpect(jsonPath("$[0].account.active_card").value("true"))
            .andExpect(jsonPath("$[0].account.available_limit").value("100"))
            .andDo(print());
    }

    @Test
    public void createOneAccountAndTransaccion() throws Exception {
        List<CuentaModel> list = new ArrayList<CuentaModel>();
        CuentaModel acc = new CuentaModel();
        acc.setId(1);
        acc.setActive_card(true);
        acc.setAvailable_limit(100);
        list.add(acc);
        String data = "[{\"account\":{\"id\":1,\"active-card\":true,\"available-limit\":100}},{\"transaction\": {\"id\": 1, \"merchant\": \"Burger King\", \"amount\": 20, \"time\":\"2019-02-13T10:00:00.000Z\"}}]";
        
        mockMvc.perform(MockMvcRequestBuilders.post("/transaccion")
            .content(data)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$[0].account.id").value("1"))
            .andExpect(jsonPath("$[0].account.active_card").value("true"))
            .andExpect(jsonPath("$[0].account.available_limit").value("100"))
            .andExpect(jsonPath("$[1].account.id").value("1"))
            .andExpect(jsonPath("$[1].account.active_card").value("true"))
            .andExpect(jsonPath("$[1].account.available_limit").value("80"))
            .andDo(print());
    }

    @Test
    public void createRepeatAccount() throws Exception {
        List<CuentaModel> list = new ArrayList<CuentaModel>();
        CuentaModel acc = new CuentaModel();
        acc.setId(1);
        acc.setActive_card(true);
        acc.setAvailable_limit(100);
        list.add(acc);
        String data = "[{\"account\":{\"id\":1,\"active-card\":true,\"available-limit\":100}},{\"account\":{\"id\":1,\"active-card\":true,\"available-limit\":100}}]";
        
        mockMvc.perform(MockMvcRequestBuilders.post("/transaccion")
            .content(data)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$[0].violations", hasSize(0)))
            .andExpect(jsonPath("$[1].violations", hasSize(1)))
            .andExpect(jsonPath("$[1].violations[0]").value("account-already-initialized"))
            .andDo(print());
    }
}
