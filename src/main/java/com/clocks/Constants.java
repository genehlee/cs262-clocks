package com.clocks;

public class Constants {
    // Constants for randomness
    public static final int MIN_CLOCK_SPEED = 1;
    public static final int MAX_CLOCK_SPEED = 6;
    public static final int MIN_ACTION = 1;
    public static final int MAX_ACTION = 10;

    // Constants for actions
    public static final int RECEIVE_MESSAGE_NUM = 0;
    public static final int SEND_TO_PORT_1_NUM = 1;
    public static final int SEND_TO_PORT_2_NUM = 2;
    public static final int SEND_TO_BOTH_PORTS_NUM = 3;
    public static final int INTERNAL_EVENT_NUM = -1;

    // Constants for networking
    public static final String HOST = "localhost";
    public static final int NUM_PORTS = 3;
}
