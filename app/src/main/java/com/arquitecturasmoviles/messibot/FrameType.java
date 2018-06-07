package com.arquitecturasmoviles.messibot;

public enum FrameType {
    PROGRESS(0),
    BACK(1),
    LEFT(2),
    RIGHT(3),
    STOP(4),
    CHANGE_PASSWORD(5),
    VBAT(6),
    STREAM(7),
    SET_NAME(8);

    public final int value;

    FrameType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
