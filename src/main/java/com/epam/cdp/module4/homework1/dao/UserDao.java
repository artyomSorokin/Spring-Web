package com.epam.cdp.module4.homework1.dao;


import com.epam.cdp.module4.homework1.model.Ticket;
import com.epam.cdp.module4.homework1.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class UserDao {

    private static final String USER_PREFIX = "user:";

    private static final Predicate<Map.Entry<String, Object>> USER_PREDICATE =
            entry -> entry.getKey().startsWith(USER_PREFIX);

    private Map<String, Object> storage;

    private TicketDao ticketDao;

    /**
     * Gets user by its id.
     *
     * @param userId User's id.
     * @return User.
     */
    public User getUserById(long userId) {
        return (User) storage.get(USER_PREFIX + userId);
    }

    /**
     * Gets user by its email. Email is strictly matched.
     *
     * @param email User's email
     * @return User.
     */
    public User getUserByEmail(String email) {
        return storage.entrySet().stream()
                .filter(entry -> USER_PREDICATE.test(entry) &&
                        Objects.equals((((User) entry.getValue()).getEmail()), email))
                .findFirst()
                .map(entry -> (User) entry.getValue())
                .orElse(null);
    }

    /**
     * Get list of users by matching name. Name is matched using 'contains' approach.
     * In case nothing was found, empty list is returned.
     *
     * @param name     Users name or it's part.
     * @param pageSize Pagination param. Number of users to return on a page.
     * @param pageNum  Pagination param. Number of the page to return. Starts from 1.
     * @return List of users.
     */
    public List<User> getUsersByName(String name, int pageSize, int pageNum) {
        return storage.entrySet().stream()
                .filter(USER_PREDICATE)
                .map(entry -> (User) entry.getValue())
                .filter(user -> Objects.nonNull(user.getName()) && user.getName().contains(name))
                .skip(pageSize * (pageNum - 1))
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    /**
     * Creates new user. User id should be auto-generated.
     *
     * @param user User data.
     * @return Created User object.
     */
    public User createUser(User user) {
        user.setId(user.hashCode());
        storage.put(USER_PREFIX + user.getId(), user);
        return user;
    }

    /**
     * Updates user using given data.
     *
     * @param user User data for update. Should have id set.
     * @return Updated User object.
     */
    public User updateUser(User user) {
        User oldUser = (User) storage.get(USER_PREFIX + user.getId());
        if (oldUser != null) {
            oldUser.setEmail(user.getEmail());
            oldUser.setName(user.getName());
            return oldUser;
        }
        log.error("User with id={} doesn't exist", user.getId());
        return null;
    }

    /**
     * Deletes user by its id.
     *
     * @param userId User id.
     * @return Flag that shows whether user has been deleted.
     */
    public boolean deleteUser(long userId) {
        List<Ticket> ticketsForUser = ticketDao.getBookedTicketsForUser(userId, Integer.MAX_VALUE, 1);
        ticketsForUser.forEach(ticket -> ticketDao.cancelTicket(ticket.getId()));
        return storage.remove(USER_PREFIX + userId) != null;
    }

    @Required
    public void setStorage(Map<String, Object> storage) {
        this.storage = storage;
    }

    @Required
    public void setTicketDao(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }
}
