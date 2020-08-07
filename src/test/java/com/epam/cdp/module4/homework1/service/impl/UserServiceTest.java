package com.epam.cdp.module4.homework1.service.impl;

import com.epam.cdp.module4.homework1.dao.UserDao;
import com.epam.cdp.module4.homework1.entity.UserEntity;
import com.epam.cdp.module4.homework1.model.User;
import com.epam.cdp.module4.homework1.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceTest.Config.class)
public class UserServiceTest {

    private static final long USER_ID = 24;
    private static final String USER_EMAIL = "vasyl_koper@epam.com";
    private static final String USER_NAME = "vasyl";


    @Autowired
    private UserService userService;

    @Test
    public void testGetUserById() {
        User createdUser = createUser();
        User userById = userService.getUserById(createdUser.getId());
        assertEquals(createdUser, userById);
    }

    @Test
    public void testGetUserByEmail() {
        User createdUser = createUser();
        User userById = userService.getUserByEmail(createdUser.getEmail());
        assertEquals(createdUser, userById);
    }

    @Test
    public void testGetUsersByName() {
        User createdUser = createUser();
        List<User> usersByName = userService.getUsersByName(createdUser.getName(), 1, 1);
        assertEquals(createdUser.getName(), usersByName.get(0).getName());
    }

    @Test
    public void testUpdateUser() {
        User createdUser = createUser();
        createdUser.setName("Ivan");
        userService.updateUser(createdUser);
        List<User> usersByName = userService.getUsersByName(createdUser.getName(), 1, 1);
        assertEquals(createdUser, usersByName.get(0));
    }

    private User createUser() {
        User user = new UserEntity();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setName(USER_NAME);
        return userService.createUser(user);
    }

    @Configuration
    static class Config {

        @Bean
        UserServiceImpl userService() {
            UserServiceImpl userService = new UserServiceImpl();
            UserDao userDao = new UserDao();
            userDao.setStorage(new ConcurrentHashMap<>());
            userService.setUserDao(userDao);
            return userService;
        }
    }


}
