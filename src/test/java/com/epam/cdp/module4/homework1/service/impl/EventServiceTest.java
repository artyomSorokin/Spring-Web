package com.epam.cdp.module4.homework1.service.impl;

import com.epam.cdp.module4.homework1.dao.EventDao;
import com.epam.cdp.module4.homework1.dao.TicketDao;
import com.epam.cdp.module4.homework1.entity.EventEntity;
import com.epam.cdp.module4.homework1.model.Event;
import com.epam.cdp.module4.homework1.service.EventService;
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
@SpringBootTest(classes = EventServiceTest.Config.class)
public class EventServiceTest {

    private static final long EVENT_ID = 27;
    private static final String EVENT_TITLE = "concert";
    private static final Date EVENT_DATE = new Date();

    @Autowired
    private EventService eventService;

    @Test
    public void testCreateEvent() {
        Event event = new EventEntity();
        event.setId(EVENT_ID);
        event.setTitle(EVENT_TITLE);
        event.setDate(EVENT_DATE);

        Event createdEvent = eventService.createEvent(event);
        assertEquals(event, createdEvent);
    }

    @Test
    public void testGetEventById() {
        Event event = createEvent();
        Event eventById = eventService.getEventById(event.getId());
        assertEquals(event, eventById);
    }

    @Test
    public void testGetEventsByTitle() {
        Event event = createEvent();
        List<Event> eventsByTitle = eventService.getEventsByTitle(event.getTitle(), 1, 1);
        assertEquals(event.getTitle(), eventsByTitle.get(0).getTitle());
    }

    @Test
    public void testGetEventsForDay() {
        Event event = createEvent();
        List<Event> eventsByTitle = eventService.getEventsForDay(event.getDate(), 1, 1);
        assertEquals(event.getDate(), eventsByTitle.get(0).getDate());
    }

    @Test
    public void testUpdateEvent() {
        Event event = createEvent();
        event.setTitle("Party");
        Event updatedEvent = eventService.updateEvent(event);
        assertEquals(event, updatedEvent);
    }

    @Test
    public void testDeleteEvent() {
        Event event = createEvent();
        eventService.deleteEvent(event.getId());
        List<Event> eventsByTitle = eventService.getEventsForDay(event.getDate(), 1, 1);
        assertTrue(eventsByTitle.size() == 0);
    }

    private Event createEvent() {
        Event event = new EventEntity();
        event.setId(EVENT_ID);
        event.setTitle(EVENT_TITLE);
        event.setDate(EVENT_DATE);
        return eventService.createEvent(event);
    }

    @Configuration
    static class Config {

        @Bean
        EventService ticketService() {
            EventServiceImpl eventService = new EventServiceImpl();
            eventService.setEventDao(createEventDao());
            return eventService;
        }

        private EventDao createEventDao() {
            ConcurrentHashMap<String, Object> storage = new ConcurrentHashMap<>();
            TicketDao ticketDao = new TicketDao();
            ticketDao.setStorage(storage);
            EventDao eventDao = new EventDao();
            eventDao.setStorage(storage);
            eventDao.setTicketDao(ticketDao);
            return eventDao;
        }
    }
}
