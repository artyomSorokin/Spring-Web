package com.epam.cdp.module4.homework1.entity;

import com.epam.cdp.module4.homework1.model.Ticket;

public class TicketEntity extends AbstractEntity implements Ticket {

    private long eventId;
    private long userId;
    private Category category;
    private int place;

    @Override
    public long getEventId() {
        return eventId;
    }

    @Override
    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public int getPlace() {
        return place;
    }

    @Override
    public void setPlace(int place) {
        this.place = place;
    }
}
