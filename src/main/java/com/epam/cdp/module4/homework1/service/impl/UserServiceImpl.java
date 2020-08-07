package com.epam.cdp.module4.homework1.service.impl;

import com.epam.cdp.module4.homework1.dao.UserDao;
import com.epam.cdp.module4.homework1.model.User;
import com.epam.cdp.module4.homework1.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

@Slf4j
public class UserServiceImpl implements UserService {

    private UserDao userDao;

    @Override
    public User getUserById(long userId) {
        User foundUser = userDao.getUserById(userId);
        log.debug("Found user by id '{}' is '{}'", userId, foundUser);
        return foundUser;
    }

    @Override
    public User getUserByEmail(String email) {
        User foundUser = userDao.getUserByEmail(email);
        log.debug("Found user by email '{}' is '{}'", email, foundUser);
        return foundUser;
    }

    @Override
    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        List<User> foundUsers = userDao.getUsersByName(name, pageSize, pageNum);
        log.debug("Found {} users by name '{}'", foundUsers.size(), name);
        return foundUsers;
    }

    @Override
    public User createUser(User user) {
        User createdUser = userDao.createUser(user);
        log.debug("User with id '{}' has been created", createdUser.getId());
        return createdUser;
    }

    @Override
    public User updateUser(User user) {
        User updatedUser = userDao.updateUser(user);

        if (updatedUser == null) {
            log.debug("Could not update user with id '{}'", updatedUser.getId());
        }
        log.debug("User with id '{}' has been updated", updatedUser.getId());
        return updatedUser;
    }

    @Override
    public boolean deleteUser(long userId) {
        boolean isDeleted = userDao.deleteUser(userId);
        if (isDeleted) {
            log.debug("User with id '{}' has been deleted", userId);
        } else {
            log.debug("Could not delete user with id '{}'", userId);
        }

        return isDeleted;
    }

    @Required
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
