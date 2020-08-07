package com.epam.cdp.module4.homework1.entity;

import com.epam.cdp.module4.homework1.model.Event;

import java.util.Date;

public class EventEntity extends AbstractEntity implements Event {

    private String title;
    private Date date;

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

}
