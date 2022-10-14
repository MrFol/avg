package simulation;

import java.util.LinkedList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import aktor.Aktor;
import sensor.Sensorik;
import steuerwerk.Steuerwerk;

/**
 * 
 *
 */
public class Simulation {
	
	private ConnectionFactory connectionFactory;
	private Connection connection;
	/**
	 * Session über die die Sensoren senden
	 */
	private Session sessionSensorik;
	/**
	 * Session über die das Steuerwerk sendet
	 */
	private Session sessionSteuerwerk;
	/**
	 * Session über die die Aktoren senden
	 */
	private Session sessionAktor;
	LinkedList<Sensorik> sensoren = new LinkedList<Sensorik>();
	LinkedList<Aktor> aktoren = new LinkedList<Aktor>();
	Steuerwerk steuerwerk;
	
	/**
	 * Konstruktor
	 * @param url der Apache ActiveMQ MoM
	 * @throws JMSException
	 */
	public Simulation(String url) throws JMSException {
		this.connectionFactory = new ActiveMQConnectionFactory(url);
		this.connection = this.connectionFactory.createConnection();
		connection.start();
		
		this.sessionSensorik = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE); 
		
		this.sessionSteuerwerk = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE); 
		
		this.sessionAktor = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE); 
	}
	
	/**
	 * Hinzufügen von Sensoren für die Simulation
	 * @param name String für die Namen der Sensoren
	 * @param anzahl_studis String für die Anzahl der Studenten in Räumen
	 * @throws JMSException
	 */
	public void addSensoren(String[] name, int[] anzahl_studis) throws JMSException {
        for(int i = 0; i < name.length; i++) {
        	Destination destination = sessionSensorik.createQueue(name[i]);
        	MessageProducer producer = sessionSensorik.createProducer(destination);
        	Sensorik sensorik = new Sensorik(name[i], sessionSensorik, producer, anzahl_studis[i]);
        	sensoren.add(sensorik);
        }
	}
	
	/**
	 * Hinzufügen von Aktoren für die Simulation
	 * @param name String für die Namen der Aktöre
	 * @throws JMSException
	 */
	public void addAktoren(String[] name) throws JMSException {
		for (int i = 0; i< name.length; i++) {
			Destination destination = sessionAktor.createQueue(name[i]);
			MessageConsumer consumer = sessionAktor.createConsumer(destination);
			Aktor aktor = new Aktor(name[i], consumer);
			aktoren.add(aktor);
		}
	}
	
	/**
	 * Hinzufügen des Steuerwerks
	 * @param producers String für die Namen der Aktoren
	 * @param consumers String für die Namen der Sensoren
	 * @throws JMSException
	 */
	public void addSteuerwerk(String[] producers, String[] consumers) throws JMSException {
		LinkedList<MessageConsumer> consumersList = new LinkedList<MessageConsumer>();
		LinkedList<MessageProducer> producersList = new LinkedList<MessageProducer>(); 
		//Bad Practise
		for (int i = 0; i < consumers.length; i++) {
			Destination destinationConsumer = sessionSteuerwerk.createQueue(consumers[i]);
			MessageConsumer consumer = sessionSteuerwerk.createConsumer(destinationConsumer);
			consumersList.add(consumer);
			Destination destinationProducer = sessionSteuerwerk.createQueue(producers[i]);
			MessageProducer producer = sessionSteuerwerk.createProducer(destinationProducer);
			producersList.add(producer);
		}
		this.steuerwerk = new Steuerwerk(this.sessionSteuerwerk, consumersList, producersList);
	}
	
	/**
	 * Ausführen eines Durchlaufes der Simulation
	 * @throws JMSException
	 */
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
