package steuerwerk;

import java.util.LinkedList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Steuerwerk {
	
	private Session session;
	private LinkedList<MessageConsumer> consumers = new LinkedList<MessageConsumer>();
	private LinkedList<MessageProducer> producers = new LinkedList<MessageProducer>(); 
	
	public Steuerwerk(Session session, LinkedList<MessageConsumer> consumers, LinkedList<MessageProducer> producers) {
		this.session = session;
		this.consumers = consumers;
		this.producers = producers;
	}
	
	public void simulateOneMinute() throws JMSException {
 		for (int i = 0; i < consumers.size(); i++) {
 			Message messageConsume = consumers.get(i).receive();
 			if (messageConsume instanceof TextMessage) {
 				TextMessage textmessageConsume = (TextMessage) messageConsume;
 				int value = Integer.parseInt(textmessageConsume.getText());
 				if (value > maxValue_ppm) {
 					TextMessage textmessageProduce = session.createTextMessage("" + OPEN);
 					producers.get(i).send(textmessageProduce);
 				}
 				else if (value == 420) {
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
