package com.cricketpulse.entity;

public enum MatchType {
    T20(20),
    ODI(50),
    TEST(90),
    T10(10);

    private final int defaultOvers;

    MatchType(int defaultOvers) {
        this.defaultOvers = defaultOvers;
    }

    public int getDefaultOvers() {
        return defaultOvers;
    }
}
