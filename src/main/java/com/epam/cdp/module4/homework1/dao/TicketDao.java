package com.epam.cdp.module4.homework1.dao;

import com.epam.cdp.module4.homework1.model.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class TicketDao {

    private static final String TICKET_PREFIX = "ticket:";
    private static final Predicate<Map.Entry<String, Object>> TICKET_PREDICATE =
            entry -> entry.getKey().startsWith(TICKET_PREFIX);

    private Map<String, Object> storage;
    private UserDao userDao;
    private EventDao eventDao;

    /**
     * Creates a new ticket. Id should be auto-generated.
     *
     * @param ticket Ticket data.
     * @return Created ticket object.
     */
    public Ticket createTicket(Ticket ticket) {
        isEventExists(ticket.getEventId());
        isUserExists(ticket.getUserId());
        isPlaceIsFree(ticket.getEventId(), ticket.getPlace());
        ticket.setId(ticket.hashCode());
        storage.put(TICKET_PREFIX + ticket.getId(), ticket);
        return ticket;
    }

    /**
     * Get all booked tickets for specified user id.
     *
     * @param userId   User's id
     * @param pageSize Pagination param. Number of tickets to return on a page.
     * @param pageNum  Pagination param. Number of the page to return. Starts from 1.
     * @return List of Ticket objects.
     */
    public List<Ticket> getBookedTicketsForUser(long userId, int pageSize, int pageNum) {
        return storage.entrySet().stream()
                .filter(TICKET_PREDICATE)
                .map(entry -> (Ticket) entry.getValue())
                .filter(ticket -> Objects.equals(userId, ticket.getUserId()))
                .skip((pageNum - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    /**
     * Get all booked tickets for specified event id.
     *
     * @param eventId  Event's id
     * @param pageSize Pagination param. Number of tickets to return on a page.
     * @param pageNum  Pagination param. Number of the page to return. Starts from 1.
     * @return List of Ticket objects.
     */
    public List<Ticket> getBookedTicketsForEvent(long eventId, int pageSize, int pageNum) {
        return storage.entrySet().stream()
                .filter(TICKET_PREDICATE)
                .map(entry -> (Ticket) entry.getValue())
                .filter(ticket -> Objects.equals(eventId, ticket.getEventId()))
                .skip((pageNum - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    /**
     * Cancel ticket with a specified id.
     *
     * @param ticketId Ticket id.
     * @return Flag whether anything has been canceled.
     */
    public boolean cancelTicket(long ticketId) {
        return storage.remove(TICKET_PREFIX + ticketId) != null;
    }

    private void isUserExists(long userId) {
        if (userDao.getUserById(userId) == null) {
            log.error("User with id={} doesn't exist", userId);
            throw new IllegalArgumentException(String.format("User with id=%d doesn't exist", userId));
        }
    }

    private void isEventExists(long eventId) {
        if (eventDao.getEventById(eventId) == null) {
            log.error("Event with id={} doesn't exist", eventId);
            throw new IllegalArgumentException(String.format("Event with id=%d doesn't exist", eventId));
        }
    }

    private void isPlaceIsFree(long eventId, int place) {
        storage.entrySet().stream()
                .filter(TICKET_PREDICATE)
                .map(entry -> (Ticket) entry.getValue())
                .filter(ticket -> Objects.equals(eventId, ticket.getEventId()) && Objects.equals(place, ticket.getPlace()))
                .findAny()
                .ifPresent(ticket -> {
                    log.error("Place {} already taken", place);
                    throw new IllegalStateException(String.format("Place %d already taken", place));
                });
    }

    @Required
    public void setStorage(Map<String, Object> storage) {
        this.storage = storage;
    }

    @Required
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Required
    public void setEventDao(EventDao eventDao) {
        this.eventDao = eventDao;
    }
}
