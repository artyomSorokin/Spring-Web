package com.epam.cdp.module4.homework1.dao;

import com.epam.cdp.module4.homework1.model.Event;
import com.epam.cdp.module4.homework1.model.Ticket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class EventDao {

    private static final String EVENT_PREFIX = "event:";

    private static final Predicate<Map.Entry<String, Object>> EVENT_PREDICATE =
            entry -> entry.getKey().startsWith(EVENT_PREFIX);

    private Map<String, Object> storage;

    private TicketDao ticketDao;

    /**
     * Gets event by its id.
     *
     * @param eventId Event's id
     * @return Event.
     */
    public Event getEventById(long eventId) {
        return (Event) storage.get(EVENT_PREFIX + eventId);
    }

    /**
     * Get list of events by matching title. Title is matched using 'contains' approach.
     * In case nothing was found, empty list is returned.
     *
     * @param title    Event title or it's part.
     * @param pageSize Pagination param. Number of events to return on a page.
     * @param pageNum  Pagination param. Number of the page to return. Starts from 1.
     * @return List of events.
     */
    public List<Event> getEventsByTitle(String title, int pageSize, int pageNum) {
        return storage.entrySet().stream()
                .filter(EVENT_PREDICATE)
                .map(entry -> (Event) entry.getValue())
                .filter(event -> Objects.nonNull(event.getTitle()) && event.getTitle().contains(title))
                .skip((pageNum - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    /**
     * Get list of events for specified day.
     * In case nothing was found, empty list is returned.
     *
     * @param day      Date object from which day information is extracted.
     * @param pageSize Pagination param. Number of events to return on a page.
     * @param pageNum  Pagination param. Number of the page to return. Starts from 1.
     * @return List of events.
     */
    public List<Event> getEventsForDay(Date day, int pageSize, int pageNum) {
        return storage.entrySet().stream()
                .filter(EVENT_PREDICATE)
                .map(entry -> (Event) entry.getValue())
                .filter(event -> Objects.equals(day, event.getDate()))
                .skip((pageNum - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    /**
     * Creates new event. Event id should be auto-generated.
     *
     * @param event Event data.
     * @return Created Event object.
     */
    public Event createEvent(Event event) {
        event.setId(event.hashCode());
        storage.put(EVENT_PREFIX + event.getId(), event);
        return event;
    }

    /**
     * Updates event using given data.
     *
     * @param event Event data for update. Should have id set.
     * @return Updated Event object.
     */
    public Event updateEvent(Event event) {
        Event oldEvent = (Event) storage.get(EVENT_PREFIX + event.getId());
        if (oldEvent != null) {
            oldEvent.setDate(event.getDate());
            oldEvent.setTitle(event.getTitle());
            return oldEvent;
        }
        log.error("Event wit id={} doesn't exist", event.getId());
        return null;
    }

    /**
     * Deletes event by its id.
     *
     * @param eventId Event id.
     * @return Flag that shows whether event has been deleted.
     */
    public boolean deleteEvent(long eventId) {
        List<Ticket> ticketsForEvent = ticketDao.getBookedTicketsForEvent(eventId, Integer.MAX_VALUE, 1);
        ticketsForEvent.forEach(ticket -> ticketDao.cancelTicket(ticket.getId()));
        return storage.remove(EVENT_PREFIX + eventId) != null;
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
