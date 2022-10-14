package sensor;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage; 

/**
 * Simuliert einen Sensor für einen Raum der Narichten an eine Queue sendet
 *
 */
public class Sensorik {
	
	private String name;
	private Session session;
	private MessageProducer producer;
	private int anzahl_studenten;
	private int co2_ppm = 420;
	private boolean openWindow = false;
	
	/**
	 * Konstruktor
	 * @param name Name des Sensors
	 * @param session Session mit der sich der Sensor zur Queue verbindet
	 * @param producer MessageProducer welcher die Narichten an die Queue sendet
	 * @param anzahl_studenten Die Anzahl der Studenten im Raum
	 */
	public Sensorik(String name, Session session, MessageProducer producer, int anzahl_studenten) {
		this.name = name;
		this.session = session;
		this.producer = producer;
		this.anzahl_studenten = anzahl_studenten;
	}
	
	/**
	 * Konstruktor
	 * @param name Name des Sensors
	 * @param session Session mit der sich der Sensor zur Queue verbindet
	 * @param producer MessageProducer welcher die Narichten an die Queue sendet
	 * @param anzahl_studenten Die Anzahl der Studenten im Raum
	 * @param co2_ppm Startwert des Co2 Gehaltes im Raum
	 */
	public Sensorik(String name, Session session, MessageProducer producer, int anzahl_studenten, int co2_ppm) {
		this.name = name;
		this.session = session;
		this.producer = producer;
		this.anzahl_studenten = anzahl_studenten;
		this.co2_ppm = co2_ppm;
	}
	
	/**
	 * Simuliert eine Minute der Daten im Raum und sendet diese an die Queue abhängig von der Anzahl der Studenten im Raum, des Ausstoßes Pro Kopf und ob das Fenster offen oder geschlossen ist
	 * @throws JMSException
	 */
	public void simulateOneMinute() throws JMSException {
		if (openWindow) {
			co2_ppm = co2_ppm - 90;
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
	
	public void setCo2PPM(int ppm) {
		this.co2_ppm = ppm;
	}
	
	public void setWindow(boolean openWindow) {
		this.openWindow = openWindow;
	}
	
	/**
	 * Ausstoß pro Kopf im Raum
	 */
	public static int co2prokopf = 20;
	
	
}
