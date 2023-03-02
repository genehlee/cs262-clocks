package com.clocks;

/*
 * Data structure for a message
 */
public class QueueMessage {
    private int sender;
    private long timestamp;

    public QueueMessage(int sender, long timestamp) {
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public int getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
