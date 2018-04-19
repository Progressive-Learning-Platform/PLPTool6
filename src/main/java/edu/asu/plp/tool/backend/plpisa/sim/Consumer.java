package edu.asu.plp.tool.backend.plpisa.sim;

import java.util.ArrayList;
import java.util.List;

import javax.jms.*;

import edu.asu.plp.tool.backend.isa.events.SimulatorControlEvent;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * This thread will keep listening on the message transfer from back end.
 * And notify the front end through registered listeners(call backs).
 */
public class Consumer implements Runnable, ExceptionListener {
    /**
     * Listeners(Call backs) list.
     */
    private List<SnapshotListener> listeners = new ArrayList<SnapshotListener>();

    /**
     * Register a listener(call back)
     * @param toAdd The call back need to execute after received the message.
     */
    public void addListener(SnapshotListener toAdd) {
        listeners.add(toAdd);
    }

    /**
     * Keep listening on the message event
     * When received the message trigger call backs one by one.
     */
    public void run() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("Request-to-simulator");

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            JmsTemplate template = new JmsTemplate();
            while (true) {
                // Wait for a message
                Message message = consumer.receive(10);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println(text);
                    //for (SnapshotListener hl : listeners)
                        //hl.receiveSnapshot(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}

