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

import avg_co2_sensor.Sensorik;
import steuerwerk.Steuerwerk;

public class Simulation {
	
	private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private Session sessionSteuerwerk;
	
	LinkedList<Sensorik> sensoren = new LinkedList<Sensorik>();
	Steuerwerk steuerwerk;
	
	public Simulation(String url) throws JMSException {
		this.connectionFactory = new ActiveMQConnectionFactory(url);
		this.connection = this.connectionFactory.createConnection();
		connection.start();
		
		this.session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE); 
		
		this.sessionSteuerwerk = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE); 
	}
	
	public void addSensoren(String[] subject, int[] anzahl_studis) throws JMSException {
        for(int i = 0; i < subject.length; i++) {
        	Destination destination = session.createQueue(subject[i]);
        	MessageProducer producer = session.createProducer(destination);
        	Sensorik sensorik = new Sensorik(subject[i], session, producer, anzahl_studis[i]);
        	sensoren.add(sensorik);
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
	}
	
	public void closeConnection() throws JMSException {
		connection.close();
	}
}
