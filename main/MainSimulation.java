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
import simulation.Simulation;

public class MainSimulation {

	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static String[] subjectSensor = {"Sensor1", "Sensor2", "Sensor3"};
	private static String[] subjectAktor = {"Aktor1", "Aktor2", "Aktor3"};
	private static int[] anzahl_studis = {10, 5, 30};
	private static int runs = 15;
	
	public static void main(String[] args) throws JMSException, InterruptedException {
		Simulation sim = new Simulation(url);
		sim.addSensoren(subjectSensor, anzahl_studis);
		sim.addSteuerwerk(subjectAktor, subjectSensor);
		sim.addAktoren(subjectAktor);
		int i = 0;
		while (i < runs) {
			System.out.println("Run: " + i);
			sim.runSimulation();
			i++;
			TimeUnit.SECONDS.sleep(2);
		}
		sim.closeConnection();
	}
}
