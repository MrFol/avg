package simulation;

import java.util.LinkedList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import aktor.Aktor;
import avg_co2_sensor.Sensorik;
import steuerwerk.Steuerwerk;

public class Simulation {
	
	private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private Session sessionSteuerwerk;
	private Session sessionAktor;
	LinkedList<Sensorik> sensoren = new LinkedList<Sensorik>();
	LinkedList<Aktor> aktoren = new LinkedList<Aktor>();
	Steuerwerk steuerwerk;
	
	public Simulation(String url) throws JMSException {
		this.connectionFactory = new ActiveMQConnectionFactory(url);
		this.connection = this.connectionFactory.createConnection();
		connection.start();
		
		this.session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE); 
		
		this.sessionSteuerwerk = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE); 
		
		this.sessionAktor = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE); 
	}
	
	public void addSensoren(String[] name, int[] anzahl_studis) throws JMSException {
        for(int i = 0; i < name.length; i++) {
        	Destination destination = session.createQueue(name[i]);
        	MessageProducer producer = session.createProducer(destination);
        	Sensorik sensorik = new Sensorik(name[i], session, producer, anzahl_studis[i]);
        	sensoren.add(sensorik);
        }
	}
	
	public void addAktoren(String[] name) throws JMSException {
		for (int i = 0; i< name.length; i++) {
			Destination destination = sessionAktor.createQueue(name[i]);
			MessageConsumer consumer = sessionAktor.createConsumer(destination);
			Aktor aktor = new Aktor(name[i], consumer);
			aktoren.add(aktor);
		}
	}
	
	public void addSteuerwerk(String[] producers, String[] consumers) throws JMSException {
		LinkedList<MessageConsumer> consumersList = new LinkedList<MessageConsumer>();
		LinkedList<MessageProducer> producersList = new LinkedList<MessageProducer>(); 
		//Bad Practise
		for (int i = 0; i < consumers.length; i++) {
			Destination destinationConsumer = session.createQueue(consumers[i]);
			MessageConsumer consumer = session.createConsumer(destinationConsumer);
			consumersList.add(consumer);
			Destination destinationProducer = session.createQueue(producers[i]);
			MessageProducer producer = session.createProducer(destinationProducer);
			producersList.add(producer);
		}
		this.steuerwerk = new Steuerwerk(this.sessionSteuerwerk, consumersList, producersList);
	}
	
	public void runSimulation() throws JMSException {
		for (int t = 0; t < sensoren.size();t++) {
        	sensoren.get(t).simulateOneMinute();
        }
		steuerwerk.simulateOneMinute();
		for (int t = 0; t < aktoren.size(); t++) {
			boolean isOpen = aktoren.get(t).simulateOneMinute();
			sensoren.get(t).setWindow(isOpen);
		}
	}
	
	public void closeConnection() throws JMSException {
		connection.close();
	}
}
