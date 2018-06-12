package edu.asu.plp.jms;

import com.google.gson.Gson;
import edu.asu.plp.tool.backend.isa.events.SimulatorControlEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.Serializable;

/**
 * Created by prashanth on 4/18/18.
 */
@Component
public class JmsPublisher implements Serializable{
    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${jsa.activemq.queue}")
    String destinationQueue;

    public void send(SimulatorControlEvent msg) throws JMSException{
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        try {
            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("Request-to-simulator");

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            //producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            //producer.setTimeToLive(10);

            // Create a messages
            //TextMessage message = session.createTextMessage("Hello");
            Gson gson = new Gson();
            String message = gson.toJson(msg);
            TextMessage finalMessage = session.createTextMessage(message);
           // ObjectMessage message = session.createObjectMessage();
           // message.setObject(msg);
            // Tell the producer to send the message
            producer.send(finalMessage);

            // Clean up
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }


    }

}
