package com.epam.cdp.module4.homework1.controller;

import com.epam.cdp.module4.homework1.entity.TicketEntity;
import com.epam.cdp.module4.homework1.facade.BookingFacade;
import com.epam.cdp.module4.homework1.model.Event;
import com.epam.cdp.module4.homework1.model.Ticket;
import com.epam.cdp.module4.homework1.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epam.cdp.module4.homework1.util.WebConstant.INFO_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.TICKETS_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.TICKET_PDF_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.TICKET_TEMPLATE_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    private static final int EVENT_ID = 100;
    private static final int TICKET_ID = 100;
    private static final int USER_ID = 100;
    private static final int PLACE = 100;
    private static final String CATEGORY = "BAR";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingFacade bookingFacade;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testBookTicket() throws Exception {
        Map<String, String> ticketParameters = generateTicketParameters();
        Ticket ticket = generateTicket();
        when(bookingFacade.bookTicket(USER_ID, EVENT_ID, PLACE, Ticket.Category.valueOf(CATEGORY))).thenReturn(ticket);

        mockMvc.perform(
                post("/ticket")
                        .content(objectMapper.writeValueAsString(ticketParameters))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.view().name(TICKET_TEMPLATE_NAME));
    }

    @Test
    public void testGetTicketsByUserId() throws Exception {
        List<Ticket> tickets = generateTickets();
        when(bookingFacade.getBookedTickets(any(User.class), anyInt(), anyInt())).thenReturn(tickets);
        mockMvc.perform(
                get("/ticket/user/{userId}", USER_ID)
                        .param("pageSize", String.valueOf(Integer.MAX_VALUE))
                        .param("pageNum", String.valueOf(1))
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(TICKETS_TEMPLATE_NAME));
    }

    @Test
    public void testGetTicketsAsPdfByUserId() throws Exception {
        List<Ticket> tickets = generateTickets();
        when(bookingFacade.getBookedTickets(any(User.class), anyInt(), anyInt())).thenReturn(tickets);

        mockMvc.perform(
                get("/ticket/user/{userId}", USER_ID)
                        .param("pageSize", String.valueOf(Integer.MAX_VALUE))
                        .param("pageNum", String.valueOf(1))
                        .accept(MediaType.APPLICATION_PDF))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(TICKET_PDF_TEMPLATE_NAME));
    }

    @Test
    public void testGetTicketsByEventId() throws Exception {
        List<Ticket> tickets = generateTickets();
        when(bookingFacade.getBookedTickets(any(Event.class), anyInt(), anyInt())).thenReturn(tickets);

        mockMvc.perform(
                get("/ticket/event/{eventId}", EVENT_ID)
                        .param("pageSize", String.valueOf(Integer.MAX_VALUE))
                        .param("pageNum", String.valueOf(1))
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(TICKETS_TEMPLATE_NAME));
    }

    @Test
    public void testCancelTicket() throws Exception {
        when(bookingFacade.cancelTicket(TICKET_ID)).thenReturn(true);

        mockMvc.perform(
                delete("/ticket/{ticketId}", TICKET_ID))
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.view().name(INFO_TEMPLATE_NAME));
    }

    private Map<String, String> generateTicketParameters() {
        Map<String, String> ticketParameters = new HashMap<>();
        ticketParameters.put("eventId", String.valueOf(EVENT_ID));
        ticketParameters.put("userId", String.valueOf(USER_ID));
        ticketParameters.put("category", CATEGORY);
        ticketParameters.put("place", String.valueOf(PLACE));
        return ticketParameters;
    }

    private Ticket generateTicket() {
        Ticket ticket = new TicketEntity();
        ticket.setId(TICKET_ID);
        ticket.setEventId(EVENT_ID);
        ticket.setUserId(USER_ID);
        ticket.setCategory(Ticket.Category.valueOf(CATEGORY));
        ticket.setPlace(PLACE);
        return ticket;
    }

    private List<Ticket> generateTickets() {
        Ticket ticket = generateTicket();
        List<Ticket> tickets = new ArrayList<>(2);
        tickets.add(ticket);
        tickets.add(ticket);
        return tickets;
    }
}
