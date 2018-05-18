package agent;
import java.io.IOException;
//import java.io.PrintWriter;
import java.net.*;
import java.util.*;


public class Agent implements Runnable{
	int agency;
	int number;
	String[]  names;
	int port;
	boolean catched = false;
	List<String> secrets = new ArrayList<>();
	List<Boolean> secretBetray = new ArrayList<>();
	List<String> newSecrets = new ArrayList<>();
	
	
	
	public Agent(String[] names, int agency, int number, String secret) {
		this.names = names;
		this.agency = agency;
		this.number = number;
		this.secrets.add(secret);
		this.secretBetray.add(false);
	}

	

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		System.out.println("Start "+ names[0] + " agent");
		Thread cli = new Thread(new Client(this));
		Thread ser = new Thread(new Server(this));

		ser.start();
		
		cli.start();
		
		try {
			ser.join();
			cli.join();
		} catch (InterruptedException e) {
		}
		
		
	}

}




