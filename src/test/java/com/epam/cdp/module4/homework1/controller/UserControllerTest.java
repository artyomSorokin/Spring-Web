package com.epam.cdp.module4.homework1.controller;

import com.epam.cdp.module4.homework1.entity.UserEntity;
import com.epam.cdp.module4.homework1.facade.BookingFacade;
import com.epam.cdp.module4.homework1.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epam.cdp.module4.homework1.util.WebConstant.INFO_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.USERS_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.USER_TEMPLATE_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static final int USER_ID = 100;
    private static final String NAME = "Vasyl";
    private static final String NAME_UPDATE = "Nik";
    private static final String EMAIL = "vasyl@epam.com";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingFacade bookingFacade;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetUserById() throws Exception {
        User user = generateUser();
        when(bookingFacade.getUserById(USER_ID)).thenReturn(user);

        mockMvc.perform(
                get("/user/id/{userId}", USER_ID)
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user"));
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        User user = generateUser();
        when(bookingFacade.getUserByEmail(EMAIL)).thenReturn(user);

        mockMvc.perform(
                get("/user/email/{email}", EMAIL)
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user"));
    }

    @Test
    public void testGetUsersByName() throws Exception {
        List<User> users = generateUsers();
        when(bookingFacade.getUsersByName(NAME, Integer.MAX_VALUE, 1)).thenReturn(users);

        mockMvc.perform(
                get("/users/name/{name}", NAME)
                        .param("pageSize", String.valueOf(Integer.MAX_VALUE))
                        .param("pageNum", String.valueOf(1))
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(USERS_TEMPLATE_NAME));
    }

    @Test
    public void testCreateUser() throws Exception {
        Map<String, String> userParameters = generateUserParameters();
        User user = generateUser();
        when(bookingFacade.createUser(any())).thenReturn(user);

        mockMvc.perform(
                post("/user")
                        .content(objectMapper.writeValueAsString(userParameters))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.view().name(USER_TEMPLATE_NAME));
    }

    @Test
    public void testUpdateUser() throws Exception {
        Map<String, String> userParameters = generateUpdateUserParameters();
        User user = generateUser();
        when(bookingFacade.updateUser(any())).thenReturn(user);

        mockMvc.perform(
                put("/user")
                        .content(objectMapper.writeValueAsString(userParameters))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(USER_TEMPLATE_NAME));
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(bookingFacade.deleteUser(USER_ID)).thenReturn(true);

        mockMvc.perform(
                delete("/user/{userId}", USER_ID))
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.view().name(INFO_TEMPLATE_NAME));
    }

    private Map<String, String> generateUserParameters() {
        Map<String, String> userParameters = new HashMap<>();
        userParameters.put("id", String.valueOf(USER_ID));
        userParameters.put("name", NAME);
        userParameters.put("email", EMAIL);
        return userParameters;
    }

    private Map<String, String> generateUpdateUserParameters() {
        Map<String, String> userParameters = new HashMap<>();
        userParameters.put("id", String.valueOf(USER_ID));
        userParameters.put("name", NAME_UPDATE);
        userParameters.put("email", EMAIL);
        return userParameters;
    }

    private User generateUser() {
        User user = new UserEntity();
        user.setId(USER_ID);
        user.setName(NAME);
        user.setEmail(EMAIL);
        return user;
    }

    private List<User> generateUsers() {
        User user = generateUser();
        List<User> users = new ArrayList<>(2);
        users.add(user);
        users.add(user);
        return users;
    }
}
