package com.epam.cdp.module4.homework1.service.impl;

import com.epam.cdp.module4.homework1.dao.TicketDao;
import com.epam.cdp.module4.homework1.entity.TicketEntity;
import com.epam.cdp.module4.homework1.model.Event;
import com.epam.cdp.module4.homework1.model.Ticket;
import com.epam.cdp.module4.homework1.model.User;
import com.epam.cdp.module4.homework1.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

@Slf4j
public class TicketServiceImpl implements TicketService {

    private TicketDao ticketDao;

    @Override
    public Ticket bookTicket(long userId, long eventId, int place, Ticket.Category category) {
        Ticket ticket = new TicketEntity();
        ticket.setUserId(userId);
        ticket.setEventId(eventId);
        ticket.setPlace(place);
        ticket.setCategory(category);

        Ticket bookedTicket = ticketDao.createTicket(ticket);
        log.debug("Ticket with id '{}' has been booked", bookedTicket.getId());
        return bookedTicket;
    }

    @Override
    public List<Ticket> getBookedTickets(User user, int pageSize, int pageNum) {
        List<Ticket> foundTickets = ticketDao.getBookedTicketsForUser(user.getId(), pageSize, pageNum);
        log.debug("Found {} tickets for user '{}'", foundTickets.size(), user.getId());
        return foundTickets;
    }

    @Override
    public List<Ticket> getBookedTickets(Event event, int pageSize, int pageNum) {
        List<Ticket> foundTickets = ticketDao.getBookedTicketsForEvent(event.getId(), pageSize, pageNum);
        log.debug("Found {} tickets for event '{}'", foundTickets.size(), event.getId());
        return foundTickets;
    }

    @Override
    public boolean cancelTicket(long ticketId) {
        boolean isCancelled = ticketDao.cancelTicket(ticketId);
        if (isCancelled) {
            log.debug("Ticket with id '{}' has been cancelled", ticketId);
        } else {
            log.debug("Could not cancel ticket with id '{}'", ticketId);
        }
        return isCancelled;
    }

    @Required
    public void setTicketDao(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }
}
