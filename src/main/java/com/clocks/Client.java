package com.clocks;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import com.clocks.MessageServiceOuterClass.Message;
import com.clocks.MessageServiceOuterClass.Empty;

/*
 * Client program for the clocks project
 */
public class Client implements Runnable {

    // Arguments passed to the client
    private String[] args;

    // Port of this client
    public int port;

    // Ports of other clients
    public int[] other_ports;
    
    // Clock speed of this client
    public int ticksPerSecond;
    public long waitMillis;

    // Message queue
    public ConcurrentLinkedQueue<QueueMessage> messageQueue;

    // Logical clock value
    public long logicalClock;

    // PrintWriter to write to log file
    public PrintWriter logger;
    
    // gRPC stubs
    public MessageServiceGrpc.MessageServiceBlockingStub[] stubs;

    // gRPC server
    public Server server;

    public Client(String[] args) {
        this.args = args;
    }

    /* 
     * Runs the client
     * @param args Client port, other port 1, other port 2
     */
    public void run() {
        // start a timer
        long startTime = System.currentTimeMillis();
        try {
            // Initialize the client
            init(args);
            while(true) {
                // if the time for the simulation is up, then exit the program
                if(System.currentTimeMillis() - startTime > Constants.LIFETIME) return;
                Action action;
                // If there are any messages in the queue, then take the RECEIVE_MESSAGE action
                if(!messageQueue.isEmpty()) {
                    action = Action.RECEIVE_MESSAGE;
                }
                // Otherwise, pick a random action
                else {
                    int rand = randInt(Constants.MIN_ACTION, Constants.MAX_ACTION);
                    action = Action.fromInt(rand);
                }
                // Perform the action
                handleAction(action);
                // Log the action
                log(action);
                // Increment the logical clock
                logicalClock++;
                // Wait according to the clock speed
                Thread.sleep(waitMillis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the client
            close();
        }
    }

    /*
     * Initializes the client
     * @param args Client port, other port 1, other port 2
     */
    public void init(String[] args) throws InterruptedException, IOException {
        System.out.println("Running client...");

        // Check if the correct number of arguments were passed
        checkArgs(args);

        // Read port numbers from the command line
        readPorts(args);

        // Assign a random clock speed to the client and calculate the wait time
        ticksPerSecond = randInt(Constants.MIN_CLOCK_SPEED, Constants.MAX_CLOCK_SPEED);
        waitMillis = Constants.MILLISECONDS_PER_SECOND / ticksPerSecond;

        // Initialize the message queue
        messageQueue = new ConcurrentLinkedQueue<QueueMessage>();

        // Initialize a log file
        logger = new PrintWriter(port + ".log");

        // Initialize the logical clock
        logicalClock = 0;

        // create the gRPC server
        server = ServerBuilder.forPort(port)
            .addService(new MessageServiceImpl())
            .build();
        
        // start the server
        server.start();
        
        // wait for other clients to start
        Thread.sleep(Constants.WAIT_TIME_FOR_CONNECTIONS);

        // connect to the two other clients
        stubs = new MessageServiceGrpc.MessageServiceBlockingStub[other_ports.length];
        for(int i = 0; i < other_ports.length; i++) {
            System.out.println("Connecting port " + port + " to port " + other_ports[i]);
            stubs[i] = MessageServiceGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress(Constants.HOST, other_ports[i])
                    .usePlaintext()
                    .build()
            );
        }

        // log the ticks per second
        logger.println("Ticks per second: " + ticksPerSecond);
    }

    private void checkArgs(String[] args) {
        if(args.length != Constants.NUM_PORTS) {
            String errorMessage = "Usage: java Client <client port>";
            for (int i = 1; i < Constants.NUM_PORTS; i++) {
                errorMessage += " <other port " + i + ">";
            }
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void readPorts(String[] args) {
        port = Integer.parseInt(args[0]);
        other_ports = new int[Constants.NUM_PORTS - 1];
        for (int i = 0; i < other_ports.length; i++) {
            other_ports[i] = Integer.parseInt(args[i + 1]);
        }
    }

    /* 
     * Closes the client
     */
    public void close() {
        System.out.println("Shutting down...");

        // Close the logger
        logger.close();

        // Log the final logical clock value
        System.out.println("Final logical clock value: " + logicalClock);

        // Log the message queue size
        System.out.println("Message queue size: " + messageQueue.size());
    }

    /*
     * Handles an action
     * @param action Action to handle
     */
    public void handleAction(Action action) {
        switch(action) {
            case RECEIVE_MESSAGE:
                // Remove the first message in the queue
                QueueMessage message = messageQueue.poll();

                // Update local logical clock
                logicalClock = Math.max(logicalClock, message.getTimestamp());
                break;
            case SEND_TO_PORT_1:
                // Send a message to port 1
                send(stubs[0], logicalClock);
                break;
            case SEND_TO_PORT_2:
                // Send a message to port 2
                send(stubs[1], logicalClock);
                break;
            case SEND_TO_BOTH_PORTS:
                // Send a message to ports 1 and 2
                send(stubs[0], logicalClock);
                send(stubs[1], logicalClock);
                break;
            case INTERNAL_EVENT:
                // Do nothing
                break;
            default:
                // Undefined action
                throw new IllegalArgumentException("Undefined action: " + action);
        }
    }

    /*
     * Logs information based on action
     * @param action Action to log
     */
    public void log(Action action) {
        long global_time = System.currentTimeMillis();

        String message;
        switch(action) {
            case RECEIVE_MESSAGE:
                message = "Received message at " + global_time + " at logical clock time " + logicalClock + ". Queue length is " + messageQueue.size() + ".";
                break;
            case SEND_TO_PORT_1:
            case SEND_TO_PORT_2:
                message = "Sent a message at " + global_time + " at logical clock time " + logicalClock + ".";
                break;
            case SEND_TO_BOTH_PORTS:
                message = "Sent two messages at " + global_time + " at logical clock time " + logicalClock + ".";
                break;
            case INTERNAL_EVENT:
                message = "Internal event at " + global_time + " at logical clock time " + logicalClock + ".";
                break;
            default:
                message = "Undefined action at " + global_time + " at logical clock time " + logicalClock + ".";
                break;
        }
        logger.println(message);
    }

    /*
    * Sends a message to another client through its gRPC stub with `timestamp`
    * @param recipientStub gRPC stub of the recipient
    * @param timestamp Timestamp of the message
    */
    public void send(MessageServiceGrpc.MessageServiceBlockingStub recipientStub, long timestamp) {
        // Create the message
        Message message = Message.newBuilder()
            .setSender(port)
            .setTimestamp(timestamp)
            .build();

        // Send the message
        recipientStub.send(message);
    }

    /*
     * Utility function for random number inclusive of min and max
     * @param min Minimum value
     * @param max Maximum value
     * @return Random integer between min and max inclusive
     */
    public int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /*
    * gRPC server implementation. Defines the handlers that are called when a message is received by the gRPC server.
    */
    class MessageServiceImpl extends MessageServiceGrpc.MessageServiceImplBase {
        /*
         * Handles a message received
         * @param request Message received
         * @param responseObserver Observer to send response to
         */
        @Override
        public void send(Message request,
        StreamObserver<Empty> responseObserver) {
            // translate the gRPC message to the internal message format
            QueueMessage message = new QueueMessage(request.getSender(), request.getTimestamp());
            
            // Add the message to the queue
            messageQueue.add(message);

            // Send an empty message back to the sender
            Empty response = Empty.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    /* 
     * Runs the program
     * @param args Client port, other port 1, other port 2
     */
    public static void main(String[] args) {
        Client client = new Client(args);
        client.run();
    }
}
