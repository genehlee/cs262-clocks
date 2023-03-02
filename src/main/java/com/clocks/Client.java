package com.clocks;

import java.util.concurrent.ThreadLocalRandom;


public class Client {
    /* TODO: initialize messageQueue */
    static long logicalClock;
    static int ticksPerSecond;
    public static void main(String[] args) {
        // Assign a random clock speed to the client and calculate the wait time
        ticksPerSecond = ThreadLocalRandom.current().nextInt(Constants.MIN_CLOCK_SPEED, Constants.MAX_CLOCK_SPEED + 1);
        int waitMillis = 1000 / ticksPerSecond;

        while(true) {
            // Determine random action (1-10)
            int action = ThreadLocalRandom.current().nextInt(Constants.MIN_ACTION, Constants.MAX_ACTION + 1);

            // Perform action
            switch(action) {
                case 1:
                    System.out.println("Action 1");
                    break;
                case 2:
                    System.out.println("Action 2");
                    break;
                case 3:
                    System.out.println("Action 3");
                    break;
                // Internal event
                default:
                    System.out.println("Default action");
                    break;
            }

            /* TODO: Print to log */

            // Increment the logical clock
            logicalClock++;

            // Wait according to the clock speed
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
