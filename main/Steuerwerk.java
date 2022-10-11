package main;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
 
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import avg_co2_sensor.Sensorik;

public class Steuerwerk {

	 private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	 private static String[] subject = {"Sensor1", "Sensor2"};
	 private static int[] anzahl_studis = {10, 5};
	 
	public static void main(String[] args) throws JMSException, InterruptedException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        
        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE); 
        
        LinkedList<Sensorik> sensoren = new LinkedList<Sensorik>();
        
        for(int i = 0; i < subject.length; i++) {
        	Destination destination = session.createQueue(subject[i]);
        	MessageProducer producer = session.createProducer(destination);
        	Sensorik sensorik = new Sensorik(subject[i], session, producer, anzahl_studis[i]);
        	sensoren.add(sensorik);
        }
        
        int i = 0;
        while(i <= 10) {
        TimeUnit.SECONDS.sleep(5);
        for (int t = 0; t < sensoren.size();t++) {
        	sensoren.get(t).simulateOneMinute();
        }
        i++;
        }
        connection.close();
	}
}
