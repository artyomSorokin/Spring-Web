package com.epam.cdp.module4.homework1.controller;

import com.epam.cdp.module4.homework1.entity.EventEntity;
import com.epam.cdp.module4.homework1.facade.BookingFacade;
import com.epam.cdp.module4.homework1.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epam.cdp.module4.homework1.controller.EventController.DATE_FORMATTER;
import static com.epam.cdp.module4.homework1.controller.EventController.DATE_WITH_TIME_FORMATTER;
import static com.epam.cdp.module4.homework1.util.WebConstant.EVENTS_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.EVENT_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.INFO_TEMPLATE_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(EventController.class)
public class EventControllerTest {

    private static final String DATE_STRING = "15-12-2019";
    private static final String DATE_TIME_STRING = "15-12-2019 09:00";
    private static final String TITLE = "Concert";
    private static final int EVENT_ID = 100;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingFacade bookingFacade;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetEventById() throws Exception {
        Event event = generateEvent();
        when(bookingFacade.getEventById(EVENT_ID)).thenReturn(event);

        mockMvc.perform(
                get("/event/{eventId}", EVENT_ID)
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(EVENT_TEMPLATE_NAME));
    }

    @Test
    public void testGetEventsByDate() throws Exception {
        List<Event> events = generateEvents();
        when(bookingFacade.getEventsForDay(
                new SimpleDateFormat(DATE_FORMATTER).parse(DATE_STRING), Integer.MAX_VALUE, 1))
                .thenReturn(events);

        mockMvc.perform(
                get("/events/date/{date}", DATE_STRING)
                        .param("pageSize", String.valueOf(Integer.MAX_VALUE))
                        .param("pageNum", String.valueOf(1))
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(EVENTS_TEMPLATE_NAME));
    }

    @Test
    public void testGetEventsByTitle() throws Exception {
        List<Event> events = generateEvents();
        when(bookingFacade.getEventsByTitle(TITLE, Integer.MAX_VALUE, 1)).thenReturn(events);

        mockMvc.perform(
                get("/events/title/{title}", TITLE)
                        .param("pageSize", String.valueOf(Integer.MAX_VALUE))
                        .param("pageNum", String.valueOf(1))
                        .accept(MediaType.TEXT_HTML_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(EVENTS_TEMPLATE_NAME));
    }

    @Test
    public void testCreateEvent() throws Exception {
        Map<String, String> eventParameters = generateEventParameters();
        Event event = generateEvent();
        when(bookingFacade.createEvent(any())).thenReturn(event);

        mockMvc.perform(
                post("/event")
                        .content(objectMapper.writeValueAsString(eventParameters))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.view().name(EVENT_TEMPLATE_NAME));
    }

    @Test
    public void testUpdateEvent() throws Exception {
        Map<String, String> eventParameters = generateEventParameters();
        Event event = generateEvent();
        when(bookingFacade.updateEvent(any())).thenReturn(event);

        mockMvc.perform(
                put("/event")
                        .content(objectMapper.writeValueAsString(eventParameters))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(EVENT_TEMPLATE_NAME));
    }

    @Test
    public void testDeleteEvent() throws Exception {
        when(bookingFacade.deleteEvent(EVENT_ID)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/event/{eventId}", EVENT_ID))
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.view().name(INFO_TEMPLATE_NAME));
    }

    private Map<String, String> generateEventParameters() {
        Map<String, String> eventParameters = new HashMap<>();
        eventParameters.put("title", TITLE);
        eventParameters.put("eventDate", DATE_TIME_STRING);
        return eventParameters;
    }

    private Event generateEvent() throws ParseException {
        Event event = new EventEntity();
        event.setId(EVENT_ID);
        event.setTitle(TITLE);
        event.setDate(new SimpleDateFormat(DATE_WITH_TIME_FORMATTER).parse(DATE_TIME_STRING));
        return event;
    }

    private List<Event> generateEvents() throws ParseException {
        Event event = generateEvent();
        List<Event> events = new ArrayList<>(2);
        events.add(event);
        events.add(event);
        return events;
    }
}
