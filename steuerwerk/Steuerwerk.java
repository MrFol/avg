package steuerwerk;

import java.util.LinkedList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Simuliert das Steuerwerk, sendet und empfängt Narichten aus einer Queue
 *
 */
public class Steuerwerk {
	
	private Session session;
	private LinkedList<MessageConsumer> consumers = new LinkedList<MessageConsumer>();
	private LinkedList<MessageProducer> producers = new LinkedList<MessageProducer>(); 
	
	/**
	 * Konstruktor
	 * @param session Session mit der sich das Steuerwerk zur Queue verbindet
	 * @param consumers Liste an Consumers welche Narichten aus der Queue empfangen die Sensoren gesendet haben
	 * @param producers Liste an Produders welche Narichten an die Queue senden die von Aktoren empfangen werden 
	 */
	public Steuerwerk(Session session, LinkedList<MessageConsumer> consumers, LinkedList<MessageProducer> producers) {
		this.session = session;
		this.consumers = consumers;
		this.producers = producers;
	}
	
	/**
	 * Simuliert eine Minute für das Steuerwerk
	 * Wenn der maximal Wert für einen Raum an CO2 überschritten wird sendet das Steuerwerk eine Naricht für das Öffnen eines Fensters
	 * Wenn der Wert für einen Raum an CO2 421 CO2 PPM unterschreitet sendet das Steuerwerk eine Naricht für das Schließen eines Fensters
	 * @throws JMSException
	 */
	public void simulateOneMinute() throws JMSException {
 		for (int i = 0; i < consumers.size(); i++) {
 			Message messageConsume = consumers.get(i).receiveNoWait();
 			if (messageConsume instanceof TextMessage) {
 				TextMessage textmessageConsume = (TextMessage) messageConsume;
 				int value = Integer.parseInt(textmessageConsume.getText());
 				if (value > maxValue_ppm) {
 					TextMessage textmessageProduce = session.createTextMessage("" + OPEN);
 					producers.get(i).send(textmessageProduce);
 				}
 				else if (value <= 420) {
 					TextMessage textmessageProduce = session.createTextMessage("" + CLOSE);
 					producers.get(i).send(textmessageProduce);
 				}
 			}
 		}
	}
	
	private final static int maxValue_ppm = 1000;
	private final static int OPEN = 1;
	private final static int CLOSE = 0;
}
