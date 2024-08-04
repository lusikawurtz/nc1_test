package com.nc1_test.entities;

public enum NewsTime {

    MORNING("morning"),
    DAY("day"),
    EVENING("evening"),
    ;

    private final String time;

    NewsTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return this.time;
    }

}