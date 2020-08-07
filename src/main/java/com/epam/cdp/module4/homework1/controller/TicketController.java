package com.epam.cdp.module4.homework1.controller;

import com.epam.cdp.module4.homework1.entity.EventEntity;
import com.epam.cdp.module4.homework1.entity.UserEntity;
import com.epam.cdp.module4.homework1.facade.BookingFacade;
import com.epam.cdp.module4.homework1.model.Event;
import com.epam.cdp.module4.homework1.model.Ticket;
import com.epam.cdp.module4.homework1.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

import static com.epam.cdp.module4.homework1.util.WebConstant.INFO_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.MESSAGE_ATTRIBUTE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.TICKETS_ATTRIBUTE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.TICKETS_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.TICKET_ATTRIBUTE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.TICKET_PDF_TEMPLATE_NAME;
import static com.epam.cdp.module4.homework1.util.WebConstant.TICKET_TEMPLATE_NAME;


@Controller
@Validated
public class TicketController {

    @Resource
    private BookingFacade bookingFacade;

    /**
     * Books a ticket.
     *
     * @param ticketParameters parameters for book ticket.
     * @param model            model on view.
     * @return event template.
     */
    @PostMapping("/ticket")
    @ResponseStatus(HttpStatus.CREATED)
    public String bookTicket(
            @RequestBody
                    Map<String, String> ticketParameters, Model model) {
        String eventId = ticketParameters.get("eventId");
        String userId = ticketParameters.get("userId");
        String category = ticketParameters.get("category");
        String place = ticketParameters.get("place");

        Ticket ticket = bookingFacade.bookTicket(Long.valueOf(userId), Long.valueOf(eventId),
                Integer.valueOf(place), Ticket.Category.valueOf(category));
        model.addAttribute(TICKET_ATTRIBUTE_NAME, ticket);
        return TICKET_TEMPLATE_NAME;
    }

    /**
     * Finds tickets by user id.
     *
     * @param userId   User id.
     * @param pageSize page size.
     * @param pageNum  page num.
     * @param model    model on view.
     * @return tickets template.
     */
    @GetMapping("/ticket/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String getTicketsByUserId(
            @PathVariable(name = "userId")
            @Pattern(regexp = "^[1-9]\\d*$", message = "User id must be greater than 0")
                    String userId,
            @RequestParam(name = "pageSize")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page size must be greater than 0")
                    String pageSize,
            @RequestParam(name = "pageNum")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page num must be greater than 0")
                    String pageNum,
            Model model) {
        User user = new UserEntity();
        user.setId(Long.valueOf(userId));
        List<Ticket> tickets = bookingFacade.getBookedTickets(user, Integer.valueOf(pageSize), Integer.valueOf(pageNum));
        model.addAttribute(TICKETS_ATTRIBUTE_NAME, tickets);
        return TICKETS_TEMPLATE_NAME;
    }

    /**
     * Finds tickets by user id.
     *
     * @param userId   User id.
     * @param pageSize page size.
     * @param pageNum  page num.
     * @param model    model on view.
     * @return tickets template.
     */
    @GetMapping(value = "/ticket/user/{userId}", headers = "accept=application/pdf")
    @ResponseStatus(HttpStatus.OK)
    public String getTicketsAsPdfByUserId(
            @PathVariable(name = "userId")
            @Pattern(regexp = "^[1-9]\\d*$", message = "User id must be greater than 0")
                    String userId,
            @RequestParam(name = "pageSize")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page size must be greater than 0")
                    String pageSize,
            @RequestParam(name = "pageNum")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page num must be greater than 0")
                    String pageNum,
            Model model) {
        User user = new UserEntity();
        user.setId(Long.valueOf(userId));
        List<Ticket> tickets = bookingFacade.getBookedTickets(user, Integer.valueOf(pageSize), Integer.valueOf(pageNum));
        model.addAttribute(TICKETS_ATTRIBUTE_NAME, tickets);
        return TICKET_PDF_TEMPLATE_NAME;
    }

    /**
     * Finds tickets by event id.
     *
     * @param eventId  Event id.
     * @param pageSize page size.
     * @param pageNum  page num.
     * @param model    model on view.
     * @return tickets template.
     */
    @GetMapping("/ticket/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public String getTicketsByEventId(
            @PathVariable(name = "eventId")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Event id must be greater than 0")
                    String eventId,
            @RequestParam(name = "pageSize")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page size must be greater than 0")
                    String pageSize,
            @RequestParam(name = "pageNum")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Page num must be greater than 0")
                    String pageNum,
            Model model) {
        Event event = new EventEntity();
        event.setId(Long.valueOf(eventId));
        List<Ticket> tickets = bookingFacade.getBookedTickets(
                event, Integer.valueOf(pageSize), Integer.valueOf(pageNum));
        model.addAttribute(TICKETS_ATTRIBUTE_NAME, tickets);
        return TICKETS_TEMPLATE_NAME;
    }

    /**
     * Cancels the ticket.
     *
     * @param ticketId Ticket id.
     * @param model    model on view.
     * @return info template with cancellation result.
     */
    @DeleteMapping("/ticket/{ticketId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String cancelTicket(
            @PathVariable(name = "ticketId")
            @Pattern(regexp = "^[1-9]\\d*$", message = "Ticket id must be greater than 0")
                    String ticketId, Model model) {
        if (bookingFacade.cancelTicket(Long.valueOf(ticketId))) {
            model.addAttribute(
                    MESSAGE_ATTRIBUTE_NAME, "Ticket with id '" + ticketId + "' has been canceled");
        } else {
            model.addAttribute(
                    MESSAGE_ATTRIBUTE_NAME, "Could not delete ticket, please check id '" + ticketId + "'");
        }
        return INFO_TEMPLATE_NAME;
    }
}
