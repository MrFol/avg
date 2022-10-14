package aktor;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import steuerwerk.Steuerwerk;

public class Aktor {
	
	private String name;
	private MessageConsumer consumer;
	private boolean openWindow = false;
	
	public Aktor(String name, MessageConsumer consumer) {
		this.name = name;
		this.consumer = consumer;
	}
	
	public boolean simulateOneMinute() throws JMSException {
		Message message = consumer.receiveNoWait();
		if (message instanceof TextMessage) {
			TextMessage textmessage = (TextMessage) message;
			int value = Integer.parseInt(textmessage.getText());
			if (value == OPEN && openWindow == false) {
				openWindow = true;
				System.out.println(this.name + ": Fenster geöffnet");
			}
			else if (value == CLOSE && openWindow == true) {
				openWindow = false;
				System.out.println(this.name + ": Fenster geschlossen");
			}
		}
		return openWindow;
	}
	
	private final static int OPEN = 1;
	private final static int CLOSE = 0;
}
