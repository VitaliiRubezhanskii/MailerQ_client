package main;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {
    /**
     *  Settings for RabbitMQ
     */

    // same as the rabbitmq-host option in the config file
    private static String hostname = "localhost";


    // same as the rabbitmq-user option in the config file
    private static String username = "guest";

    // same as the rabbitmq-password option in the config file
    private static String password = "guest";

    // same as the rabbitmq-vhost option in the config file
    private static String vhost = "/";

    // same as the rabbitmq-outbox option in the config file
    private static String outbox = "inbox";

    /**
     *  Settings for sending out the test email
     */

    // domain where the test message should be delivered
    private static String recipientDomain = "gmail.com";

    // email where the test message should be delivered
    private static String recipientEmail = "viatlii.rubezhanskii@gmail.com";

    // address where the email was sent from
    private static String fromAddress = "vitalii.rubizhanskyi@geckodynamics.com";

    public static void main(String[] argv) throws Exception {
        // create a new ConnectionFactory
        ConnectionFactory factory = new ConnectionFactory();

        // set the host to RabbitMQ
        factory.setHost(hostname);

//        factory.setPort(5672);

        // set the user name to connect to RabbitMQ
        factory.setUsername(username);

        // set the password to connect to RabbitMQ
        factory.setPassword(password);

        // set the virtual host to RabbitMQ
        factory.setVirtualHost(vhost);

        // create the new connection
        Connection connection = factory.newConnection();

        // create the channel
        Channel channel = connection.createChannel();

        // declare the queue
        channel.queueDeclare(outbox, true, false, false, null);

        // create the message

        JsonObject message = new JsonObject();
        JsonObject mime = new JsonObject();
        mime.addProperty("From","vitalii.rubizhanskyi@geckodynamics.com");
        mime.addProperty("To", "viatlii.rubezhanskii@gmail.com");

        message.addProperty("envelope", fromAddress);
        message.addProperty("recipient", recipientEmail);
        message.addProperty("mime", mime.toString());



        String messageAsString = message.toString();

        // publish the message on the queue
        channel.basicPublish("outgoing.exchange", "messages.key", null, messageAsString.getBytes());

        // some output
        System.out.println("Sent: '" + message + "'");

        // close the channel
        channel.close();

        // close the connection
        connection.close();
    }
}

