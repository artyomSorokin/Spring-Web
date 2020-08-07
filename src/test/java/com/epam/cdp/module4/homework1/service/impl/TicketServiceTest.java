package com.epam.cdp.module4.homework1.service.impl;

import com.epam.cdp.module4.homework1.dao.EventDao;
import com.epam.cdp.module4.homework1.dao.TicketDao;
import com.epam.cdp.module4.homework1.dao.UserDao;
import com.epam.cdp.module4.homework1.entity.EventEntity;
import com.epam.cdp.module4.homework1.entity.UserEntity;
import com.epam.cdp.module4.homework1.model.Event;
import com.epam.cdp.module4.homework1.model.Ticket;
import com.epam.cdp.module4.homework1.model.User;
import com.epam.cdp.module4.homework1.service.TicketService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TicketServiceTest.Config.class)
public class TicketServiceTest {

    private static final ConcurrentHashMap<String, Object> storage = new ConcurrentHashMap<>();
    private static final long USER_ID = 24;
    private static final String USER_EMAIL = "vasyl_koper@epam.com";
    private static final String USER_NAME = "vasyl";
    private static final long EVENT_ID = 27;
    private static final String EVENT_TITLE = "concert";
    private static final Date EVENT_DATE = new Date();

    private UserDao userDao;
    private EventDao eventDao;

    @Autowired
    private TicketService ticketService;

    @Before
    public void init() {
        userDao = new UserDao();
        userDao.setStorage(storage);
        eventDao = new EventDao();
        eventDao.setStorage(storage);
    }

    @Test
    public void testBookTicket() {
        User user = createUser();
        Event event = createEvent();
        Ticket ticket = ticketService.bookTicket(user.getId(), event.getId(), 21, Ticket.Category.BAR);
        assertEquals(ticket.getEventId(), event.getId());
        assertEquals(ticket.getUserId(), user.getId());
    }

    @Test
    public void testGetBookedTicketsForUser() {
        User user = createUser();
        Event event = createEvent();
        Ticket ticket = ticketService.bookTicket(user.getId(), event.getId(), 21, Ticket.Category.BAR);
        List<Ticket> bookedTickets = ticketService.getBookedTickets(user, 1, 1);
        assertEquals(ticket, bookedTickets.get(0));
    }

    @Test
    public void testGetBookedTicketsForEvent() {
        User user = createUser();
        Event event = createEvent();
        Ticket ticket = ticketService.bookTicket(user.getId(), event.getId(), 21, Ticket.Category.BAR);
        List<Ticket> bookedTickets = ticketService.getBookedTickets(event, 1, 1);
        assertEquals(ticket, bookedTickets.get(0));
    }

    @Test
    public void testCancelTicket() {
        User user = createUser();
        Event event = createEvent();
        Ticket ticket = ticketService.bookTicket(user.getId(), event.getId(), 21, Ticket.Category.BAR);
        boolean isTicketCancel = ticketService.cancelTicket(ticket.getId());
        assertTrue(isTicketCancel);
        List<Ticket> bookedTickets = ticketService.getBookedTickets(event, 1, 1);
        assertTrue(bookedTickets.size() == 0);
    }

    private User createUser() {
        User user = new UserEntity();
        user.setId(USER_ID);
        user.setEmail(USER_EMAIL);
        user.setName(USER_NAME);
        return userDao.createUser(user);
    }

    private Event createEvent() {
        Event event = new EventEntity();
        event.setId(EVENT_ID);
        event.setTitle(EVENT_TITLE);
        event.setDate(EVENT_DATE);
        return eventDao.createEvent(event);
    }

    @Configuration
    static class Config {

        @Bean
        TicketServiceImpl ticketService() {
            TicketServiceImpl ticketService = new TicketServiceImpl();
            ticketService.setTicketDao(createTicketDao());
            return ticketService;
        }

        private TicketDao createTicketDao() {
            UserDao userDao = new UserDao();
            userDao.setStorage(storage);
            EventDao eventDao = new EventDao();
            eventDao.setStorage(storage);
            TicketDao ticketDao = new TicketDao();
            ticketDao.setStorage(storage);
            ticketDao.setEventDao(eventDao);
            ticketDao.setUserDao(userDao);
            return ticketDao;
        }
    }
}
