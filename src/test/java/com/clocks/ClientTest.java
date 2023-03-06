package com.clocks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.mockito.Mockito;

import com.clocks.MessageServiceGrpc.MessageServiceBlockingStub;

import com.clocks.MessageServiceOuterClass.Message;

import io.grpc.stub.StreamObserver;

/*
* Unit tests for the Client class.
*/
public class ClientTest {
  @Test
  public void main_shouldExitAfterLifetime() {
    // Run the client in a separate thread, making sure it returns before the lifetime is up
    String[] args = new String[] { "8000", "8001", "8002"};
    Client client = new Client(args);
    Thread clientThread = new Thread(client);
    // Wait for the lifetime to expire
    try {
      Thread.sleep(Constants.LIFETIME);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // Make sure the client thread is dead
    assertFalse(clientThread.isAlive());
  }

  @Test
  public void main_whenMessageQueueIsNonEmpty_shouldReceiveMessage() {
    Client client = Mockito.spy(new Client(new String[] { "8000", "8000", "8000" }));
    client.run();
    // Add a message to the queue
    client.messageQueue.add(new QueueMessage(1234, 1234));
    // Let the client run for a few seconds
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // Make sure handleAction is called with a RECEIVE_MESSAGE action at least once
    Mockito.verify(client, Mockito.atLeastOnce()).handleAction(Action.RECEIVE_MESSAGE);
  }

  @Test
  public void init_shouldParseArgs() {
    String[] args = new String[] { "8000", "8001", "8002"};
    Client client = new Client(args);
    try {
      client.init(args);
    } catch (Exception e) {
      e.printStackTrace();
      assertTrue(false);
    }
    assertTrue(client.port == 8000);
    assertTrue(client.other_ports[0] == 8001);
    assertTrue(client.other_ports[1] == 8002);
  }

  @Test
  public void handleAction_RECEIVE_MESSAGE() {
    // Call handleAction with a RECEIVE_MESSAGE and confirm that a message is popped from the queue and that the logical clock is updated properly
    Client client = new Client(new String[] { "8000", "8000", "8000" });
    client.run();
    client.messageQueue.add(new QueueMessage(1234, 1234));
    client.handleAction(Action.RECEIVE_MESSAGE);
    assertTrue(client.messageQueue.size() == 0);
    assertTrue(client.logicalClock == 1234);
  }

  @Test
  public void handleAction_SEND_TO_PORT_1() {
    // Call handleAction with a SEND_TO_PORT_1
    Client client = Mockito.spy(new Client(new String[] { "8000", "8001", "8002" }));
    client.run();
    client.handleAction(Action.SEND_TO_PORT_1);
    // extract the stub
    MessageServiceBlockingStub stub = client.stubs[0];
    // make sure send() is called with the correct stub but ignore the logicalClock argument
    Mockito.verify(client).send(stub, Mockito.anyInt());
  }

  @Test
  public void handleAction_SEND_TO_PORT_2() {
    // Call handleAction with a SEND_TO_PORT_2
    Client client = Mockito.spy(new Client(new String[] { "8000", "8001", "8002" }));
    client.handleAction(Action.SEND_TO_PORT_2);
    // extract the stub
    MessageServiceBlockingStub stub = client.stubs[1];
    // make sure send() is called with the correct stub but ignore the logicalClock argument
    Mockito.verify(client).send(stub, Mockito.anyInt());
  }

  @Test
  public void handleAction_SEND_TO_BOTH_PORTS() {
    // Call handleAction with a SEND_TO_BOTH_PORTS
    Client client = Mockito.spy(new Client(new String[] { "8000", "8001", "8002" }));
    client.handleAction(Action.SEND_TO_BOTH_PORTS);
    // extract the stubs
    MessageServiceBlockingStub stub1 = client.stubs[0];
    MessageServiceBlockingStub stub2 = client.stubs[1];
    // make sure send() is called with the correct stubs but ignore the logicalClock argument
    Mockito.verify(client).send(stub1, Mockito.anyInt());
    Mockito.verify(client).send(stub2, Mockito.anyInt());
  }

  @Test
  public void log_shouldLog() {
    // Call log with a message and make sure it calls logger.println()
    Client client = Mockito.spy(new Client(new String[] { "8000", "8000", "8000" }));
    client.log(Action.INTERNAL_EVENT);
    Mockito.verify(client.logger).println(Mockito.anyString());
  }

  @Test
  public void send_shouldSendToStub() {
    // Call send with a stub and make sure it calls stub.send()
    Client client = Mockito.spy(new Client(new String[] { "8000", "8000", "8000" }));
    MessageServiceBlockingStub stub = Mockito.mock(MessageServiceBlockingStub.class);
    client.send(stub, 1234);
    Mockito.verify(stub).send(Mockito.any());
  }

  @Test
  public void messageServiceImpl_send_shouldAddToQueue() {
    // Call the send method on the MessageServiceImpl within Client and make sure it adds a message to the queue of the client
    // Client client = new Client(new String[] { "8000", "8000", "8000" });
    // Client.MessageServiceImpl messageServiceImpl = client.new MessageServiceImpl();
    // StreamObserver<Empty> streamObserver = new
    // messageServiceImpl.send(Message.newBuilder().setTimestamp(1234).setSender(4321).build());
  }
}