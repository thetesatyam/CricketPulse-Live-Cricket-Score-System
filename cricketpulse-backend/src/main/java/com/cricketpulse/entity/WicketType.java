package com.cricketpulse.entity;

public enum WicketType {
    BOWLED(true),
    CAUGHT(true),
    LBW(true),
    RUN_OUT(false),
    STUMPED(true),
    HIT_WICKET(true),
    RETIRED_HURT(false);

    private final boolean creditedToBowler;

    WicketType(boolean creditedToBowler) {
        this.creditedToBowler = creditedToBowler;
    }

    public boolean isCreditedToBowler() {
        return creditedToBowler;
    }
}
