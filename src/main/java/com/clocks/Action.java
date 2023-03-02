package com.clocks;

/*
 * Enum for the different actions a client can perform
 */
public enum Action {
    RECEIVE_MESSAGE(Constants.RECEIVE_MESSAGE_NUM),
    SEND_TO_PORT_1(Constants.SEND_TO_PORT_1_NUM),
    SEND_TO_PORT_2(Constants.SEND_TO_PORT_2_NUM),
    SEND_TO_BOTH_PORTS(Constants.SEND_TO_BOTH_PORTS_NUM),
    INTERNAL_EVENT(Constants.INTERNAL_EVENT_NUM);

    private int numVal;

    Action(int numVal) {
        this.numVal = numVal;
    }

    public static Action fromInt(int i) {
        for (Action b : Action.values()) {
            if (b.numVal == i) { return b; }
        }
        return INTERNAL_EVENT;
    }
}
