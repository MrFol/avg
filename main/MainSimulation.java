package main;

import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
 
import org.apache.activemq.ActiveMQConnection;

import simulation.Simulation;

/**
 * Main Klasse der Simulation
 *
 */
public class MainSimulation {

	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	private static String[] subjectSensor = {"Sensor1", "Sensor2", "Sensor3"};
	private static String[] subjectAktor = {"Aktor1", "Aktor2", "Aktor3"};
	private static int[] anzahl_studis = {10, 5, 30};
	private static int runs = 10;
	
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
			TimeUnit.SECONDS.sleep(1);
		}
		sim.closeConnection();
	}
}
