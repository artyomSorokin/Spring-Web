package com.epam.cdp.module4.homework1.controller;

import com.epam.cdp.module4.homework1.entity.EventEntity;
import com.epam.cdp.module4.homework1.facade.BookingFacade;
import com.epam.cdp.module4.homework1.model.Event;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.epam.cdp.module4.homework1.util.WebConstant.EVENTS_ATTRIBUTE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.EVENTS_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.EVENT_ATTRIBUTE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.EVENT_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.INFO_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.MESSAGE_ATTRIBUTE_NAME;


@Controller
@Validated
public class EventController {

    public static final String DATE_FORMATTER = "dd-MM-yyyy";
    public static final String DATE_WITH_TIME_FORMATTER = "dd-MM-yyyy HH:mm";

    @Resource
    private BookingFacade bookingFacade;

    /**
     * Finds event by id.
     *
     * @param eventId Event id.
     * @param model   model on view.
     * @return        event template.
     */
    @GetMapping("/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public String getEventById(
            @PathVariable(name = "eventId")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Event id must be greater than 0")
                    String eventId, Model model) {
        Event event = bookingFacade.getEventById(Long.valueOf(eventId));
        model.addAttribute(EVENT_ATTRIBUTE_NAME, event);
        return EVENT_TEMPLATE_NAME;
    }

    /**
     * Finds events by date.
     *
     * @param date            date.
     * @param pageSize        page size.
     * @param pageNum         page num.
     * @param model           model on view.
     * @return                events template.
     * @throws ParseException exception from parsing.
     */
    @GetMapping("/events/date/{date}")
    @ResponseStatus(HttpStatus.OK)
    public String getEventsByDate(
            @PathVariable(name = "date")
            @NotEmpty(message = "Date should not be empty")
                    String date,
            @RequestParam(name = "pageSize")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page size must be greater than 0")
                    String pageSize,
            @RequestParam(name = "pageNum")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page num must be greater than 0")
                    String pageNum,
            Model model) throws ParseException {
        Date parseDate = new SimpleDateFormat(DATE_FORMATTER).parse(date);
        List<Event> events = bookingFacade.getEventsForDay(
                parseDate, Integer.valueOf(pageSize), Integer.valueOf(pageNum));
        model.addAttribute(EVENTS_ATTRIBUTE_NAME, events);
        return EVENTS_TEMPLATE_NAME;
    }

    /**
     * Finds events by title.
     *
     * @param title    title of Event.
     * @param pageSize page size.
     * @param pageNum  page num.
     * @param model    model on view.
     * @return         events template.
     */
    @GetMapping("/events/title/{title}")
    @ResponseStatus(HttpStatus.OK)
    public String getEventsByTitle(
            @PathVariable(name = "title")
            @NotEmpty(message = "Title should not be empty")
                    String title,
            @RequestParam(name = "pageSize")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page size must be greater than 0")
                    String pageSize,
            @RequestParam(name = "pageNum")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page num must be greater than 0")
                    String pageNum,
            Model model) {
        List<Event> events = bookingFacade.getEventsByTitle(title, Integer.valueOf(pageSize), Integer.valueOf(pageNum));
        model.addAttribute(EVENTS_ATTRIBUTE_NAME, events);
        return EVENTS_TEMPLATE_NAME;
    }

    /**
     * Creates an event entity.
     *
     * @param eventParameters parameters for create User.
     * @param model           model on view.
     * @return                event template.
     * @throws ParseException exception from parsing.
     */
    @PostMapping("event")
    @ResponseStatus(value = HttpStatus.CREATED)
    public String createEvent(
            @RequestBody
                    Map<String, String> eventParameters, Model model) throws ParseException {
        Event eventToCreate = convertToEvent(eventParameters);
        Event createdEvent = bookingFacade.createEvent(eventToCreate);
        model.addAttribute(EVENT_ATTRIBUTE_NAME, createdEvent);
        return EVENT_TEMPLATE_NAME;
    }

    /**
     * Updates the event entity.
     *
     * @param eventParameters parameters for update User.
     * @param model           model on view.
     * @return                event template.
     * @throws ParseException exception from parsing.
     */
    @PutMapping("event")
    public String updateEvent(
            @RequestBody
                    Map<String, String> eventParameters, Model model) throws ParseException {
        Event eventToUpdate = convertToEvent(eventParameters);
        Event updatedEvent = bookingFacade.updateEvent(eventToUpdate);
        if (updatedEvent == null) {
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Could not update event, please check id '" + eventToUpdate.getId() + "'");
            return INFO_TEMPLATE_NAME;
        }
        model.addAttribute(EVENT_ATTRIBUTE_NAME, updatedEvent);
        return EVENT_TEMPLATE_NAME;
    }

    /**
     * Deletes the event entity.
     *
     * @param eventId Event id.
     * @param model   model on view.
     * @return        info template with result of deleting.
     */
    @DeleteMapping("event/{eventId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String deleteEvent(
            @PathVariable(name = "eventId")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Event id must be greater than 0")
                    String eventId, Model model) {
        if (bookingFacade.deleteEvent(Long.valueOf(eventId))) {
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Event with id '" + eventId + "' has been deleted");
        } else {
            model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Could not delete event, please check id '" + eventId + "'");
        }
        return INFO_TEMPLATE_NAME;
    }

    private Event convertToEvent(@RequestBody Map<String, String> eventParameters) throws ParseException {
        String id = eventParameters.get("id");
        String title = eventParameters.get("title");
        String eventDate = eventParameters.get("eventDate");
        //String ticketPrice = eventParameters.get("ticketPrice");
        Event event = new EventEntity();
        if (!StringUtils.isEmpty(id)) {
            event.setId(Long.valueOf(id));
        }
        event.setTitle(title);
        event.setDate(new SimpleDateFormat(DATE_WITH_TIME_FORMATTER).parse(eventDate));
        return event;
    }
}
