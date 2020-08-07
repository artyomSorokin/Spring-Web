package com.epam.cdp.module4.homework1.service.impl;

import com.epam.cdp.module4.homework1.dao.EventDao;
import com.epam.cdp.module4.homework1.model.Event;
import com.epam.cdp.module4.homework1.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;

import java.util.Date;
import java.util.List;

@Slf4j
public class EventServiceImpl implements EventService {

    private EventDao eventDao;

    @Override
    public Event createEvent(Event event) {
        Event createdEvent = eventDao.createEvent(event);
        log.debug("Event with id {} has been created", createdEvent.getId());
        return createdEvent;
    }

    @Override
    public Event getEventById(long eventId) {
        Event foundEvent = eventDao.getEventById(eventId);
        log.debug("Found event by id {} is {}", eventId, foundEvent);
        return foundEvent;
    }

    @Override
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        List<Event> foundEvents = eventDao.getEventsByTitle(title, pageSize, pageNum);
        log.debug("Found {} events by title '{}'", foundEvents.size(), title);
        return foundEvents;
    }

    @Override
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        List<Event> foundEvents = eventDao.getEventsForDay(day, pageSize, pageNum);
        log.debug("Found {} events for day {}", foundEvents.size(), day);
        return foundEvents;
    }

    @Override
    public Event updateEvent(Event event) {
        Event updatedEvent = eventDao.updateEvent(event);
        log.debug("Event with id {} has been updated", updatedEvent.getId());
        return updatedEvent;
    }

    @Override
    public boolean deleteEvent(long eventId) {
        boolean isDeleted = eventDao.deleteEvent(eventId);
        if (isDeleted) {
            log.debug("Event with id {} has been deleted", eventId);
        } else {
            log.debug("Could not delete event with id {}", eventId);
        }
        return isDeleted;
    }

    @Required
    public void setEventDao(EventDao eventDao) {
        this.eventDao = eventDao;
    }
}
