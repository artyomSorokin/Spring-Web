package com.epam.cdp.module4.homework1;


import com.epam.cdp.module4.homework1.entity.EventEntity;
import com.epam.cdp.module4.homework1.entity.UserEntity;
import com.epam.cdp.module4.homework1.facade.BookingFacade;
import com.epam.cdp.module4.homework1.model.Event;
import com.epam.cdp.module4.homework1.model.Ticket;
import com.epam.cdp.module4.homework1.model.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-context.xml"})
public class BookingFacadeIntegrationTest {

    private static final String DATE_PATTERN = "MM-dd-yyyy";
    private static final String DATE = "10-09-2019";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private ApplicationContext applicationContext;

    private BookingFacade bookingFacade;

    private Event event;
    private User user;

    private List<Event> events;
    private List<User> users;

    @Before
    public void setUp() {
        bookingFacade = (BookingFacade) applicationContext.getBean("bookingFacade");
    }

    @Test
    public void testGetEventById() {
        Event event = bookingFacade.getEventById(1);
        assertNotNull(event);
        assertEquals(1, event.getId());
    }

    @Test
    public void testGetEventsByTitle() {
        events = bookingFacade.getEventsByTitle("concert", 10, 1);
        assertEquals(3, events.size());
    }

    @Test
    public void testGetEventsByTitleShouldReturnEmptyList() {
        events = bookingFacade.getEventsByTitle("Led zeppelin", 10, 1);
        assertTrue(events.isEmpty());
    }

    @Test
    public void testGetEventsByTitleWhenPageSizeIsTwo() {
        events = bookingFacade.getEventsByTitle("concert", 2, 1);
        assertEquals(2, events.size());
    }

    @Test
    public void testGetEventsByTitleShouldReturnEmptyListWhenPageNumIsTwo() {
        events = bookingFacade.getEventsByTitle("concert", 10, 2);
        assertTrue(events.isEmpty());
    }

    @Test
    public void testGetEventsForDay() throws ParseException {
        events = bookingFacade.getEventsForDay(
                new SimpleDateFormat(DATE_PATTERN).parse(DATE), 10, 1);
        assertEquals(1, events.size());
    }

    @Test
    public void testGetEventsForDayShouldReturnEmptyList() throws ParseException {
        events = bookingFacade.getEventsForDay(
                new SimpleDateFormat(DATE_PATTERN).parse("11-05-2019"), 10, 1);
        assertTrue(events.isEmpty());
    }

    @Test
    public void testGetEventsForDayShouldReturnEmptyListWhenPageNumIsTwo() throws ParseException {
        events = bookingFacade.getEventsForDay(new SimpleDateFormat(DATE_PATTERN).parse(DATE), 1, 2);
        assertTrue(events.isEmpty());
    }

    @Test
    public void testCreateAnEvent() {
        event = new EventEntity();
        event.setTitle("Crazy party");
        event.setDate(new Date());

        int oldEventCount = bookingFacade.getEventsByTitle("Crazy", Integer.MAX_VALUE, 1).size();
        long createdEventId = bookingFacade.createEvent(event).getId();
        long newEventCount = bookingFacade.getEventsByTitle("Crazy", Integer.MAX_VALUE, 1).size();

        assertNotEquals(0, createdEventId);
        assertNotNull(bookingFacade.getEventById(createdEventId));
        assertEquals(oldEventCount + 1, newEventCount);

        bookingFacade.deleteEvent(createdEventId);
    }

    @Test
    public void testUpdateAnEvent() {
        Event oldEvent = bookingFacade.getEventById(1);
        String oldTitle = oldEvent.getTitle();
        String newTitle = "Booomb";
        oldEvent.setTitle(newTitle);
        Event updatedEvent = bookingFacade.updateEvent(oldEvent);

        assertNotEquals(oldTitle, newTitle);
        assertNotNull(updatedEvent);
        assertEquals(newTitle, updatedEvent.getTitle());
        assertEquals(newTitle, bookingFacade.getEventById(oldEvent.getId()).getTitle());

        oldEvent.setTitle(oldTitle);
        bookingFacade.updateEvent(oldEvent);
    }

    @Test
    public void testDeleteAnEvent() {
        event = new EventEntity();
        event.setTitle("new concert");
        event.setDate(new Date());

        long createdEventId = bookingFacade.createEvent(event).getId();

        assertTrue(bookingFacade.deleteEvent(createdEventId));
        assertNull(bookingFacade.getEventById(createdEventId));
    }

    @Test
    public void testGetUserById() {
        user = bookingFacade.getUserById(1);
        assertNotNull(user);
        assertEquals(1, user.getId());
    }

    @Test
    public void testGetUserByEmail() {
        user = bookingFacade.getUserByEmail("vasyl_sokolov@epam.com");
        assertNotNull(user);
        assertEquals("vasyl_sokolov@epam.com", user.getEmail());
    }

    @Test
    public void testGetUsersByName() {
        users = bookingFacade.getUsersByName("Vasyl", 10, 1);
        assertEquals(1, users.size());
    }

    @Test
    public void testGetUsersByNameShouldReturnEmptyList() {
        users = bookingFacade.getUsersByName("Andrew", 10, 1);
        assertTrue(users.isEmpty());
    }

    @Test
    public void testGetUsersByNameShouldReturnEmptyListWhenPageNumIsTwo() {
        users = bookingFacade.getUsersByName("Vasyl", 10, 2);
        assertTrue(users.isEmpty());
    }

    @Test
    public void testCreateUser() {
        user = new UserEntity();
        user.setEmail("nikolay@epam.com");
        user.setName("Nikolay");

        int oldUserCount = bookingFacade.getUsersByName("Ni", Integer.MAX_VALUE, 1).size();
        long createdUserId = bookingFacade.createUser(user).getId();
        long newUserCount = bookingFacade.getUsersByName("Ni", Integer.MAX_VALUE, 1).size();

        assertNotEquals(0, createdUserId);
        assertNotNull(bookingFacade.getUserById(createdUserId));
        assertEquals(oldUserCount + 1, newUserCount);

        bookingFacade.deleteUser(createdUserId);
    }

    @Test
    public void testUpdateUser() {
        User oldUser = bookingFacade.getUserById(2);
        String oldName = oldUser.getName();
        String newName = "Gena";
        oldUser.setName(newName);
        User updatedUser = bookingFacade.updateUser(oldUser);

        assertNotEquals(oldName, newName);
        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(newName, bookingFacade.getUserById(oldUser.getId()).getName());

        oldUser.setName(oldName);
        bookingFacade.updateUser(oldUser);
    }

    @Test
    public void testDeleteUserById() {
        user = new UserEntity();
        user.setName("Anatoliy");
        user.setEmail("anatoliy@epam.com");

        long createdUserId = bookingFacade.createUser(user).getId();

        assertTrue(bookingFacade.deleteUser(createdUserId));
        assertNull(bookingFacade.getUserById(createdUserId));
    }

    @Test
    public void testBookTicket() {
        user = new UserEntity();
        user.setId(1);
        event = new EventEntity();
        event.setId(1);

        long oldTicketCountForUser = bookingFacade.getBookedTickets(user, Integer.MAX_VALUE, 1).size();
        long oldTicketCountForEvent = bookingFacade.getBookedTickets(event, Integer.MAX_VALUE, 1).size();
        Ticket bookedTicket = bookingFacade.bookTicket(user.getId(), event.getId(), 753, Ticket.Category.BAR);
        long newTicketCountForUser = bookingFacade.getBookedTickets(user, Integer.MAX_VALUE, 1).size();
        long newTicketCountForEvent = bookingFacade.getBookedTickets(event, Integer.MAX_VALUE, 1).size();

        assertNotNull(bookedTicket);
        assertNotEquals(0, bookedTicket.getId());
        assertEquals(oldTicketCountForEvent + 1, newTicketCountForEvent);
        assertEquals(oldTicketCountForUser + 1, newTicketCountForUser);

        bookingFacade.cancelTicket(bookedTicket.getId());
    }

    @Test
    public void testGetBookedTicketsForEvent() {
        event = new EventEntity();
        event.setId(1);

        List<Ticket> ticketsForEvent = bookingFacade.getBookedTickets(event, Integer.MAX_VALUE, 1);

        assertEquals(2, ticketsForEvent.size());
        assertEquals(1, ticketsForEvent.get(0).getId());
        assertEquals(3, ticketsForEvent.get(1).getId());
    }

    @Test
    public void testGetBookedTicketsForUser() {
        user = new UserEntity();
        user.setId(2);

        List<Ticket> ticketsForUser = bookingFacade.getBookedTickets(user, Integer.MAX_VALUE, 1);

        assertEquals(2, ticketsForUser.size());
        assertEquals(3, ticketsForUser.get(0).getId());
        assertEquals(2, ticketsForUser.get(1).getId());
    }

    @Test
    public void testCancelTicket() {
        user = new UserEntity();
        user.setId(3);
        event = new EventEntity();
        event.setId(1);

        List<Ticket> oldTicketsForUser = bookingFacade.getBookedTickets(user, Integer.MAX_VALUE, 1);
        Ticket bookedTicket = bookingFacade.bookTicket(user.getId(), event.getId(), 753, Ticket.Category.BAR);
        List<Ticket> newTicketsForUser = bookingFacade.getBookedTickets(user, Integer.MAX_VALUE, 1);


        assertNotNull(bookedTicket);
        assertNotEquals(0, bookedTicket.getId());
        assertTrue(oldTicketsForUser.isEmpty());
        assertEquals(1, newTicketsForUser.size());
        assertTrue(bookingFacade.cancelTicket(bookedTicket.getId()));
        assertTrue(bookingFacade.getBookedTickets(user, Integer.MAX_VALUE, 1).isEmpty());
    }

    @Test
    public void testRealLifeScenario() throws ParseException {
        user = new UserEntity();
        user.setName("Volodimir");
        user.setEmail("volodimir@epam.com");
        event = new EventEntity();
        event.setTitle("Ocean party");
        event.setDate(new SimpleDateFormat(DATE_PATTERN).parse("11-12-2019"));

        Event createdEvent = bookingFacade.createEvent(event);
        User createdUser = bookingFacade.createUser(user);
        Ticket bookedTicket = bookingFacade.bookTicket(createdUser.getId(), createdEvent.getId(), 500, Ticket.Category.PREMIUM);

        List<Ticket> ticketsForUser = bookingFacade.getBookedTickets(user, Integer.MAX_VALUE, 1);
        List<Ticket> ticketsForEvent = bookingFacade.getBookedTickets(event, Integer.MAX_VALUE, 1);

        assertNotNull(createdEvent);
        assertNotNull(createdUser);
        assertNotNull(bookedTicket);
        assertNotEquals(0, createdEvent.getId());
        assertNotEquals(0, createdUser.getId());
        assertNotEquals(0, bookedTicket.getId());
        assertEquals(1, ticketsForUser.size());
        assertEquals(1, ticketsForEvent.size());
        assertTrue(bookingFacade.deleteEvent(createdEvent.getId()));
        assertTrue(bookingFacade.deleteUser(createdUser.getId()));
        assertTrue(bookingFacade.getBookedTickets(user, Integer.MAX_VALUE, 1).isEmpty());
        assertTrue(bookingFacade.getBookedTickets(event, Integer.MAX_VALUE, 1).isEmpty());
    }

    @Test
    public void testBookTicketShouldThrowIllegalStateExWhenPlaceIsBooked() {
        expectedException.expect(IllegalStateException.class);
        bookingFacade.bookTicket(3, 1, 1, Ticket.Category.STANDARD);
    }

    @Test
    public void testBookTicketShouldThrowIllegalArgExWhenUserDoesNotExist() {
        expectedException.expect(IllegalArgumentException.class);
        bookingFacade.bookTicket(1234, 1, 44, Ticket.Category.STANDARD);
    }

    @Test
    public void testBookTicketShouldThrowIllegalArgExWhenEventDoesNotExist() {
        expectedException.expect(IllegalArgumentException.class);
        bookingFacade.bookTicket(1, 1234, 44, Ticket.Category.STANDARD);
    }
}
