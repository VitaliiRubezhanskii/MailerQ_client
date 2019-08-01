package main;

import com.google.gson.JsonObject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        final String fromEmail = "vitalii.rubizhanskyi@geckodynamics.com";
        final String password = "Vitalik@1992";
        final String toEmail = "vitalii.rubezhanskii@gmail.com";

        System.out.println("SSLEmail Start");
        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
//        props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
//        props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
        props.put("mail.smtp.port", 25); //SMTP Port

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        JsonObject person = new JsonObject();
        person.addProperty("firstName", "Sergey");
        person.addProperty("lastName", "Kargopolov");
        person.addProperty("Key_1050", "Key_1050" );

        Session session = Session.getDefaultInstance(props, auth);

        System.out.println("Session created");
        sendEmail(session, toEmail,"SSLEmail Testing Subject", person.toString());
    }






    public static void sendEmail(Session session, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("vitalii.rubezhanskii@gmail.com", "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse("vitaliy.rubizhanskyi@gmail.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
