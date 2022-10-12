package avg_co2_sensor;

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

public class Sensorik {
	
	private String name;
	private Session session;
	private MessageProducer producer;
	private int anzahl_studenten;
	private int co2_ppm = 420;
	private boolean openWindow = false;
	
	public Sensorik(String name, Session session, MessageProducer producer, int anzahl_studenten) {
		this.name = name;
		this.session = session;
		this.producer = producer;
		this.anzahl_studenten = anzahl_studenten;
	}
	
	public Sensorik(String name, Session session, MessageProducer producer, int anzahl_studenten, int co2_ppm) {
		this.name = name;
		this.session = session;
		this.producer = producer;
		this.anzahl_studenten = anzahl_studenten;
		this.co2_ppm = co2_ppm;
	}
	
	public void setCo2PPM(int ppm) {
		this.co2_ppm = ppm;
	}
	
	public void setWindow(boolean openWindow) {
		this.openWindow = openWindow;
	}
	
	public void simulateOneMinute() throws JMSException {
		if (openWindow) {
			co2_ppm = co2_ppm - 100;
			if (co2_ppm < 420) {
				co2_ppm = 420;
			}
		}
		else {
			int co2 = anzahl_studenten * co2prokopf;
			co2_ppm = co2_ppm + co2;
		}
		TextMessage message = session.createTextMessage("" + co2_ppm);
		producer.send(message);
		System.out.println(this.name + ": One Minute passed. New Value = " + co2_ppm);
	}
	
	public static int co2prokopf = 25;
	
	
}
