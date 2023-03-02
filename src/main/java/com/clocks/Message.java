package com.clocks;

/*
 * Data structure for a message
 */
public class Message {
    private int sender;
    private int timestamp;

    public Message(int sender, int timestamp) {
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public int getSender() {
        return sender;
    }

    public int getTimestamp() {
        return timestamp;
    }
}
